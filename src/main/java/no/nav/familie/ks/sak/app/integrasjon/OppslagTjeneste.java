package no.nav.familie.ks.sak.app.integrasjon;

import no.nav.familie.http.client.NavHttpHeaders;
import no.nav.familie.http.sts.StsRestClient;
import no.nav.familie.ks.sak.app.behandling.domene.typer.AktørId;
import no.nav.familie.ks.sak.app.integrasjon.medlemskap.MedlemskapsInfo;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.OppslagException;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.PersonIdent;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.PersonhistorikkInfo;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.Personinfo;
import no.nav.familie.ks.sak.util.LocalSts;
import no.nav.familie.log.mdc.MDCConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;

import static no.nav.familie.ks.sak.app.behandling.domene.typer.Tid.TIDENES_BEGYNNELSE;
import static no.nav.familie.ks.sak.app.behandling.domene.typer.Tid.TIDENES_ENDE;

@Component
public class OppslagTjeneste {

    private static final Logger logger = LoggerFactory.getLogger(OppslagTjeneste.class);
    private static final Logger secureLogger = LoggerFactory.getLogger("secureLogger");
    private URI oppslagServiceUri;
    private StsRestClient stsRestClient;
    private RestTemplate restTemplate;

    @Autowired
    public OppslagTjeneste(@Value("${FAMILIE_KS_OPPSLAG_API_URL}") URI oppslagServiceUri,
                           RestTemplate restTemplate,
                           StsRestClient stsRestClient) {
        this.oppslagServiceUri = oppslagServiceUri;
        this.stsRestClient = stsRestClient;
        this.restTemplate = restTemplate;
    }

    private <T> ResponseEntity<T> request(URI uri, Class<T> clazz) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", "Bearer " + stsRestClient.getSystemOIDCToken());
        headers.add(NavHttpHeaders.NAV_CALLID.asString(), MDC.get(MDCConstants.MDC_CALL_ID));

        HttpEntity httpEntity = new HttpEntity(headers);

