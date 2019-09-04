package no.nav.familie.ks.sak.app.grunnlag;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.familie.http.client.NavHttpHeaders;
import no.nav.familie.http.sts.StsRestClient;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.OppslagException;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.AktørId;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.PersonhistorikkInfo;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.Personinfo;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.relasjon.Familierelasjon;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.relasjon.RelasjonsRolleType;
import no.nav.familie.log.mdc.MDCConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
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
import java.util.*;

@Component
public class OppslagTjeneste {

    private static final Logger logger = LoggerFactory.getLogger(OppslagTjeneste.class);
    private URI oppslagServiceUri;
    private HttpClient client;
    private StsRestClient stsRestClient;
    private ObjectMapper mapper;
    private Environment env;

    @Autowired
    public OppslagTjeneste(@Value("${FAMILIE_KS_OPPSLAG_API_URL}") URI oppslagServiceUri,
                           StsRestClient stsRestClient,
                           ObjectMapper objectMapper,
                           Environment env) {
        this.oppslagServiceUri = oppslagServiceUri;
        this.stsRestClient = stsRestClient;
        this.mapper = objectMapper;
        this.client = create();
        this.env = env;
    }

    private boolean erDevProfil() {
        return Arrays.stream(env.getActiveProfiles()).anyMatch(profile -> profile.equalsIgnoreCase("dev"));
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
                .header(NavHttpHeaders.NAV_CALLID.asString(), MDC.get(MDCConstants.MDC_CALL_ID))
                .GET()
                .build();
    }

    private HttpRequest requestMedPersonident(URI uri, String personident) {
        return HttpRequest.newBuilder()
            .uri(uri)
            .header("Authorization", "Bearer " + stsRestClient.getSystemOIDCToken())
            .header(NavHttpHeaders.NAV_CALLID.asString(), MDC.get(MDCConstants.MDC_CALL_ID))
            .header("Nav-Personident", personident)
            .GET()
            .build();
    }

    public TpsFakta hentTpsFakta(Søknad søknad) {
        Forelder forelder = genererForelder(hentAktørId(søknad.person.fnr));
        Personinfo barn = hentBarnSøktFor(søknad);
        Forelder annenForelder = hentAnnenForelder(barn, forelder);
        return new TpsFakta.Builder()
                .medForelder(forelder)
                .medBarn(barn)
                .medAnnenForelder(annenForelder)
                .build();
    }

    private Forelder genererForelder(String aktørId) {
        return new Forelder.Builder()
                .medPersonhistorikkInfo(hentHistorikkFor(aktørId))
                .medPersoninfo(hentPersoninfoFor(aktørId))
                .build();
    }

    private Forelder hentAnnenForelder(Personinfo barn, Forelder forelder) {
        Set<RelasjonsRolleType> foreldreRelasjoner = new HashSet<>(Arrays.asList(RelasjonsRolleType.FARA, RelasjonsRolleType.MEDMOR, RelasjonsRolleType.MORA));
        AktørId søker = forelder.getPersoninfo().getAktørId();

        Optional<AktørId> annenForelder = barn.getFamilierelasjoner().stream()
                .filter( relasjon -> foreldreRelasjoner.contains(relasjon.getRelasjonsrolle()))
                .map(Familierelasjon::getAktørId)
                .filter( aktørId ->  ! aktørId.equals(søker))
                .findFirst();

        return annenForelder.map(aktørId -> genererForelder(aktørId.getId())).orElse(null);
    }

    private Personinfo hentBarnSøktFor(Søknad søknad) {
        String fødselsnummer = søknad.getMineBarn().getFødselsnummer();
        String aktørId = hentAktørId(fødselsnummer);
        return hentPersoninfoFor(aktørId);
    }

    public String hentAktørId(String personident) {
        if (erDevProfil()) {
            return personident;
        }

        if (personident == null || personident.isEmpty()) {
            return null;
        }
        URI uri = URI.create(oppslagServiceUri + "/aktoer");
        logger.info("Henter aktørId fra " + oppslagServiceUri);
        try {
            HttpResponse<String> response = client.send(requestMedPersonident(uri, personident), HttpResponse.BodyHandlers.ofString());
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
        URI uri = URI.create(oppslagServiceUri + "/personopplysning/info?id=" + aktørId);
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
}
