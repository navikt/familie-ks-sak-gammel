package no.nav.familie.ks.sak.app.integrasjon;

import no.nav.familie.ks.kontrakter.søknad.testdata.SøknadTestdata;
import no.nav.familie.ks.sak.FaktagrunnlagTestBuilder;
import no.nav.familie.ks.sak.app.behandling.domene.Behandling;
import no.nav.familie.ks.sak.app.behandling.domene.BehandlingRepository;
import no.nav.familie.ks.sak.app.behandling.domene.Fagsak;
import no.nav.familie.ks.sak.app.behandling.domene.FagsakRepository;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.personopplysning.Person;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.personopplysning.PersonopplysningService;
import no.nav.familie.ks.sak.app.behandling.domene.typer.AktørId;
import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.grunnlag.TpsFakta;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.PersonIdent;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.PersonhistorikkInfo;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.Personinfo;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
    classes = {ApplicationConfig.class},
    loader = AnnotationConfigContextLoader.class)
@DataJpaTest(excludeAutoConfiguration = FlywayAutoConfiguration.class)
public class RegisterInnhentingServiceTest {

    @MockBean
    private OppslagTjeneste oppslagTjeneste;
    @Autowired
    private RegisterInnhentingService tjeneste;
    @Autowired
    private FagsakRepository fagsakRepository;
    @Autowired
    private PersonopplysningService personopplysningService;
    @Autowired
    private BehandlingRepository behandlingRepository;

    private final Faktagrunnlag faktagrunnlag = FaktagrunnlagTestBuilder.beggeForeldreBorINorgeOgErNorskeStatsborgere();
    private final TpsFakta tpsFakta = faktagrunnlag.getTpsFakta();

    private PersonIdent søker = tpsFakta.getForelder().getPersoninfo().getPersonIdent();
    private AktørId søkerAktørId = tpsFakta.getForelder().getPersoninfo().getAktørId();
    private Personinfo søkerPersoninfo = tpsFakta.getForelder().getPersoninfo();
    private PersonhistorikkInfo søkerPersonhistorikk = tpsFakta.getForelder().getPersonhistorikkInfo();

    private PersonIdent annenPart = tpsFakta.getAnnenForelder().getPersoninfo().getPersonIdent();
    private AktørId annenPartAktørId = tpsFakta.getAnnenForelder().getPersoninfo().getAktørId();
    private Personinfo annenPartPersoninfo = tpsFakta.getAnnenForelder().getPersoninfo();
    private PersonhistorikkInfo annenPartPersonhistorikk = tpsFakta.getAnnenForelder().getPersonhistorikkInfo();

    @Before
    public void setUp() {
        when(oppslagTjeneste.hentPersonIdent(any())).thenReturn(søkerPersoninfo.getPersonIdent());

        when(oppslagTjeneste.hentAktørId(eq(søkerPersoninfo.getPersonIdent().getIdent()))).thenReturn(søkerAktørId);
        when(oppslagTjeneste.hentPersoninfoFor(eq(søker.getIdent()))).thenReturn(søkerPersoninfo);
        when(oppslagTjeneste.hentHistorikkFor(eq(søker.getIdent()))).thenReturn(søkerPersonhistorikk);

        when(oppslagTjeneste.hentPersonIdent(annenPartPersoninfo.getAktørId().getIdent())).thenReturn(annenPartPersoninfo.getPersonIdent());
        when(oppslagTjeneste.hentAktørId(eq(annenPartPersoninfo.getPersonIdent().getIdent()))).thenReturn(annenPartAktørId);
        when(oppslagTjeneste.hentPersoninfoFor(eq(annenPart.getIdent()))).thenReturn(annenPartPersoninfo);
        when(oppslagTjeneste.hentHistorikkFor(eq(annenPart.getIdent()))).thenReturn(annenPartPersonhistorikk);
    }

    @Test
    public void skal_lagre_ned_respons() {
        PersonIdent barn = tpsFakta.getBarna().get(0).getPersoninfo().getPersonIdent();
        AktørId barnAktørId = tpsFakta.getBarna().get(0).getPersoninfo().getAktørId();
        Personinfo barnPersoninfo = tpsFakta.getBarn().getPersoninfo();
        PersonhistorikkInfo barnPersonhistorikk = tpsFakta.getBarn().getPersonhistorikkInfo();

        when(oppslagTjeneste.hentAktørId(eq(barnPersoninfo.getPersonIdent().getIdent()))).thenReturn(barnAktørId);
        when(oppslagTjeneste.hentPersoninfoFor(eq(barn.getIdent()))).thenReturn(barnPersoninfo);
        when(oppslagTjeneste.hentHistorikkFor(eq(barn.getIdent()))).thenReturn(barnPersonhistorikk);

        final var fagsak = Fagsak.opprettNy(søkerAktørId, "123412341234");
        fagsakRepository.save(fagsak);

        final var behandling = Behandling.forFørstegangssøknad(fagsak).build();
        behandlingRepository.save(behandling);

        tjeneste.innhentPersonopplysninger(behandling, SøknadTestdata.norskFamilieUtenBarnehageplass());

        final var personopplysningGrunnlag = personopplysningService.hentHvisEksisterer(behandling);

        assert personopplysningGrunnlag.isPresent();
        final var registerVersjonOpt = personopplysningGrunnlag.get().getRegistrertePersoner();
        assertThat(registerVersjonOpt).isPresent();

        for (Person person : registerVersjonOpt.get()) {
            assertThat(person.getAdresseHistorikk()).hasSize(1);
            assertThat(person.getRelasjoner()).hasSize(2);
            assertThat(person.getStatsborgerskapHistorikk()).hasSize(1);
        }


    }

    @Test
    public void skal_lagre_ned_respons_uten_annen_part() {
        final Faktagrunnlag faktagrunnlagUtenAnnenPart = FaktagrunnlagTestBuilder.aleneForelderNorskStatsborgerskapUtenBarnehage();
        final TpsFakta tpsFaktaUtenAnnenPart = faktagrunnlagUtenAnnenPart.getTpsFakta();

        AktørId barn = tpsFaktaUtenAnnenPart.getBarn().getPersoninfo().getAktørId();
        Personinfo barnPersoninfo = tpsFaktaUtenAnnenPart.getBarn().getPersoninfo();
        PersonhistorikkInfo barnPersonhistorikk = tpsFaktaUtenAnnenPart.getBarn().getPersonhistorikkInfo();

        when(oppslagTjeneste.hentAktørId(eq(barnPersoninfo.getPersonIdent().getIdent()))).thenReturn(barn);
        when(oppslagTjeneste.hentPersoninfoFor(eq(barn.getIdent()))).thenReturn(barnPersoninfo);
        when(oppslagTjeneste.hentHistorikkFor(eq(barn.getIdent()))).thenReturn(barnPersonhistorikk);

        final var fagsak = Fagsak.opprettNy(søkerAktørId, "123412341234");
        fagsakRepository.save(fagsak);

        final var behandling = Behandling.forFørstegangssøknad(fagsak).build();
        behandlingRepository.save(behandling);

        tjeneste.innhentPersonopplysninger(behandling, SøknadTestdata.norskFamilieUtenAnnenPartOgUtenBarnehageplass());

        final var personopplysningGrunnlag = personopplysningService.hentHvisEksisterer(behandling);

        assert personopplysningGrunnlag.isPresent();
        final var registerVersjonOpt = personopplysningGrunnlag.get().getRegistrertePersoner();
        assertThat(registerVersjonOpt).isPresent();

        assertThat(personopplysningGrunnlag.get().getAnnenPart()).isNull();
    }
}
