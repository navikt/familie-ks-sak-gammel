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

        when(oppslagTjeneste.hentPersonIdent(tpsFakta.getForelder().getPersoninfo().getAktørId().getId())).thenReturn(tpsFakta.getForelder().getPersoninfo().getPersonIdent());
        when(oppslagTjeneste.hentPersonIdent(tpsFakta.getAnnenForelder().getPersoninfo().getAktørId().getId())).thenReturn(tpsFakta.getAnnenForelder().getPersoninfo().getPersonIdent());
        when(oppslagTjeneste.hentPersonIdent(tpsFakta.getBarn().getPersoninfo().getAktørId().getId())).thenReturn(tpsFakta.getBarn().getPersoninfo().getPersonIdent());

        when(oppslagTjeneste.hentAktørId(tpsFakta.getForelder().getPersoninfo().getPersonIdent().getIdent())).thenReturn(tpsFakta.getForelder().getPersoninfo().getAktørId());
        when(oppslagTjeneste.hentAktørId(tpsFakta.getAnnenForelder().getPersoninfo().getPersonIdent().getIdent())).thenReturn(tpsFakta.getAnnenForelder().getPersoninfo().getAktørId());
        when(oppslagTjeneste.hentAktørId(tpsFakta.getBarn().getPersoninfo().getPersonIdent().getIdent())).thenReturn(tpsFakta.getBarn().getPersoninfo().getAktørId());

        when(oppslagTjeneste.hentPersoninfoFor(tpsFakta.getForelder().getPersoninfo().getAktørId()))
            .thenReturn(tpsFakta.getForelder().getPersoninfo());
        when(oppslagTjeneste.hentPersoninfoFor(tpsFakta.getAnnenForelder().getPersoninfo().getAktørId()))
            .thenReturn(tpsFakta.getAnnenForelder().getPersoninfo());
        when(oppslagTjeneste.hentPersoninfoFor(tpsFakta.getBarn().getPersoninfo().getAktørId()))
            .thenReturn(tpsFakta.getBarn().getPersoninfo());

        when(oppslagTjeneste.hentHistorikkFor(tpsFakta.getForelder().getPersoninfo().getAktørId()))
            .thenReturn(tpsFakta.getForelder().getPersonhistorikkInfo());
        when(oppslagTjeneste.hentHistorikkFor(tpsFakta.getAnnenForelder().getPersoninfo().getAktørId()))
            .thenReturn(tpsFakta.getAnnenForelder().getPersonhistorikkInfo());
        when(oppslagTjeneste.hentHistorikkFor(tpsFakta.getBarn().getPersoninfo().getAktørId()))
            .thenReturn(tpsFakta.getBarn().getPersonhistorikkInfo());
    }

    @Test
    public void hentRestFagsak() {
        when(fastsettingServiceMock.fastsettFakta(any(), any())).thenReturn(FaktagrunnlagTestBuilder.familieNorskStatsborgerskapUtenBarnehage());

        final var søknad = SøknadTestdata.norskFamilieUtenBarnehageplass();
        Vedtak vedtak = saksbehandling.behandle(søknad, SAKSNUMMER);
        Optional<Behandling> behandling = behandlingRepository.findById(vedtak.getBehandlingsId());

        assertThat(behandling).isPresent();

        final List<RestFagsak> restFagsaker = restFagsakService.hentRestFagsaker(behandling.get().getFagsak().getSaksnummer());
        assertThat(restFagsaker).hasSize(1);
        assertThat(restFagsaker.get(0).getId()).isEqualTo(behandling.get().getFagsak().getId());

        final List<RestBehandling> restBehandlinger = restFagsaker.get(0).getBehandlinger();
        assertThat(restBehandlinger).hasSize(1);
    }

    @Test
    public void hentFagsaker() {
        when(fastsettingServiceMock.fastsettFakta(any(), any())).thenReturn(FaktagrunnlagTestBuilder.familieNorskStatsborgerskapUtenBarnehage());
        saksbehandling.behandle(SøknadTestdata.norskFamilieUtenBarnehageplass(), SAKSNUMMER);

        assertThat(restFagsakService.hentFagsaker()).hasSize(1);
    }

    @Test
    public void rest_fagsak_har_tps_informasjon() {
        when(fastsettingServiceMock.fastsettFakta(any(), any())).thenReturn(FaktagrunnlagTestBuilder.familieNorskStatsborgerskapUtenBarnehage());
        Vedtak vedtak = saksbehandling.behandle(SøknadTestdata.norskFamilieUtenBarnehageplass(), SAKSNUMMER);
        Optional<Behandling> behandling = behandlingRepository.findById(vedtak.getBehandlingsId());

        assert behandling.isPresent();
        behandling.ifPresent(behandling1 -> {
            final List<RestFagsak> restFagsak = restFagsakService.hentRestFagsaker(behandling.get().getFagsak().getSaksnummer());
            assertThat(restFagsak).hasSize(1);

            final var restPersoner = restFagsak.get(0).getBehandlinger().iterator().next().getPersonopplysninger();

            assertThat(restPersoner).isNotNull();
            assertThat(restPersoner.getSøker().getFødselsnummer()).isEqualTo(SøknadTestdata.norskFamilieUtenBarnehageplass().getSøkerFødselsnummer());
            assertThat(restPersoner.getAnnenPart().getFødselsnummer()).isEqualTo(SøknadTestdata.norskFamilieUtenBarnehageplass().getOppgittAnnenPartFødselsnummer());
        });
    }
}
