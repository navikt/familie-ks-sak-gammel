package no.nav.familie.ks.sak.app.behandling.domene;

import no.finn.unleash.Unleash;
import no.nav.familie.ks.kontrakter.søknad.testdata.SøknadTestdata;
import no.nav.familie.ks.sak.ApplicationTestPropertyValues;
import no.nav.familie.ks.sak.DevLauncher;
import no.nav.familie.ks.sak.FaktagrunnlagTestBuilder;
import no.nav.familie.ks.sak.app.behandling.Saksbehandling;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.personopplysning.PersonopplysningGrunnlag;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.personopplysning.PersonopplysningGrunnlagRepository;
import no.nav.familie.ks.sak.app.behandling.domene.typer.AktørId;
import no.nav.familie.ks.sak.app.behandling.fastsetting.FastsettingService;
import no.nav.familie.ks.sak.app.behandling.resultat.Vedtak;
import no.nav.familie.ks.sak.app.integrasjon.OppslagTjeneste;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;



@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ContextConfiguration(classes = DevLauncher.class, initializers = { FagsakRepositoryTest.Initializer.class })
@Testcontainers
@DisabledIfEnvironmentVariable(named="CIRCLECI", matches="true")
public class FagsakRepositoryTest {

    @MockBean
    private OppslagTjeneste oppslagTjeneste;

    @MockBean
    private Unleash unleash;

    @Autowired
    private FagsakRepository fagsakRepository;
    @Autowired
    private BehandlingRepository behandlingRepository;
    @Autowired
    private PersonopplysningGrunnlagRepository personopplysningGrunnlagRepository;

    @Autowired
    private Saksbehandling saksbehandling;

    private final FastsettingService fastsettingServiceMock = mock(FastsettingService.class);

    @BeforeEach
    public void setUp() {
        when(fastsettingServiceMock.fastsettFakta(any(), any(), any(), any())).thenReturn(FaktagrunnlagTestBuilder.familieNorskStatsborgerskapUtenBarnehage());
        when(oppslagTjeneste.hentAktørId(ArgumentMatchers.any())).thenAnswer(i -> new AktørId(String.valueOf(i.getArguments()[0])));
        when(oppslagTjeneste.hentPersoninfoFor(any())).thenReturn(FaktagrunnlagTestBuilder.faktaBeggeForeldreOgBarnNorskStatsborger().getForelder().getPersoninfo(),
            FaktagrunnlagTestBuilder.faktaBeggeForeldreOgBarnNorskStatsborger().getAnnenForelder().getPersoninfo(),
            FaktagrunnlagTestBuilder.faktaBeggeForeldreOgBarnNorskStatsborger().getBarn().getPersoninfo());
        when(oppslagTjeneste.hentHistorikkFor(any(), any())).thenReturn(FaktagrunnlagTestBuilder.faktaBeggeForeldreOgBarnNorskStatsborger().getForelder().getPersonhistorikkInfo(),
            FaktagrunnlagTestBuilder.faktaBeggeForeldreOgBarnNorskStatsborger().getAnnenForelder().getPersonhistorikkInfo(),
            FaktagrunnlagTestBuilder.faktaBeggeForeldreOgBarnNorskStatsborger().getBarn().getPersonhistorikkInfo());

        when(oppslagTjeneste.hentMedlemskapsUnntakFor(any())).thenReturn(FaktagrunnlagTestBuilder.tomMedlemskapsinfo());
        when(oppslagTjeneste.hentMedlemskapsUnntakFor(any())).thenReturn(FaktagrunnlagTestBuilder.tomMedlemskapsinfo());
        when(oppslagTjeneste.hentInfoOmLøpendeKontantstøtteForBarn(any())).thenReturn(FaktagrunnlagTestBuilder.typiskInfotrygdFakta.getAktivKontantstøtteInfo());
    }

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest");


    @Test
    public void skal_kjøre_flyway_migreringer_og_lagre_data_i_postgresql() {

        //given
        final var søknad = SøknadTestdata.norskFamilieUtenBarnehageplass();
        final var saksnummer = "TEST123";
        Vedtak vedtak = saksbehandling.behandle(søknad, saksnummer, "1234567");

        // when
        Optional<Behandling> behandling = behandlingRepository.findById(vedtak.getBehandlingsId());
        long count = fagsakRepository.count();

        // then
        assertThat(behandling).isPresent();
        assertEquals(count, 1);

    }

    @Test
    public void skal_lagre_personer_med_historikk() {

        //given
        final var søknad = SøknadTestdata.norskFamilieMedBarnehageplass();
        final var saksnummer = "TEST123";

        // when
        Vedtak vedtak = saksbehandling.behandle(søknad, saksnummer, "1234567");

        // then
        Optional<Behandling> behandling = behandlingRepository.findById(vedtak.getBehandlingsId());

        Optional<PersonopplysningGrunnlag> personopplysningGrunnlag = personopplysningGrunnlagRepository.findByBehandlingAndAktiv(behandling.get().getId());

        assertThat(personopplysningGrunnlag.get().getSøker()).isNotNull();
        assertThat(personopplysningGrunnlag.get().getBarna()).isNotEmpty();
        assertThat(personopplysningGrunnlag.get().getAnnenPart()).isNull();

        assertThat(personopplysningGrunnlag.get().getSøker().getAdresseHistorikk()).isNotEmpty();
        assertThat(personopplysningGrunnlag.get().getBarna().get(0).getAdresseHistorikk()).isNotEmpty();

        assertThat(personopplysningGrunnlag.get().getSøker().getStatsborgerskapHistorikk()).isNotEmpty();
        assertThat(personopplysningGrunnlag.get().getBarna().get(0).getStatsborgerskapHistorikk()).isNotEmpty();

    }

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {

            ApplicationTestPropertyValues.using(postgreSQLContainer)
                .applyTo(configurableApplicationContext.getEnvironment());

        }

    }

}
