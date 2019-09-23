package no.nav.familie.ks.sak.app.behandling;

import no.nav.familie.http.sts.StsRestClient;
import no.nav.familie.ks.sak.FaktagrunnlagBuilder;
import no.nav.familie.ks.sak.Saksbehandling;
import no.nav.familie.ks.sak.app.behandling.domene.Behandling;
import no.nav.familie.ks.sak.app.behandling.domene.BehandlingRepository;
import no.nav.familie.ks.sak.app.behandling.domene.FagsakRepository;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.barnehagebarn.BarnehageBarnGrunnlagRepository;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.personopplysning.PersonopplysningRepository;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.søknad.SøknadGrunnlagRepository;
import no.nav.familie.ks.sak.app.behandling.domene.typer.AktørId;
import no.nav.familie.ks.sak.app.behandling.fastsetting.FastsettingService;
import no.nav.familie.ks.sak.app.behandling.resultat.Vedtak;
import no.nav.familie.ks.sak.app.integrasjon.OppslagTjeneste;
import no.nav.familie.ks.sak.app.rest.Behandling.RestBehandling;
import no.nav.familie.ks.sak.app.rest.Behandling.RestFagsak;
import no.nav.familie.ks.sak.config.ApplicationConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
    classes = {ApplicationConfig.class},
    loader = AnnotationConfigContextLoader.class)
@DataJpaTest(excludeAutoConfiguration = FlywayAutoConfiguration.class)
public class BehandlingslagerServiceTest {

    @MockBean
    private OppslagTjeneste oppslagTjeneste;

    @MockBean
    private StsRestClient stsRestClient;

    @Autowired
    private BehandlingslagerService tjeneste;

    @Autowired
    private FagsakRepository fagsakRepository;
    @Autowired
    private BehandlingRepository behandlingRepository;
    @Autowired
    private SøknadGrunnlagRepository søknadGrunnlagRepository;
    @Autowired
    private BarnehageBarnGrunnlagRepository barnehageBarnGrunnlagRepository;

    @MockBean
    private PersonopplysningRepository personopplysningRepository;

    private final FastsettingService fastsettingServiceMock = mock(FastsettingService.class);

    @Autowired
    private Saksbehandling saksbehandling;

    @Before
    public void setUp() {
        when(personopplysningRepository.findByBehandlingAndAktiv(any()))
            .thenReturn(
                FaktagrunnlagBuilder.genererPersonopplysningGrunnlag(new AktørId(FaktagrunnlagBuilder.norskPersonAktør.getId()))
            );
        when(oppslagTjeneste.hentAktørId(ArgumentMatchers.any())).thenAnswer(i -> new AktørId(String.valueOf(i.getArguments()[0])));

        when(oppslagTjeneste.hentPersoninfoFor(any()))
            .thenReturn(
                FaktagrunnlagBuilder.faktaBeggeForeldreOgBarnNorskStatsborger().getForelder().getPersoninfo(),
                FaktagrunnlagBuilder.faktaBeggeForeldreOgBarnNorskStatsborger().getAnnenForelder().getPersoninfo(),
                FaktagrunnlagBuilder.faktaBeggeForeldreOgBarnNorskStatsborger().getBarn().getPersoninfo()
            );
        when(oppslagTjeneste.hentHistorikkFor(any()))
            .thenReturn(
                FaktagrunnlagBuilder.faktaBeggeForeldreOgBarnNorskStatsborger().getForelder().getPersonhistorikkInfo(),
                FaktagrunnlagBuilder.faktaBeggeForeldreOgBarnNorskStatsborger().getAnnenForelder().getPersonhistorikkInfo(),
                FaktagrunnlagBuilder.faktaBeggeForeldreOgBarnNorskStatsborger().getBarn().getPersonhistorikkInfo()
            );
    }

