package no.nav.familie.ks.sak.app.grunnlag;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import no.nav.familie.kontrakter.felles.personopplysning.Ident;
import no.nav.familie.ks.sak.app.behandling.domene.typer.AktørId;
import no.nav.familie.ks.sak.app.integrasjon.IntegrasjonTjeneste;
import no.nav.familie.ks.sak.app.integrasjon.infotrygd.domene.AktivKontantstøtteInfo;
import no.nav.familie.ks.sak.app.integrasjon.medlemskap.MedlemskapsInfo;
import no.nav.familie.ks.sak.app.integrasjon.medlemskap.PeriodeStatusÅrsak;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.PersonIdent;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.PersonhistorikkInfo;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.Personinfo;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.status.PersonstatusType;
import no.nav.familie.ks.sak.app.integrasjon.tilgangskontroll.Tilgang;
import no.nav.familie.ks.sak.config.ApplicationConfig;
import no.nav.familie.ks.sak.config.JacksonJsonConfig;
import no.nav.familie.log.NavHttpHeaders;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static no.nav.familie.kontrakter.felles.Ressurs.Companion;
import static org.assertj.core.api.Assertions.assertThat;


@ActiveProfiles(value = {"dev", "mock-oauth"})
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ApplicationConfig.class}, properties = {"FAMILIE_INTEGRASJONER_API_URL=http://localhost:28083/api"})
@AutoConfigureWireMock(port = 28083)
public class IntegrasjonTjenesteTest {

    private static final ObjectMapper MAPPER = new JacksonJsonConfig().objectMapper();
    private static final String NORGE = "NOR";
    public static final String AKTØR_ID_STRING = "1234567890";
    public static final String FNR = "fnr";

    @Before
    public void setUp() {
        WireMock.resetAllRequests();
    }

    @Autowired
    IntegrasjonTjeneste integrasjonTjeneste;

    @Test
    public void personhistorikk_deserialiseres() throws IOException {
        File personhistorikkResponseBody = new File(getFile("personhistorikk.json"));
        PersonhistorikkInfo personhistorikkInfo = MAPPER.readValue(personhistorikkResponseBody, PersonhistorikkInfo.class);
        assertThat(personhistorikkInfo.getAdressehistorikk().get(0).getAdresse().getLand()).isEqualTo(NORGE);
        assertThat(personhistorikkInfo.getStatsborgerskaphistorikk().get(0).getTilhørendeLand().getKode()).isEqualTo(NORGE);
        assertThat(personhistorikkInfo.getPersonstatushistorikk()
                                      .get(0)
                                      .getPersonstatus()).isEqualByComparingTo(PersonstatusType.BOSA);
        assertThat(personhistorikkInfo.getPersonIdent().getIdent()).isNotEmpty();
    }

    @Test
    public void medlemskapsinfo_deserialiseres() throws IOException {
        File medlemskapsinfoResponseBody = new File(getFile("medlemskapsInfo.json"));
        MedlemskapsInfo medlemskapsInfo = MAPPER.readValue(medlemskapsinfoResponseBody, MedlemskapsInfo.class);

        assertThat(medlemskapsInfo.getGyldigePerioder().size()).isEqualTo(1);
        assertThat(medlemskapsInfo.getGyldigePerioder().get(0).getPeriodeStatusÅrsak()).isNull();
        assertThat(medlemskapsInfo.getAvvistePerioder()
                                  .get(0)
                                  .getPeriodeStatusÅrsak()).isEqualTo(PeriodeStatusÅrsak.Feilregistrert);
        assertThat(medlemskapsInfo.getUavklartePerioder().get(0).isGjelderMedlemskapIFolketrygden()).isFalse();
    }

    @Test
    public void hentAktørId() throws Exception {
        stubFor(get(urlEqualTo("/api/aktoer/v1"))
                    .withHeader(NavHttpHeaders.NAV_PERSONIDENT.asString(), equalTo(FNR))
                    .willReturn(aResponse()
                                    .withHeader("Content-Type", "application/json")
                                    .withBody(MAPPER.writeValueAsString(
                                        Companion.success(Map.of("aktørId",
                                                                 AKTØR_ID_STRING))))));


        AktørId aktørId = integrasjonTjeneste.hentAktørId(FNR);
        assertThat(aktørId.getId()).isEqualTo(AKTØR_ID_STRING);
    }

    @Test
    public void hentHistorikkFor() throws Exception {
        stubFor(post(urlEqualTo("/api/personopplysning/v2/historikk?fomDato=1980-01-31&tomDato=" + LocalDate.now().format(
            DateTimeFormatter.ISO_DATE)))
                    .withRequestBody(matchingJsonPath("$.[?(@.ident == '" + FNR + "')]"))
                    .willReturn(aResponse()
                                    .withHeader("Content-Type", "application/json")
                                    .withHeader("Nav-Personident", FNR)
                                    .withBody(MAPPER.writeValueAsString(
                                        Companion.success(PersonhistorikkInfo.builder()
                                                                             .medPersonIdent(
                                                                                 PersonIdent
                                                                                     .fra(
                                                                                         FNR))
                                                                             .build())))));


        PersonhistorikkInfo personhistorikkInfo =
            integrasjonTjeneste.hentHistorikkFor(FNR, LocalDate.of(1980, Month.JANUARY, 31));
        assertThat(personhistorikkInfo.getPersonIdent().getIdent()).isEqualTo(FNR);
    }

