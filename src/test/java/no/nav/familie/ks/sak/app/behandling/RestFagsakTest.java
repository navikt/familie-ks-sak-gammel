package no.nav.familie.ks.sak.app.behandling;

import no.nav.familie.http.sts.StsRestClient;
import no.nav.familie.ks.kontrakter.søknad.testdata.SøknadTestdata;
import no.nav.familie.ks.sak.FaktagrunnlagBuilder;
import no.nav.familie.ks.sak.app.behandling.domene.Behandling;
import no.nav.familie.ks.sak.app.behandling.domene.BehandlingRepository;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.personopplysning.PersonopplysningGrunnlagRepository;
import no.nav.familie.ks.sak.app.behandling.domene.typer.AktørId;
import no.nav.familie.ks.sak.app.behandling.fastsetting.FastsettingService;
import no.nav.familie.ks.sak.app.behandling.resultat.Vedtak;
import no.nav.familie.ks.sak.app.integrasjon.OppslagTjeneste;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.PersonIdent;
import no.nav.familie.ks.sak.app.rest.RestFagsakService;
import no.nav.familie.ks.sak.app.rest.behandling.RestBehandling;
import no.nav.familie.ks.sak.app.rest.behandling.RestFagsak;
import no.nav.familie.ks.sak.config.ApplicationConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

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
public class RestFagsakTest {

    @MockBean
    private OppslagTjeneste oppslagTjeneste;

    @MockBean
    private StsRestClient stsRestClient;

    @Autowired
    private RestFagsakService restFagsakService;

    @Autowired
    private BehandlingRepository behandlingRepository;

    @MockBean
    private PersonopplysningGrunnlagRepository personopplysningRepository;

    private final FastsettingService fastsettingServiceMock = mock(FastsettingService.class);

    @Autowired
    private Saksbehandling saksbehandling;

    @Before
    public void setUp() {
        when(oppslagTjeneste.hentPersonIdent(ArgumentMatchers.any())).thenAnswer(i -> new PersonIdent(String.valueOf(i.getArguments()[0])));

        when(personopplysningRepository.findByBehandlingAndAktiv(any()))
            .thenReturn(
                FaktagrunnlagBuilder.genererPersonopplysningGrunnlag(new AktørId(FaktagrunnlagBuilder.morAktørId.getId()))
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
    public void hentRestFagsak() {
        when(fastsettingServiceMock.fastsettFakta(any(), any())).thenReturn(FaktagrunnlagBuilder.familieNorskStatsborgerskapUtenBarnehage());

        final var søknad = SøknadTestdata.norskFamilieUtenBarnehageplass();
        Vedtak vedtak = saksbehandling.behandle(søknad);
        Optional<Behandling> behandling = behandlingRepository.findById(vedtak.getBehandlingsId());

        assertThat(behandling).isPresent();

        assert behandling.isPresent();
        final RestFagsak restFagsak = restFagsakService.hentRestFagsak(behandling.get().getFagsak().getId());
        assertThat(restFagsak).isNotNull();
        assertThat(restFagsak.getId()).isEqualTo(behandling.get().getFagsak().getId());

        final List<RestBehandling> restBehandlinger = restFagsak.getBehandlinger();
        assertThat(restBehandlinger).hasSize(1);
    }

    @Test
    public void hentFagsakMedBarnehageplass() {
        when(fastsettingServiceMock.fastsettFakta(any(), any())).thenReturn(FaktagrunnlagBuilder.familieNorskStatsborgerskapMedBarnehage());
        saksbehandling.behandle(SøknadTestdata.norskFamilieMedBarnehageplass());

        assertThat(restFagsakService.hentFagsaker()).hasSize(1);
    }

    @Test
    public void hentFagsakUtenBarnehageplass() {
        when(fastsettingServiceMock.fastsettFakta(any(), any())).thenReturn(FaktagrunnlagBuilder.familieNorskStatsborgerskapUtenBarnehage());
        saksbehandling.behandle(SøknadTestdata.norskFamilieUtenBarnehageplass());

        assertThat(restFagsakService.hentFagsaker()).hasSize(1);
    }
}
