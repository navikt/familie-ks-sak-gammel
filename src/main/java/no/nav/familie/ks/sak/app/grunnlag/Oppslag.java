package no.nav.familie.ks.sak.app.grunnlag;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.OppslagException;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.AktørId;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.PersonhistorikkInfo;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.Personinfo;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.relasjon.RelasjonsRolleType;
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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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
        try {
            Optional<AktørId> annenForelder = barn.getFamilierelasjoner().stream()
                    .filter( relasjon -> foreldreRelasjoner.contains(relasjon.getRelasjonsrolle()))
                    .map( relasjon -> relasjon.getAktørId() )
                    .filter( aktørId ->  ! aktørId.equals(søker))
                    .findFirst();
            return genererForelder(annenForelder.get().getId());
        }
        catch (NullPointerException e) {
            return null;
        }
    }

    private Personinfo hentBarnSøktFor(Søknad søknad) {
        String fødselsnummer = søknad.getMineBarn().getFødselsnummer();
        String aktørId = hentAktørId(fødselsnummer);
        return hentPersoninfoFor(aktørId);
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
}
