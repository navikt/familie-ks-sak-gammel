package no.nav.familie.ks.sak.app.integrasjon;

import no.finn.unleash.Unleash;
import no.nav.familie.http.sts.StsRestClient;
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
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.FDATException;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
    classes = {ApplicationConfig.class})
@DataJpaTest(excludeAutoConfiguration = FlywayAutoConfiguration.class)
@ActiveProfiles("dev")
public class RegisterInnhentingServiceTest {

    @MockBean
    private OppslagTjeneste oppslagTjeneste;
    @MockBean
    private StsRestClient stsRestClient;
    @Autowired
    private RegisterInnhentingService tjeneste;
    @Autowired
    private FagsakRepository fagsakRepository;
    @Autowired
    private PersonopplysningService personopplysningService;
    @Autowired
    private BehandlingRepository behandlingRepository;
    @MockBean
    private Unleash unleash;

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
        when(oppslagTjeneste.hentHistorikkFor(eq(søker.getIdent()), eq(søkerPersoninfo.getFødselsdato()))).thenReturn(søkerPersonhistorikk);

        when(oppslagTjeneste.hentPersonIdent(annenPartPersoninfo.getAktørId().getId())).thenReturn(annenPartPersoninfo.getPersonIdent());
        when(oppslagTjeneste.hentAktørId(eq(annenPartPersoninfo.getPersonIdent().getIdent()))).thenReturn(annenPartAktørId);
        when(oppslagTjeneste.hentPersoninfoFor(eq(annenPart.getIdent()))).thenReturn(annenPartPersoninfo);
        when(oppslagTjeneste.hentHistorikkFor(eq(annenPart.getIdent()), eq(annenPartPersoninfo.getFødselsdato()))).thenReturn(annenPartPersonhistorikk);
    }

    @Test
    public void skal_lagre_ned_respons() throws FDATException {
        PersonIdent barn = tpsFakta.getBarna().get(0).getPersoninfo().getPersonIdent();
        AktørId barnAktørId = tpsFakta.getBarna().get(0).getPersoninfo().getAktørId();
        Personinfo barnPersoninfo = tpsFakta.getBarna().get(0).getPersoninfo();
        PersonhistorikkInfo barnPersonhistorikk = tpsFakta.getBarna().get(0).getPersonhistorikkInfo();

        when(oppslagTjeneste.hentAktørId(eq(barnPersoninfo.getPersonIdent().getIdent()))).thenReturn(barnAktørId);
        when(oppslagTjeneste.hentPersoninfoFor(eq(barn.getIdent()))).thenReturn(barnPersoninfo);
        when(oppslagTjeneste.hentHistorikkFor(eq(barn.getIdent()), eq(barnPersoninfo.getFødselsdato()))).thenReturn(barnPersonhistorikk);

        final var fagsak = Fagsak.opprettNy(søkerAktørId, søkerPersoninfo.getPersonIdent(), "123412341234");
        fagsakRepository.save(fagsak);

        final var behandling = Behandling.forFørstegangssøknad(fagsak, "12345678").build();
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
    public void skal_lagre_ned_respons_uten_annen_part() throws FDATException {
        final Faktagrunnlag faktagrunnlagUtenAnnenPart = FaktagrunnlagTestBuilder.aleneForelderNorskStatsborgerskapUtenBarnehage();
        final TpsFakta tpsFaktaUtenAnnenPart = faktagrunnlagUtenAnnenPart.getTpsFakta();

        AktørId barnAktørId = tpsFaktaUtenAnnenPart.getBarna().get(0).getPersoninfo().getAktørId();
        PersonIdent barnPersonIdent = tpsFaktaUtenAnnenPart.getBarna().get(0).getPersoninfo().getPersonIdent();
        Personinfo barnPersoninfo = tpsFaktaUtenAnnenPart.getBarna().get(0).getPersoninfo();
        PersonhistorikkInfo barnPersonhistorikk = tpsFaktaUtenAnnenPart.getBarna().get(0).getPersonhistorikkInfo();

        when(oppslagTjeneste.hentAktørId(eq(barnPersoninfo.getPersonIdent().getIdent()))).thenReturn(barnAktørId);
        when(oppslagTjeneste.hentPersoninfoFor(eq(barnPersonIdent.getIdent()))).thenReturn(barnPersoninfo);
        when(oppslagTjeneste.hentHistorikkFor(eq(barnPersonIdent.getIdent()), eq(barnPersoninfo.getFødselsdato()))).thenReturn(barnPersonhistorikk);

        final var fagsak = Fagsak.opprettNy(søkerAktørId, søkerPersoninfo.getPersonIdent(), "123412341234");
        fagsakRepository.save(fagsak);

        final var behandling = Behandling.forFørstegangssøknad(fagsak, "12345678").build();
        behandlingRepository.save(behandling);

        tjeneste.innhentPersonopplysninger(behandling, SøknadTestdata.norskFamilieUtenAnnenPartOgUtenBarnehageplass());

        final var personopplysningGrunnlag = personopplysningService.hentHvisEksisterer(behandling);

        assert personopplysningGrunnlag.isPresent();
        final var registerVersjonOpt = personopplysningGrunnlag.get().getRegistrertePersoner();
        assertThat(registerVersjonOpt).isPresent();

        assertThat(personopplysningGrunnlag.get().getAnnenPart()).isNull();
    }

    @Test(expected = FDATException.class)
    public void skal_kaste_exception_for_relasjoner_med_FDAT() throws FDATException {
        final Faktagrunnlag faktagrunnlagUtenlandskFamilie = FaktagrunnlagTestBuilder.familieUtenlandskStatsborgerskapMedBarnehage();
        final TpsFakta tpsFaktaUtenlandskFamilie = faktagrunnlagUtenlandskFamilie.getTpsFakta();

        AktørId barnAktørId = tpsFaktaUtenlandskFamilie.getBarna().get(0).getPersoninfo().getAktørId();
        PersonIdent barnPersonIdent = tpsFaktaUtenlandskFamilie.getBarna().get(0).getPersoninfo().getPersonIdent();
        Personinfo barnPersoninfo = tpsFaktaUtenlandskFamilie.getBarna().get(0).getPersoninfo();
        PersonhistorikkInfo barnPersonhistorikk = tpsFaktaUtenlandskFamilie.getBarna().get(0).getPersonhistorikkInfo();

        PersonIdent søker = tpsFaktaUtenlandskFamilie.getForelder().getPersoninfo().getPersonIdent();
        AktørId søkerAktørId = tpsFaktaUtenlandskFamilie.getForelder().getPersoninfo().getAktørId();
        Personinfo søkerPersoninfo = tpsFaktaUtenlandskFamilie.getForelder().getPersoninfo();
        PersonhistorikkInfo søkerPersonhistorikk = tpsFaktaUtenlandskFamilie.getForelder().getPersonhistorikkInfo();

        when(oppslagTjeneste.hentAktørId(eq(søkerPersoninfo.getPersonIdent().getIdent()))).thenReturn(søkerAktørId);
        when(oppslagTjeneste.hentPersoninfoFor(eq(søker.getIdent()))).thenReturn(søkerPersoninfo);
        when(oppslagTjeneste.hentHistorikkFor(eq(søker.getIdent()), eq(søkerPersoninfo.getFødselsdato()))).thenReturn(søkerPersonhistorikk);
        when(oppslagTjeneste.hentAktørId(eq(barnPersoninfo.getPersonIdent().getIdent()))).thenReturn(barnAktørId);
        when(oppslagTjeneste.hentPersoninfoFor(eq(barnPersonIdent.getIdent()))).thenReturn(barnPersoninfo);
        when(oppslagTjeneste.hentHistorikkFor(eq(barnPersonIdent.getIdent()), eq(barnPersoninfo.getFødselsdato()))).thenReturn(barnPersonhistorikk);

        final var fagsak = Fagsak.opprettNy(søkerAktørId, søker);
        fagsakRepository.save(fagsak);

        final var behandling = Behandling.forFørstegangssøknad(fagsak, "12345678").build();
        behandlingRepository.save(behandling);

        tjeneste.innhentPersonopplysninger(behandling, SøknadTestdata.utenlandskFamilieMedBarnehageplass());
    }
}
