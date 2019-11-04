package no.nav.familie.ks.sak.app.Rest;

import no.nav.familie.ks.sak.ApplicationTestPropertyValues;
import no.nav.familie.ks.sak.DevLauncher;
import no.nav.familie.ks.sak.FaktagrunnlagTestBuilder;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.barnehagebarn.BarnehageBarnGrunnlagRepository;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.søknad.SøknadGrunnlagRepository;
import no.nav.familie.ks.sak.app.behandling.domene.typer.AktørId;
import no.nav.familie.ks.sak.app.integrasjon.OppslagTjeneste;
import no.nav.familie.ks.sak.app.rest.RestFagsakService;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DevLauncher.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = DevLauncher.class, initializers = { FagsakControllerTest.Initializer.class })
public class FagsakControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    RestTemplate restTemplate;

    @MockBean
    private OppslagTjeneste oppslagTjeneste;

    @Autowired
    private RestFagsakService restFagsakService;

    @Autowired
    private SøknadGrunnlagRepository søknadGrunnlagRepository;
    @Autowired
    private BarnehageBarnGrunnlagRepository barnehageBarnGrunnlagRepository;


    @ClassRule
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest");

    @Before
    public void setUp() throws Exception {
        Mockito.when(oppslagTjeneste.hentAktørId(ArgumentMatchers.any())).thenAnswer(i -> new AktørId("121111111111111111111111111111111111111111111111111111"));
        when(oppslagTjeneste.hentPersoninfoFor(any())).thenReturn(FaktagrunnlagTestBuilder.faktaBeggeForeldreOgBarnNorskStatsborger().getForelder().getPersoninfo(),
            FaktagrunnlagTestBuilder.faktaBeggeForeldreOgBarnNorskStatsborger().getAnnenForelder().getPersoninfo(),
            FaktagrunnlagTestBuilder.faktaBeggeForeldreOgBarnNorskStatsborger().getBarn().getPersoninfo());
        when(oppslagTjeneste.hentHistorikkFor(any(), any())).thenReturn(FaktagrunnlagTestBuilder.faktaBeggeForeldreOgBarnNorskStatsborger().getForelder().getPersonhistorikkInfo(),
            FaktagrunnlagTestBuilder.faktaBeggeForeldreOgBarnNorskStatsborger().getAnnenForelder().getPersonhistorikkInfo(),
            FaktagrunnlagTestBuilder.faktaBeggeForeldreOgBarnNorskStatsborger().getBarn().getPersonhistorikkInfo());
    }

    @Test //Integrasjonstest / json api kontrakt test
    public void test_at_behandling_ikke_blir_persistert_hvis_feil_fra_oppslags() throws Exception {

        //final var søknad = FaktagrunnlagTestBuilder.familieNorskStatsborgerskapMedBarnehage();

        //restTemplate.getForObject(createURLWithPort("/local/cookie"), Object.class);

        //HttpEntity<Søknad> request = new HttpEntity<Søknad>(søknad);


        //restTemplate.postForObject(createURLWithPort("/api/mottak/dokument"), request, ResponseEntity.class);

        //assertThat(restFagsakService.hentFagsaker().size()).isLessThan(1);

    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }


    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {

            ApplicationTestPropertyValues.using(postgreSQLContainer)
                .applyTo(configurableApplicationContext.getEnvironment());

        }

    }


}
