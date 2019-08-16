package no.nav.familie.ks.sak.app.grunnlag;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.OppslagException;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.AktørId;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.PersonIdent;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.PersonhistorikkInfo;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.Personinfo;
import no.nav.familie.ks.sak.app.integrasjon.sts.StsRestClient;
import no.nav.log.MDCConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class Oppslag {

    private static final Logger logger = LoggerFactory.getLogger(Oppslag.class);
    private URI oppslagServiceUri;
    private HttpClient client;
    private StsRestClient stsRestClient;
    private ObjectMapper mapper;

    @Autowired
    public Oppslag(@Value("${FAMILIE_KS_OPPSLAG_API_URL}") URI oppslagServiceUri,
                   StsRestClient stsRestClient,
                   ObjectMapper objectMapper) {
        this.oppslagServiceUri = oppslagServiceUri;
        this.stsRestClient = stsRestClient;
        this.mapper = objectMapper;
        this.client = create();
    }

    private HttpClient create() {
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .version(HttpClient.Version.HTTP_1_1)
                .build();
    }

    private HttpRequest request(URI uri) {
        return HttpRequest.newBuilder()
                .uri(uri)
                .header("Authorization", "Bearer " + stsRestClient.getSystemOIDCToken())
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
        String personident = søknad.familieforhold.annenForelderFodselsnummer;
        if (!personident.isEmpty()) {
            String aktørId = hentAktørId(personident);
            return genererForelder(aktørId);
        }
        return null;
    }

    private Personinfo finnBarnSøktFor(Søknad søknad, Personinfo forelder) {
        return hentPersonFor("123");
        // TODO: Fnr for valgt barn bør sendes med søknad.
        // TODO: Sjekk om barn er i familierelasjon og returner personinfo for barn
    }

    private String hentAktørId(String personident) {
        URI uri = URI.create(oppslagServiceUri + "/aktoer?ident=" + personident);
        logger.info("Henter aktørId fra " + oppslagServiceUri);
        try {
            HttpResponse<String> response = client.send(request(uri), HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != HttpStatus.OK.value()) {
                logger.warn("Kall mot oppslag feilet ved uthenting av aktørId: " + response.body());
                throw new OppslagException(response.body());
            } else {
                return mapper.readValue(response.body(), String.class);
            }
        } catch (IOException | InterruptedException e) {
            logger.warn("Ukjent feil ved oppslag mot '" + uri + "'. " + e.getMessage());
            throw new OppslagException("Ukjent feil ved oppslag mot '" + uri + "'. " + e.getMessage());
        }
    }

    private PersonhistorikkInfo hentHistorikkFor(String aktørId) {
        final var iDag = LocalDate.now();
        URI uri = URI.create(oppslagServiceUri + "/personopplysning/historikk?id=" + aktørId + "&fomDato=" + formaterDato(iDag.minusYears(6)) + "&tomDato=" + formaterDato(iDag));
        logger.info("Henter personhistorikkInfo fra " + oppslagServiceUri);
        try {
            HttpResponse<String> response = client.send(request(uri), HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != HttpStatus.OK.value()) {
                logger.warn("Kall mot oppslag feilet ved uthenting av historikk: " + response.body());
                throw new OppslagException(response.body());
            } else {
                return mapper.readValue(response.body(), PersonhistorikkInfo.class);
            }
        } catch (IOException | InterruptedException e) {
            logger.warn("Ukjent feil ved oppslag mot '" + uri + "'. " + e.getMessage());
            throw new OppslagException("Ukjent feil ved oppslag mot '" + uri + "'. " + e.getMessage());
        }
    }

    private Personinfo hentPersoninfoFor(String aktørId) {
        URI uri = URI.create(oppslagServiceUri + "/personopplysning/info?id=" + aktørId );
        logger.info("Henter personinfo fra " + oppslagServiceUri);
        try {
            HttpResponse<String> response = client.send(request(uri), HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != HttpStatus.OK.value()) {
                logger.warn("Kall mot oppslag feilet ved uthenting av personinfo: " + response.body());
                throw new OppslagException(response.body());
            } else {
                return mapper.readValue(response.body(), Personinfo.class);
            }
        } catch (IOException | InterruptedException e) {
            logger.warn("Ukjent feil ved oppslag mot '" + uri + "'. " + e.getMessage());
            throw new OppslagException("Ukjent feil ved oppslag mot '" + uri + "'. " + e.getMessage());
        }
    }

    private String formaterDato(LocalDate date) {
        return date.format(DateTimeFormatter.ISO_DATE);
    }

    private Personinfo hentPersonFor(String aktørId) {
        //TODO: Hent når implementert i oppslag. Personinfo brukes foreløpig ikke i noen regler.
        return Personinfo.builder()
                .medAktørId(new AktørId("1111111111111"))
                .medPersonIdent(new PersonIdent("11111111111"))
                .medNavn("Navn Navnesen")
                .medFødselsdato(LocalDate.now())
                .build();
    }
}