    @Test
    public void hentPersoninfoFor() throws Exception {
        stubFor(post(urlEqualTo("/api/personopplysning/v2/info"))
                    .withRequestBody(matchingJsonPath("$.[?(@.ident == '" + FNR + "')]"))
                    .willReturn(aResponse()
                                    .withHeader("Content-Type", "application/json")
                                    .withBody(MAPPER.writeValueAsString(Companion.success(Personinfo.builder()
                                                                                                    .medAktørId(new AktørId(
                                                                                                        AKTØR_ID_STRING))
                                                                                                    .medNavn("Navn")
                                                                                                    .medFødselsdato(LocalDate.now())
                                                                                                    .medPersonIdent(
                                                                                                        PersonIdent.fra(
                                                                                                            FNR))
                                                                                                    .build())))));
        Personinfo personinfo = integrasjonTjeneste.hentPersoninfoFor(FNR);
        assertThat(personinfo.getPersonIdent().getIdent()).isEqualTo(FNR);
    }


    @Test
    public void hentMedlemskapsUnntakFor() throws Exception {
        stubFor(get(urlEqualTo("/api/medlemskap/v1/?id=" + AKTØR_ID_STRING))
                    .willReturn(aResponse()
                                    .withHeader("Content-Type", "application/json")
                                    .withBody(MAPPER.writeValueAsString(Companion.success(new MedlemskapsInfo.Builder().medPersonIdent(
                                        FNR).build())))));

        MedlemskapsInfo medlemskapsInfo = integrasjonTjeneste.hentMedlemskapsUnntakFor(new AktørId(AKTØR_ID_STRING));
        assertThat(medlemskapsInfo.getPersonIdent()).isEqualTo(FNR);
    }

    @Test
    public void hentInfoOmLøpendeKontantstøtteForBarn() throws Exception {
        stubFor(get(urlEqualTo("/api/infotrygd/v1/harBarnAktivKontantstotte"))
                    .withHeader(NavHttpHeaders.NAV_PERSONIDENT.asString(), equalTo(FNR))
                    .willReturn(aResponse()
                                    .withHeader("Content-Type", "application/json")
                                    .withBody(MAPPER.writeValueAsString(Companion.success(new AktivKontantstøtteInfo(true))))));

        AktivKontantstøtteInfo aktivKontantstøtteInfo = integrasjonTjeneste.hentInfoOmLøpendeKontantstøtteForBarn(FNR);
        assertThat(aktivKontantstøtteInfo.getHarAktivKontantstotte()).isTrue();
    }

    @Test
    public void hentPersonIdent() throws Exception {
        stubFor(get(urlEqualTo("/api/aktoer/v1/fraaktorid"))
                    .withHeader("Nav-Aktorid", equalTo(AKTØR_ID_STRING))
                    .willReturn(aResponse()
                                    .withHeader("Content-Type", "application/json")
                                    .withBody(MAPPER.writeValueAsString(Companion.success(Map.of("personIdent", FNR))))));

        PersonIdent personIdent = integrasjonTjeneste.hentPersonIdent(AKTØR_ID_STRING);
        assertThat(personIdent.getIdent()).isEqualTo(FNR);
    }

    @Test
    public void sjekkTilgangTilPerson() throws Exception {
        stubFor(get(urlEqualTo("/api/tilgang/person"))
                    .withHeader(NavHttpHeaders.NAV_PERSONIDENT.asString(), equalTo(FNR))
                    .willReturn(aResponse()
                                    .withHeader("Content-Type", "application/json")
                                    .withBody(MAPPER.writeValueAsString(new Tilgang().withHarTilgang(false)
                                                                                     .withBegrunnelse("begrunnelse")))));

        ResponseEntity<Tilgang> tilgangTilPerson = integrasjonTjeneste.sjekkTilgangTilPerson(FNR, new RestTemplate());
        assertThat(tilgangTilPerson.getBody().isHarTilgang()).isFalse();
        assertThat(tilgangTilPerson.getBody().getBegrunnelse()).isEqualTo("begrunnelse");
    }


    @Test
    public void oppdaterGosysOppgave() throws Exception {
        stubFor(get(urlEqualTo("/api/aktoer/v1"))
                    .withHeader(NavHttpHeaders.NAV_PERSONIDENT.asString(), equalTo(FNR))
                    .willReturn(aResponse()
                                    .withHeader("Content-Type", "application/json")
                                    .withBody(MAPPER.writeValueAsString(
                                        Companion.success(Map.of("aktørId",
                                                                 AKTØR_ID_STRING))))));
        stubFor(post(urlEqualTo("/api/oppgave/oppdater"))
                    .willReturn(aResponse()
                                    .withHeader("Content-Type", "application/json")
                                    .withBody(MAPPER.writeValueAsString(Companion.success(Map.of("oppgaveId", 321))))));

        integrasjonTjeneste.oppdaterGosysOppgave(FNR, "1234", "beskrivelse");
    }


    private String getFile(String filnavn) {
        return getClass().getClassLoader().getResource(filnavn).getFile();
    }
}
