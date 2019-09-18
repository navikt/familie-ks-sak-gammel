package no.nav.familie.ks.sak.app.integrasjon;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.familie.http.client.HttpClientUtil;
import no.nav.familie.http.client.NavHttpHeaders;
import no.nav.familie.http.sts.StsRestClient;
import no.nav.familie.ks.sak.app.behandling.domene.typer.AktørId;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.OppslagException;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.PersonIdent;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.PersonhistorikkInfo;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.Personinfo;
import no.nav.familie.log.mdc.MDCConstants;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class OppslagTjeneste {

    private static final Logger logger = LoggerFactory.getLogger(OppslagTjeneste.class);
    private static final Logger secureLogger = LoggerFactory.getLogger("secureLogger");
    private URI oppslagServiceUri;
    private HttpClient client;
    private StsRestClient stsRestClient;
    private ObjectMapper mapper;

    @Autowired
    public OppslagTjeneste(@Value("${FAMILIE_KS_OPPSLAG_API_URL}") URI oppslagServiceUri,
                           StsRestClient stsRestClient,
                           ObjectMapper objectMapper) {
        this.oppslagServiceUri = oppslagServiceUri;
        this.stsRestClient = stsRestClient;
        this.mapper = objectMapper;
        this.client = HttpClientUtil.create();
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
            .header(NavHttpHeaders.NAV_PERSONIDENT.asString(), personident)
            .GET()
            .build();
    }

    private HttpRequest requestMedAktørId(URI uri, String aktørId) {
        return HttpRequest.newBuilder()
            .uri(uri)
            .header("Authorization", "Bearer " + stsRestClient.getSystemOIDCToken())
            .header(NavHttpHeaders.NAV_CALLID.asString(), MDC.get(MDCConstants.MDC_CALL_ID))
            .header("Nav-Aktorid", aktørId)
            .GET()
            .build();
    }

    public AktørId hentAktørId(String personident) {
        if (personident == null || personident.isEmpty()) {
            throw new OppslagException("Ved henting av aktør id er personident null eller tom");
        }
        URI uri = URI.create(oppslagServiceUri + "/aktoer");
        logger.info("Henter aktørId fra " + oppslagServiceUri);
        try {
            HttpResponse<String> response = client.send(requestMedPersonident(uri, personident), HttpResponse.BodyHandlers.ofString());
            secureLogger.info("Vekslet inn fnr: {} til aktørId: {}", personident, response.body());

            if (response.statusCode() != HttpStatus.OK.value()) {
                logger.warn("Kall mot oppslag feilet ved uthenting av aktørId: " + response.body());
                throw new OppslagException(response.body());
            } else {
                String aktørId = mapper.readValue(response.body(), String.class);
                if (aktørId == null || aktørId.isEmpty()) {
                    throw new OppslagException("AktørId fra oppslagstjenesten er tom");
                } else {
                    return new AktørId(aktørId);
                }
            }
        } catch (IOException | InterruptedException e) {
            secureLogger.info("Ukjent feil ved oppslag mot {}. {}", uri, e.getMessage());
            logger.warn("Ukjent feil ved oppslag mot '" + uri + "'.");
            throw new OppslagException("Ukjent feil ved oppslag mot '" + uri + "'.");
        }
    }

    public PersonIdent hentPersonIdent(String aktørId) {
        if (aktørId == null || aktørId.isEmpty()) {
            throw new OppslagException("Ved henting av personident er aktørId null eller tom");
        }
        URI uri = URI.create(oppslagServiceUri + "/aktoer/fraaktorid");
        logger.info("Henter fnr fra " + oppslagServiceUri);
        try {
            HttpResponse<String> response = client.send(requestMedAktørId(uri, aktørId), HttpResponse.BodyHandlers.ofString());
            secureLogger.info("Vekslet inn aktørId: {} til fnr: {}", aktørId, response.body());

            if (response.statusCode() != HttpStatus.OK.value()) {
                logger.warn("Kall mot oppslag feilet ved uthenting av fnr.");
                secureLogger.info("Kall mot oppslag feilet ved uthenting av fnr: " + response.body());
                throw new OppslagException(response.body());
            } else {
                String personIdent = response.body();
                if (personIdent == null || personIdent.isEmpty()) {
                    throw new OppslagException("personIdent fra oppslagstjenesten er tom");
                } else {
                    return new PersonIdent(personIdent);
                }
            }
        } catch (IOException | InterruptedException e) {
            secureLogger.info("Ukjent feil ved oppslag mot {}. {}", uri, e.getMessage());
            logger.warn("Ukjent feil ved oppslag mot '" + uri + "'.");
            throw new OppslagException("Ukjent feil ved oppslag mot '" + uri + "'.");
        }
    }

    public PersonhistorikkInfo hentHistorikkFor(AktørId aktørId) {
        final var iDag = LocalDate.now();
        URI uri = URI.create(oppslagServiceUri + "/personopplysning/historikk?id=" + aktørId.getId() + "&fomDato=" + formaterDato(iDag.minusYears(6)) + "&tomDato=" + formaterDato(iDag));
        logger.info("Henter personhistorikkInfo fra " + oppslagServiceUri);
        try {
            HttpResponse<String> response = client.send(request(uri), HttpResponse.BodyHandlers.ofString());
            secureLogger.info("Personhistorikk for {}: {}", aktørId, response.body());

            if (response.statusCode() != HttpStatus.OK.value()) {
                logger.warn("Kall mot oppslag feilet ved uthenting av historikk: " + response.body());
                throw new OppslagException(response.body());
            } else {
                return mapper.readValue(response.body(), PersonhistorikkInfo.class);
            }
        } catch (IOException | InterruptedException e) {
            secureLogger.info("Ukjent feil ved oppslag mot {}. {}", uri, e.getMessage());
            logger.warn("Ukjent feil ved oppslag mot '" + uri + "'.");
            throw new OppslagException("Ukjent feil ved oppslag mot '" + uri + "'.");
        }
    }

    public Personinfo hentPersoninfoFor(AktørId aktørId) {
        URI uri = URI.create(oppslagServiceUri + "/personopplysning/info?id=" + aktørId.getId());
        logger.info("Henter personinfo fra " + oppslagServiceUri);
        try {
            HttpResponse<String> response = client.send(request(uri), HttpResponse.BodyHandlers.ofString());
            secureLogger.info("Personinfo for {}: {}", aktørId, response.body());

            if (response.statusCode() != HttpStatus.OK.value()) {
                logger.warn("Kall mot oppslag feilet ved uthenting av personinfo: " + response.body());
                throw new OppslagException(response.body());
            } else {
                return mapper.readValue(response.body(), Personinfo.class);
            }
        } catch (IOException | InterruptedException e) {
            secureLogger.info("Ukjent feil ved oppslag mot {}. {}", uri, e.getMessage());
            logger.warn("Ukjent feil ved oppslag mot '" + uri + "'.");
            throw new OppslagException("Ukjent feil ved oppslag mot '" + uri + "'.");
        }
    }

    private String formaterDato(LocalDate date) {
        return date.format(DateTimeFormatter.ISO_DATE);
    }
}
