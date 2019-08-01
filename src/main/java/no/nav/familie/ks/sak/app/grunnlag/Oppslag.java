package no.nav.familie.ks.sak.app.grunnlag;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import no.nav.familie.ks.sak.app.MottaSøknadController;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.OppslagException;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.AktørId;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.PersonIdent;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.PersonhistorikkInfo;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.Personinfo;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.relasjon.RelasjonsRolleType;
import no.nav.log.MDCConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;

@Component
public class Oppslag {

    private URI oppslagServiceUri;
    private HttpClient client;
    private static final Logger logger = LoggerFactory.getLogger(MottaSøknadController.class);
    public static final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public Oppslag(@Value("${FAMILIE_KS_OPPSLAG_API_URL}") URI oppslagServiceUri) {
        this.oppslagServiceUri = oppslagServiceUri;
        this.client = create();
    }

    private static HttpClient create() {
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .version(HttpClient.Version.HTTP_1_1)
                .build();
    }

    private static HttpRequest request(URI uri) {
        return HttpRequest.newBuilder()
                .uri(uri)
                .header("Nav-Call-Id", MDC.get(MDCConstants.MDC_CORRELATION_ID))
                .GET()
                .build();
    }

    public TpsFakta hentTpsFakta(Søknad søknad, String personident) {
        Forelder forelder = genererForelder(hentAktørId(personident));
        Forelder annenForelder = finnAnnenForelder(søknad);
        Personinfo barn = finnBarnSøktFor(søknad, forelder.getPersoninfo());
        return new TpsFakta.Builder()
                .medForelder(forelder)
                .medBarn(barn)
                .medAnnenForelder(annenForelder)
                .build();
    }

    private Forelder genererForelder(String aktørId) {
        return new Forelder.Builder()
                .medPersonhistorikkInfo(hentHistorikkFor(aktørId))
                .medPersoninfo(hentPersonFor(aktørId))
                .build();
    }

    private Forelder finnAnnenForelder(Søknad søknad) {
        var personident = søknad.familieforhold.annenForelderFodselsnummer;
        if (! personident.isEmpty()) {
            var aktørId = hentAktørId(personident);
            return genererForelder(aktørId);
        }
        else return null;
    }

    private Personinfo finnBarnSøktFor(Søknad søknad, Personinfo personinfo) {
        return hentPersonFor("123");
        /*
        // TODO: Returner fnr for valgt barn i tillegg til fødselsdato
        String personIdentBarn = søknad.mineBarn.fodselsdato;
        personinfo
                .getFamilierelasjoner()
                .stream()
                .filter( relasjon -> relasjon.getRelasjonsrolle().equals(RelasjonsRolleType.BARN))
                .filter( barn -> barn.getPersonIdent().equals(personIdentBarn))
                .findFirst()
                .orElseThrow(
                () -> new IllegalArgumentException("Finner ikke relasjon til barn søkt for: " + personIdentBarn));

        var aktørId = hentAktørId(personIdentBarn);
        return hentPersonFor(aktørId);
        */
    }

    private String hentAktørId(String personident){
        URI uri = URI.create(oppslagServiceUri + "/aktoer?ident=" + personident);
        try {
            HttpResponse<String> response = client.send(request(uri), HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                logger.info("Kall mot oppslag feilet: " + response.body());
                throw new OppslagException(response.body());
            } else {
                return mapper.readValue(response.body(), String.class);
            }
        } catch (IOException | InterruptedException e) {
            logger.info("Ukjent feil ved oppslag mot '" + uri + "'. " + e.getMessage());
            throw new OppslagException("Ukjent feil ved oppslag mot '" + uri + "'. " + e.getMessage());
        }
    }

    private PersonhistorikkInfo hentHistorikkFor(String aktørId) {
        URI uri = URI.create(oppslagServiceUri + "/personopplysning/historikk?id=" + aktørId);
        try {
            HttpResponse<String> response = client.send(request(uri), HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                logger.info("Kall mot oppslag feilet: " + response.body());
                throw new OppslagException(response.body());
            } else {
                return mapper.readValue(response.body(), PersonhistorikkInfo.class);
            }
        } catch (IOException | InterruptedException e) {
            logger.info("Ukjent feil ved oppslag mot '" + uri + "'. " + e.getMessage());
            throw new OppslagException("Ukjent feil ved oppslag mot '" + uri + "'. " + e.getMessage());
        }
    }

    private Personinfo hentPersonFor(String aktørId) {
        //TODO: Hent når implementert i oppslag
        return Personinfo.builder()
                .medAktørId(new AktørId("1111111111111"))
                .medPersonIdent(new PersonIdent("11111111111"))
                .medNavn("Navn Navnesen")
                .medFødselsdato(LocalDate.now())
                .build();
    }
}