        return restTemplate.exchange(uri, HttpMethod.GET, httpEntity, clazz);
    }

    private <T> ResponseEntity<T> requestMedPersonIdent(URI uri, String personident, Class<T> clazz) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", "Bearer " + stsRestClient.getSystemOIDCToken());
        headers.add(NavHttpHeaders.NAV_CALLID.asString(), MDC.get(MDCConstants.MDC_CALL_ID));
        headers.add(NavHttpHeaders.NAV_PERSONIDENT.asString(), personident);

        HttpEntity httpEntity = new HttpEntity(headers);

        return restTemplate.exchange(uri, HttpMethod.GET, httpEntity, clazz);
    }

    private <T> ResponseEntity<T> requestMedAktørId(URI uri, String aktørId, Class<T> clazz) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", "Bearer " + stsRestClient.getSystemOIDCToken());
        headers.add(NavHttpHeaders.NAV_CALLID.asString(), MDC.get(MDCConstants.MDC_CALL_ID));
        headers.add("Nav-Aktorid", aktørId);

        HttpEntity httpEntity = new HttpEntity(headers);

        return restTemplate.exchange(uri, HttpMethod.GET, httpEntity, clazz);
    }

    @Retryable(
        value = { OppslagException.class },
        maxAttempts = 3,
        backoff = @Backoff(delay = 5000))
    public AktørId hentAktørId(String personident) {
        if (personident == null || personident.isEmpty()) {
            throw new OppslagException("Ved henting av aktør id er personident null eller tom");
        }
        URI uri = URI.create(oppslagServiceUri + "/aktoer");
        logger.info("Henter aktørId fra " + oppslagServiceUri);
        try {
            ResponseEntity<String> response = requestMedPersonIdent(uri, personident, String.class);
            secureLogger.info("Vekslet inn fnr: {} til aktørId: {}", personident, response.getBody());

            if (response.getStatusCode().is2xxSuccessful()) {
                String aktørId = response.getBody();
                if (aktørId == null || aktørId.isEmpty()) {
                    throw new OppslagException("AktørId fra oppslagstjenesten er tom");
                } else {
                    return new AktørId(aktørId);
                }
            } else {
                String feilmelding = Optional.ofNullable(response.getHeaders().getFirst("message")).orElse("Ingen feilmelding");
                logger.warn("Kall mot oppslag feilet ved uthenting av aktørId: " + feilmelding);
                throw new OppslagException(feilmelding);
            }
        } catch (RestClientException e) {
            throw new OppslagException("Ukjent feil ved oppslag mot '" + uri + "'.", e, uri);
        }
    }

    @Retryable(
        value = { OppslagException.class },
        maxAttempts = 3,
        backoff = @Backoff(delay = 5000))
    public PersonIdent hentPersonIdent(String aktørId) {
        if (aktørId == null || aktørId.isEmpty()) {
            throw new OppslagException("Ved henting av personident er aktørId null eller tom");
        }
        URI uri = URI.create(oppslagServiceUri + "/aktoer/fraaktorid");
        logger.info("Henter fnr fra " + oppslagServiceUri);
        try {
            ResponseEntity<String> response = requestMedAktørId(uri, aktørId, String.class);
            secureLogger.info("Vekslet inn aktørId: {} til fnr: {}", aktørId, response.getBody());

            if (response.getStatusCode().is2xxSuccessful()) {
                String personIdent = response.getBody();
                if (personIdent == null || personIdent.isEmpty()) {
                    throw new OppslagException("Personident fra oppslagstjenesten er tom");
                } else {
                    return new PersonIdent(personIdent);
                }
            } else {
                String feilmelding = Optional.ofNullable(response.getHeaders().getFirst("message")).orElse("Ingen feilmelding");
                logger.warn("Kall mot oppslag feilet ved uthenting av aktørId: " + feilmelding);
                throw new OppslagException(feilmelding);
            }
        } catch (RestClientException e) {
            throw new OppslagException("Ukjent feil ved oppslag mot '" + uri + "'.", e, uri);
        }
    }

    @Retryable(
        value = { OppslagException.class },
        maxAttempts = 3,
        backoff = @Backoff(delay = 5000))
    public PersonhistorikkInfo hentHistorikkFor(String personident) {
        final var iDag = LocalDate.now();
        URI uri = URI.create(oppslagServiceUri + "/personopplysning/historikk?fomDato=" + formaterDato(iDag.minusYears(6)) + "&tomDato=" + formaterDato(iDag));
        logger.info("Henter personhistorikkInfo fra " + oppslagServiceUri);
        try {
            ResponseEntity<PersonhistorikkInfo> response = requestMedPersonIdent(uri, personident, PersonhistorikkInfo.class);
            secureLogger.info("Personhistorikk for {}: {}", personident, response.getBody());

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
        backoff = @Backoff(delay = 5000))
    public Personinfo hentPersoninfoFor(String personIdent) {
        URI uri = URI.create(oppslagServiceUri + "/personopplysning/info");
        logger.info("Henter personinfo fra " + oppslagServiceUri);
        try {
            ResponseEntity<Personinfo> response = requestMedPersonIdent(uri, personIdent, Personinfo.class);
            secureLogger.info("Personinfo for {}: {}", personIdent, Objects.requireNonNull(response.getBody()).getFamilierelasjoner());

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

    @Retryable(
        value = { OppslagException.class },
        maxAttempts = 3,
        backoff = @Backoff(delay = 5000))
    public MedlemskapsInfo hentMedlemskapsUnntakFor(AktørId aktørId) {
        URI uri = URI.create(oppslagServiceUri + "/medlemskap/?id=" + aktørId.getId());
        logger.info("Henter medlemskapsUnntak fra " + oppslagServiceUri);
        try {
            ResponseEntity<MedlemskapsInfo> response = request(uri, MedlemskapsInfo.class);
            secureLogger.info("MedlemskapsInfo for {}: {}", aktørId, response.getBody());

            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                String feilmelding = Optional.ofNullable(response.getHeaders().getFirst("message")).orElse("Ingen feilmelding");
                logger.warn("Kall mot oppslag feilet ved uthenting av medlemskapsinfo: " + feilmelding);
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
