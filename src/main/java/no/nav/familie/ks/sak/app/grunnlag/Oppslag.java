package no.nav.familie.ks.sak.app.grunnlag;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.familie.ks.sak.app.MottaSøknadController;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.OppslagException;
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

@Component
public class Oppslag {

    private URI oppslagServiceUri;
    private ObjectMapper mapper;
    private HttpClient client;
    private static final Logger logger = LoggerFactory.getLogger(MottaSøknadController.class);

    public Oppslag(@Value("${FAMILIE_KS_OPPSLAG_API_URL}") URI oppslagServiceUri) {
        this.oppslagServiceUri = oppslagServiceUri;
        this.mapper = new ObjectMapper();
        this.client = create();
    }

    public static HttpClient create() {
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .version(HttpClient.Version.HTTP_1_1)
                .build();
    }

    public TpsFakta hentTpsFakta(Søknad søknad, String personident) {

        String aktørId = hentAktørId(personident);

        Forelder forelder = genererForelder(aktørId);

        var personidentForAnnenForelder = søknad.familieforhold.annenForelderFodselsnummer;
        Forelder annenForelder = null;
        if (! personidentForAnnenForelder.isEmpty()) {
            var aktørIdAnnenForelder = hentAktørId(personidentForAnnenForelder);
            annenForelder = genererForelder(aktørIdAnnenForelder);
        }

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

    private Personinfo finnBarnSøktFor(Søknad søknad, Personinfo personinfo) {
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
    }

    private String hentAktørId(String personident){
        URI uri = URI.create(oppslagServiceUri + "/aktoer?ident=" + personident);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Nav-Call-Id", MDC.get(MDCConstants.MDC_CORRELATION_ID))
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
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
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Nav-Call-Id", MDC.get(MDCConstants.MDC_CORRELATION_ID))
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
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
        return Personinfo.builder().build();
    }
}
