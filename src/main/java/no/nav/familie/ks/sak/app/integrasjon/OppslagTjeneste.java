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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

@Component
public class OppslagTjeneste {

    private static final Logger logger = LoggerFactory.getLogger(OppslagTjeneste.class);
    private static final Logger secureLogger = LoggerFactory.getLogger("secureLogger");
    private URI oppslagServiceUri;
    private HttpClient client;
    private StsRestClient stsRestClient;
    private RestTemplate restTemplate;
    private ObjectMapper mapper;

    @Autowired
    public OppslagTjeneste(@Value("${FAMILIE_KS_OPPSLAG_API_URL}") URI oppslagServiceUri,
                           StsRestClient stsRestClient,
                           ObjectMapper objectMapper) {
        this.oppslagServiceUri = oppslagServiceUri;
        this.stsRestClient = stsRestClient;
        this.restTemplate = new RestTemplate();
        this.mapper = objectMapper;
        this.client = HttpClientUtil.create();
    }

    private <T> ResponseEntity<T> request(URI uri, Class<T> clazz) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", "Bearer " + stsRestClient.getSystemOIDCToken());
        headers.add(NavHttpHeaders.NAV_CALLID.asString(), MDC.get(MDCConstants.MDC_CALL_ID));

        HttpEntity httpEntity = new HttpEntity(headers);

        return restTemplate.exchange(uri, HttpMethod.GET, httpEntity, clazz);
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

    @Retryable(
        value = { OppslagException.class },
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000))
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
                String feilmelding = response.headers().firstValue("message").orElse("Ingen feilmelding.");
                logger.warn("Kall mot oppslag feilet ved uthenting av aktørId: " + feilmelding);
                throw new OppslagException(feilmelding);
            } else {
                String aktørId = mapper.readValue(response.body(), String.class);
                if (aktørId == null || aktørId.isEmpty()) {
                    throw new OppslagException("AktørId fra oppslagstjenesten er tom");
                } else {
                    return new AktørId(aktørId);
                }
            }
        } catch (IOException | InterruptedException e) {
            throw new OppslagException("Ukjent feil ved oppslag mot '" + uri + "'.", e, uri);
        }
    }

    @Retryable(
        value = { OppslagException.class },
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000))
    PersonIdent hentPersonIdent(String aktørId) {
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
            throw new OppslagException("Ukjent feil ved oppslag mot '" + uri + "'.", e, uri);
        }
    }

    @Retryable(
        value = { OppslagException.class },
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000))
    public PersonhistorikkInfo hentHistorikkFor(AktørId aktørId) {
        final var iDag = LocalDate.now();
        URI uri = URI.create(oppslagServiceUri + "/personopplysning/historikk?id=" + aktørId.getId() + "&fomDato=" + formaterDato(iDag.minusYears(6)) + "&tomDato=" + formaterDato(iDag));
        logger.info("Henter personhistorikkInfo fra " + oppslagServiceUri);
        try {
            ResponseEntity<PersonhistorikkInfo> response = request(uri, PersonhistorikkInfo.class);
            secureLogger.info("Personhistorikk for {}: {}", aktørId, response.getBody());

            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                String feilmelding = Optional.ofNullable(response.getHeaders().getFirst("message")).orElse("Ingen feilmelding");
                logger.warn("Kall mot oppslag feilet ved uthenting av historikk: " + feilmelding);
                throw new OppslagException(feilmelding);
            }
        } catch (RestClientException e) {
            throw new OppslagException("Ukjent feil ved oppslag mot '" + uri + "'.", e, uri);
        }
    }

    @Retryable(
        value = { OppslagException.class },
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000))
    public Personinfo hentPersoninfoFor(AktørId aktørId) {
        URI uri = URI.create(oppslagServiceUri + "/personopplysning/info?id=" + aktørId.getId());
        logger.info("Henter personinfo fra " + oppslagServiceUri);
        try {
            ResponseEntity<Personinfo> response = request(uri, Personinfo.class);
            secureLogger.info("Personinfo for {}: {}", aktørId, response.getBody());

            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                String feilmelding = Optional.ofNullable(response.getHeaders().getFirst("message")).orElse("Ingen feilmelding");
                logger.warn("Kall mot oppslag feilet ved uthenting av personinfo: " + feilmelding);
                throw new OppslagException(feilmelding);
            }
        } catch (RestClientException e) {
            throw new OppslagException("Ukjent feil ved oppslag mot '" + uri + "'.", e, uri);
        }
    }

    private String formaterDato(LocalDate date) {
        return date.format(DateTimeFormatter.ISO_DATE);
    }
}