    @Test
    public void skal_lagre_søknad_og_hente_opp_igjen() {
        final var søknad = FaktagrunnlagBuilder.medBarnehageplass(FaktagrunnlagBuilder.norskPersonIdent.getIdent());
        Behandling nyBehandling = tjeneste.nyBehandling(søknad);
        tjeneste.trekkUtOgPersister(nyBehandling, søknad);

        final var fagsaker = fagsakRepository.findAll();
        assertThat(fagsaker).hasSize(1);
        assertThat(fagsaker.get(0).getAktørId()).isEqualTo(new AktørId(søknad.getPerson().getFnr()));
        final var behandlinger = behandlingRepository.findAll();
        assertThat(behandlinger).hasSize(1);
        final var behandling = behandlinger.get(0);
        assertThat(behandling.getFagsak().getAktørId()).isEqualTo(new AktørId(søknad.getPerson().getFnr()));
        final var søknader = søknadGrunnlagRepository.findAll();
        final var søknad1 = søknader.get(0).getSøknad();
        assertThat(søknad1.getInnsendtTidspunkt()).isEqualTo(LocalDateTime.ofInstant(søknad.innsendingsTidspunkt, ZoneId.systemDefault()));
        final var erklæring = søknad1.getErklæring();
        assertThat(erklæring).isNotNull();
        assertThat(søknad1.getUtlandsTilknytning()).isNotNull();
        assertThat(søknad1.getUtlandsTilknytning().getAktørerArbeidYtelseIUtlandet()).hasSize(2);
        assertThat(søknad1.getUtlandsTilknytning().getAktørerTilknytningTilUtlandet()).hasSize(2);
        final var barnehageBarnGrunnlagList = barnehageBarnGrunnlagRepository.findAll();
        assertThat(barnehageBarnGrunnlagList).hasSize(1);
        assertThat(barnehageBarnGrunnlagList.get(0).getFamilieforhold()).isNotNull();
    }

    @Test
    public void hentRestFagsak() {
        when(fastsettingServiceMock.fastsettFakta(any(), any())).thenReturn(FaktagrunnlagBuilder.familieNorskStatsborgerskapUtenBarnehage());

        final var søknad = FaktagrunnlagBuilder.utenBarnehageplass(FaktagrunnlagBuilder.norskPersonIdent.getIdent());
        Vedtak vedtak = saksbehandling.behandle(søknad);
        Optional<Behandling> behandling = behandlingRepository.findById(vedtak.getBehandlingsId());

        assertThat(behandling).isPresent();

        assert behandling.isPresent();
        final RestFagsak restFagsak = tjeneste.hentRestFagsak(behandling.get().getFagsak().getId());
        assertThat(restFagsak).isNotNull();
        assertThat(restFagsak.getFagsak().getId()).isEqualTo(behandling.get().getFagsak().getId());

        final List<RestBehandling> restBehandlinger = restFagsak.getBehandlinger();
        assertThat(restBehandlinger).hasSize(1);
    }

    @Test
    public void hentFagsakMedBarnehageplass() {
        when(fastsettingServiceMock.fastsettFakta(any(), any())).thenReturn(FaktagrunnlagBuilder.familieNorskStatsborgerskapMedBarnehage());
        saksbehandling.behandle(FaktagrunnlagBuilder.medBarnehageplass(FaktagrunnlagBuilder.norskPersonIdent.getIdent()));

        assertThat(tjeneste.hentFagsaker()).hasSize(1);
    }

    @Test
    public void hentFagsakUtenBarnehageplass() {
        when(fastsettingServiceMock.fastsettFakta(any(), any())).thenReturn(FaktagrunnlagBuilder.familieNorskStatsborgerskapUtenBarnehage());
        saksbehandling.behandle(FaktagrunnlagBuilder.utenBarnehageplass(FaktagrunnlagBuilder.norskPersonIdent.getIdent()));

        assertThat(tjeneste.hentFagsaker()).hasSize(1);
    }
}
