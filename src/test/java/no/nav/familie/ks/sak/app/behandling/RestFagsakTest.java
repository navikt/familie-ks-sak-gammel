package no.nav.familie.ks.sak.app.behandling;

import no.nav.familie.http.sts.StsRestClient;
import no.nav.familie.ks.kontrakter.søknad.testdata.SøknadTestdata;
import no.nav.familie.ks.sak.FaktagrunnlagTestBuilder;
import no.nav.familie.ks.sak.app.behandling.domene.Behandling;
import no.nav.familie.ks.sak.app.behandling.domene.BehandlingRepository;
import no.nav.familie.ks.sak.app.behandling.fastsetting.FastsettingService;
import no.nav.familie.ks.sak.app.behandling.resultat.Vedtak;
import no.nav.familie.ks.sak.app.grunnlag.TpsFakta;
import no.nav.familie.ks.sak.app.integrasjon.OppslagTjeneste;
import no.nav.familie.ks.sak.app.rest.RestFagsakService;
import no.nav.familie.ks.sak.app.rest.behandling.RestBehandling;
import no.nav.familie.ks.sak.app.rest.behandling.RestFagsak;
import no.nav.familie.ks.sak.config.ApplicationConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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

    private final FastsettingService fastsettingServiceMock = mock(FastsettingService.class);
    private static final String SAKSNUMMER = "TEST123";
    private static final String JOURNALPOSTID = "12345678";

    @MockBean
    private OppslagTjeneste oppslagTjeneste;

    @MockBean
    private StsRestClient stsRestClient;

    @Autowired
    private RestFagsakService restFagsakService;

    @Autowired
    private BehandlingRepository behandlingRepository;

    @Autowired
    private Saksbehandling saksbehandling;

    @Before
    public void setUp() {
        TpsFakta tpsFakta = FaktagrunnlagTestBuilder.familieNorskStatsborgerskapUtenBarnehage().getTpsFakta();

        when(oppslagTjeneste.hentAktørId(tpsFakta.getForelder().getPersoninfo().getPersonIdent().getIdent())).thenReturn(tpsFakta.getForelder().getPersoninfo().getAktørId());
        when(oppslagTjeneste.hentAktørId(tpsFakta.getAnnenForelder().getPersoninfo().getPersonIdent().getIdent())).thenReturn(tpsFakta.getAnnenForelder().getPersoninfo().getAktørId());
        when(oppslagTjeneste.hentAktørId(tpsFakta.getBarn().getPersoninfo().getPersonIdent().getIdent())).thenReturn(tpsFakta.getBarn().getPersoninfo().getAktørId());

        when(oppslagTjeneste.hentPersoninfoFor(tpsFakta.getForelder().getPersoninfo().getPersonIdent().getIdent()))
            .thenReturn(tpsFakta.getForelder().getPersoninfo());
        when(oppslagTjeneste.hentPersoninfoFor(tpsFakta.getAnnenForelder().getPersoninfo().getPersonIdent().getIdent()))
            .thenReturn(tpsFakta.getAnnenForelder().getPersoninfo());
        when(oppslagTjeneste.hentPersoninfoFor(tpsFakta.getBarn().getPersoninfo().getPersonIdent().getIdent()))
            .thenReturn(tpsFakta.getBarn().getPersoninfo());

        when(oppslagTjeneste.hentHistorikkFor(tpsFakta.getForelder().getPersoninfo().getPersonIdent().getIdent()))
            .thenReturn(tpsFakta.getForelder().getPersonhistorikkInfo());
        when(oppslagTjeneste.hentHistorikkFor(tpsFakta.getAnnenForelder().getPersoninfo().getPersonIdent().getIdent()))
            .thenReturn(tpsFakta.getAnnenForelder().getPersonhistorikkInfo());
        when(oppslagTjeneste.hentHistorikkFor(tpsFakta.getBarn().getPersoninfo().getPersonIdent().getIdent()))
            .thenReturn(tpsFakta.getBarn().getPersonhistorikkInfo());

        when(oppslagTjeneste.hentMedlemskapsUnntakFor(tpsFakta.getForelder().getPersoninfo().getAktørId()))
            .thenReturn(FaktagrunnlagTestBuilder.tomMedlemskapsinfo());
        when(oppslagTjeneste.hentMedlemskapsUnntakFor(tpsFakta.getAnnenForelder().getPersoninfo().getAktørId()))
            .thenReturn(FaktagrunnlagTestBuilder.tomMedlemskapsinfo());
    }


    @Test
    public void hentRestFagsak() {
        when(fastsettingServiceMock.fastsettFakta(any(), any(), any(), any())).thenReturn(FaktagrunnlagTestBuilder.familieNorskStatsborgerskapUtenBarnehage());

        final var søknad = SøknadTestdata.norskFamilieUtenBarnehageplass();
        Vedtak vedtak = saksbehandling.behandle(søknad, SAKSNUMMER, JOURNALPOSTID);
        Optional<Behandling> behandling = behandlingRepository.findById(vedtak.getBehandlingsId());

        assert behandling.isPresent();

        final RestFagsak restFagsak = restFagsakService.hentRestFagsak(behandling.get().getFagsak().getId());
        assertThat(restFagsak).isNotNull();
        assertThat(restFagsak.getId()).isEqualTo(behandling.get().getFagsak().getId());

        final List<RestBehandling> restBehandlinger = restFagsak.getBehandlinger();
        assertThat(restBehandlinger).hasSize(1);
    }

    @Test
    public void hentFagsaker() {
        when(fastsettingServiceMock.fastsettFakta(any(), any(), any(), any())).thenReturn(FaktagrunnlagTestBuilder.familieNorskStatsborgerskapUtenBarnehage());
        saksbehandling.behandle(SøknadTestdata.norskFamilieUtenBarnehageplass(), SAKSNUMMER, JOURNALPOSTID);

        assertThat(restFagsakService.hentFagsaker()).hasSize(1);
    }

    @Test
    public void rest_fagsak_har_tps_informasjon() {
        when(fastsettingServiceMock.fastsettFakta(any(), any(), any(), any())).thenReturn(FaktagrunnlagTestBuilder.familieNorskStatsborgerskapUtenBarnehage());
        Vedtak vedtak = saksbehandling.behandle(SøknadTestdata.norskFamilieUtenBarnehageplass(), SAKSNUMMER, JOURNALPOSTID);
        Optional<Behandling> behandling = behandlingRepository.findById(vedtak.getBehandlingsId());

        assert behandling.isPresent();
        behandling.ifPresent(behandling1 -> {
            final RestFagsak restFagsak = restFagsakService.hentRestFagsak(behandling.get().getFagsak().getId());
            assertThat(restFagsak).isNotNull();

            final var restPersoner = restFagsak.getBehandlinger().iterator().next().getPersonopplysninger();
            assertThat(restPersoner.getAnnenPart()).isNotNull();

            assertThat(restPersoner).isNotNull();
            assertThat(restPersoner.getSøker().getPersonIdent()).isEqualTo(SøknadTestdata.norskFamilieUtenBarnehageplass().getSøkerFødselsnummer());
            assertThat(restPersoner.getAnnenPart().getPersonIdent()).isEqualTo(SøknadTestdata.norskFamilieUtenBarnehageplass().getOppgittAnnenPartFødselsnummer());
        });
    }
}
