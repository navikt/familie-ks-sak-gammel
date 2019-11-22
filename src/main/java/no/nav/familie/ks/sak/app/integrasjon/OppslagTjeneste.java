package no.nav.familie.ks.sak.app.integrasjon;

import no.nav.familie.http.client.NavHttpHeaders;
import no.nav.familie.ks.kontrakter.oppgave.Oppgave;
import no.nav.familie.ks.kontrakter.oppgave.OppgaveKt;
import no.nav.familie.ks.kontrakter.sak.Ressurs;
import no.nav.familie.ks.sak.app.behandling.domene.typer.AktørId;
import no.nav.familie.ks.sak.app.integrasjon.infotrygd.domene.AktivKontantstøtteInfo;
import no.nav.familie.ks.sak.app.integrasjon.medlemskap.MedlemskapsInfo;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.OppslagException;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.PersonIdent;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.PersonhistorikkInfo;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.Personinfo;
import no.nav.familie.ks.sak.app.integrasjon.tilgangskontroll.Tilgang;
import no.nav.familie.ks.sak.app.rest.BaseService;
import no.nav.familie.log.mdc.MDCConstants;
import no.nav.familie.sikkerhet.OIDCUtil;
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenService;
import no.nav.security.token.support.client.spring.ClientConfigurationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;

@Component
public class OppslagTjeneste extends BaseService {

    private static final Logger logger = LoggerFactory.getLogger(OppslagTjeneste.class);
    private static final Logger secureLogger = LoggerFactory.getLogger("secureLogger");
    private static final String OAUTH2_CLIENT_CONFIG_KEY = "ks-oppslag-clientcredentials";

    private URI oppslagServiceUri;
    private OIDCUtil oidcUtil;

    @Autowired
    public OppslagTjeneste(@Value("${FAMILIE_KS_OPPSLAG_API_URL}") URI oppslagServiceUri,
                           RestTemplateBuilder restTemplateBuilderMedProxy,
                           ClientConfigurationProperties clientConfigurationProperties,
                           OAuth2AccessTokenService oAuth2AccessTokenService,
                           OIDCUtil oidcUtil) {
        super(OAUTH2_CLIENT_CONFIG_KEY, restTemplateBuilderMedProxy, clientConfigurationProperties, oAuth2AccessTokenService);

        this.oppslagServiceUri = oppslagServiceUri;
        this.oidcUtil = oidcUtil;
    }

    private <T> ResponseEntity<T> request(URI uri, Class<T> clazz) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(NavHttpHeaders.NAV_CALLID.asString(), MDC.get(MDCConstants.MDC_CALL_ID));

        HttpEntity httpEntity = new HttpEntity(headers);

        return request(uri, HttpMethod.GET, clazz, httpEntity);
    }

    private <T> ResponseEntity<T> postRequest(URI uri, String requestBody, Class<T> clazz) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=UTF-8");
        headers.add(NavHttpHeaders.NAV_CALLID.asString(), MDC.get(MDCConstants.MDC_CALL_ID));
        HttpEntity httpEntity = new HttpEntity<>(requestBody, headers);

        return request(uri, HttpMethod.POST, clazz, httpEntity);
    }

    private <T> ResponseEntity<T> requestMedPersonIdent(URI uri, String personident, Class<T> clazz) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(NavHttpHeaders.NAV_CALLID.asString(), MDC.get(MDCConstants.MDC_CALL_ID));
        headers.add(NavHttpHeaders.NAV_PERSONIDENT.asString(), personident);

        HttpEntity httpEntity = new HttpEntity(headers);

        return request(uri, HttpMethod.GET, clazz, httpEntity);
    }

    private <T> ResponseEntity<T> requestMedAktørId(URI uri, String aktørId, Class<T> clazz) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(NavHttpHeaders.NAV_CALLID.asString(), MDC.get(MDCConstants.MDC_CALL_ID));
        headers.add("Nav-Aktorid", aktørId);

        HttpEntity httpEntity = new HttpEntity(headers);
        return request(uri, HttpMethod.GET, clazz, httpEntity);
    }


    private <T> ResponseEntity<T> requestUtenRessurs(RestTemplate restTemplate, URI uri, String personident, Class<T> clazz) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(NavHttpHeaders.NAV_CALLID.asString(), MDC.get(MDCConstants.MDC_CALL_ID));
        headers.add(NavHttpHeaders.NAV_PERSONIDENT.asString(), personident);

        HttpEntity httpEntity = new HttpEntity(headers);

        return restTemplate.exchange(uri, HttpMethod.GET, httpEntity, clazz);
    }

    private <T> ResponseEntity<T> request(URI uri, HttpMethod method, Class<T> clazz, HttpEntity httpEntity) {
        var ressursResponse = getRestTemplate().exchange(uri, method, httpEntity, Ressurs.class);
        if (ressursResponse.getBody() == null) {
            throw new OppslagException("Response kan ikke være tom", null, uri, null);
        }
        return ResponseEntity.status(ressursResponse.getStatusCode()).body(Objects.requireNonNull(ressursResponse.getBody()).convert(clazz));
    }

    @Retryable(
        value = {OppslagException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 5000))
    public AktørId hentAktørId(String personident) {
        if (personident == null || personident.isEmpty()) {
            throw new OppslagException("Ved henting av aktør id er personident null eller tom");
        }
        URI uri = URI.create(oppslagServiceUri + "/aktoer/v1");
        logger.info("Henter aktørId fra " + oppslagServiceUri);
        try {
            ResponseEntity<Map> response = requestMedPersonIdent(uri, personident, Map.class);
            secureLogger.info("Vekslet inn fnr: {} til aktørId: {}", personident, response.getBody());

            String aktørId = response.getBody().get("aktørId").toString();
            if (aktørId == null || aktørId.isEmpty()) {
                throw new OppslagException("AktørId fra oppslagstjenesten er tom");
            } else {
                return new AktørId(aktørId);
            }
        } catch (RestClientException e) {
            throw new OppslagException("Kall mot oppslag feilet ved uthenting av aktørId", e, uri, personident);
        }
    }


    @Retryable(
        value = {OppslagException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 5000))
    public PersonIdent hentPersonIdent(String aktørId) {
        if (aktørId == null || aktørId.isEmpty()) {
            throw new OppslagException("Ved henting av personident er aktørId null eller tom");
        }
        URI uri = URI.create(oppslagServiceUri + "/aktoer/v1/fraaktorid");
        logger.info("Henter fnr fra " + oppslagServiceUri);
        try {
            ResponseEntity<Map> response = requestMedAktørId(uri, aktørId, Map.class);
            secureLogger.info("Vekslet inn aktørId: {} til fnr: {}", aktørId, response.getBody());

            String personIdent = response.getBody().get("personIdent").toString();
            if (personIdent == null || personIdent.isEmpty()) {
                throw new OppslagException("Personident fra oppslagstjenesten er tom");
            } else {
                return new PersonIdent(personIdent);
            }
        } catch (RestClientException e) {
            throw new OppslagException("Kall mot oppslag feilet ved uthenting av personIdent", e, uri, aktørId);
        }
    }

    @Retryable(
        value = {OppslagException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 5000))
    public ResponseEntity<Tilgang> sjekkTilgangTilPerson(String personident, RestTemplate restTemplate) {
        if (personident == null) {
            throw new OppslagException("Ved sjekking av tilgang: personident er null");
        }
        URI uri = URI.create(oppslagServiceUri + "/tilgang/person");
        logger.info("Sjekker tilgang  " + oppslagServiceUri);
        try {
            ResponseEntity<Tilgang> response = requestUtenRessurs(restTemplate, uri, personident, Tilgang.class);
            secureLogger.info("Saksbehandler {} forsøker å få tilgang til {} med resultat {}", oidcUtil.getClaim("preferred_username"), personident, response.getBody());
            return response;
        } catch (RestClientException e) {
            throw new OppslagException("Ukjent feil ved oppslag mot '" + uri + "'.", e, uri, personident);
        }
    }

    @Retryable(
        value = {OppslagException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 5000))
    public AktivKontantstøtteInfo hentInfoOmLøpendeKontantstøtteForBarn(String personident) {
        if (personident == null || personident.isEmpty()) {
            throw new OppslagException("Personident null eller tom");
        }
        URI uri = URI.create(oppslagServiceUri + "/infotrygd/v1/harBarnAktivKontantstotte");
        logger.info("Henter info om kontantstøtte fra " + oppslagServiceUri);
        try {
            var response = requestMedPersonIdent(uri, personident, AktivKontantstøtteInfo.class);
            var aktivKontantstøtteInfo = response.getBody();

            if (aktivKontantstøtteInfo != null && aktivKontantstøtteInfo.getHarAktivKontantstotte() != null) {
                if (aktivKontantstøtteInfo.getHarAktivKontantstotte()) {
                    secureLogger.info("Personident {}: Har løpende kontantstøtte eller er under behandling for kontantstøtte.", personident);
                    logger.info("Har løpende kontantstøtte eller er under behandling for kontantstøtte.");
                } else {
                    secureLogger.info("Kontantstøtten for {} har opphørt", personident);
                    logger.info("Kontantstøtten for barnet har opphørt");
                }
                return aktivKontantstøtteInfo;
            } else {
                logger.info("AktivKontantstøtteInfo fra oppslagstjenesten er tom");
                return new AktivKontantstøtteInfo(false);
            }
        } catch (HttpClientErrorException.NotFound e) {
            secureLogger.info("Personident ikke funnet i infotrygds kontantstøttedatabase. Personident: {}", personident);
            return new AktivKontantstøtteInfo(false);
        } catch (RestClientException e) {
            throw new OppslagException("Ukjent feil ved oppslag mot '" + uri + "'.", e, uri, personident);
        }
    }

    @Retryable(
        value = {OppslagException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 5000))
    public PersonhistorikkInfo hentHistorikkFor(String personident, LocalDate fødselsdato) {
        final var fom = fødselsdato;
        final var tom = LocalDate.now();
        URI uri = URI.create(oppslagServiceUri + "/personopplysning/v1/historikk?fomDato=" + formaterDato(fom) + "&tomDato=" + formaterDato(tom));
        logger.info("Henter personhistorikkInfo fra " + oppslagServiceUri);
        try {
            ResponseEntity<PersonhistorikkInfo> response = requestMedPersonIdent(uri, personident, PersonhistorikkInfo.class);
            secureLogger.info("Personhistorikk for {}: {}", personident, response.getBody());
            return response.getBody();
        } catch (RestClientException e) {
            throw new OppslagException("Kall mot oppslag feilet ved uthenting av historikk", e, uri, personident);
        }
    }

    @Retryable(
        value = {OppslagException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 5000))
    public Personinfo hentPersoninfoFor(String personIdent) {
        URI uri = URI.create(oppslagServiceUri + "/personopplysning/v1/info");
        logger.info("Henter personinfo fra " + oppslagServiceUri);
        try {
            ResponseEntity<Personinfo> response = requestMedPersonIdent(uri, personIdent, Personinfo.class);
            secureLogger.info("Personinfo for {}: {}", personIdent, response.getBody());
            return response.getBody();
        } catch (RestClientException e) {
            throw new OppslagException("Kall mot oppslag feilet ved uthenting av personinfo", e, uri, personIdent);
        }
    }

    @Retryable(
        value = {OppslagException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 5000))
    public MedlemskapsInfo hentMedlemskapsUnntakFor(AktørId aktørId) {
        URI uri = URI.create(oppslagServiceUri + "/medlemskap/v1/?id=" + aktørId.getId());
        logger.info("Henter medlemskapsUnntak fra " + oppslagServiceUri);
        try {
            ResponseEntity<MedlemskapsInfo> response = request(uri, MedlemskapsInfo.class);
            secureLogger.info("MedlemskapsInfo for {}: {}", aktørId, response.getBody());
            return response.getBody();
        } catch (RestClientException e) {
            throw new OppslagException("Kall mot oppslag feilet ved uthenting av medlemskapsinfo", e, uri, aktørId.getId());
        }
    }

    @Retryable(
        value = {OppslagException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 5000))
    public void oppdaterGosysOppgave(String fnr, String journalpostID, String beskrivelse) {
        URI uri = URI.create(oppslagServiceUri + "/oppgave/v1/oppdater");
        logger.info("Sender \"oppdater oppgave\"-request til " + uri);
        Oppgave oppgave = new Oppgave(hentAktørId(fnr).getId(), journalpostID, null, beskrivelse);
        try {
            postRequest(uri, OppgaveKt.toJson(oppgave), String.class);
        } catch (HttpClientErrorException.NotFound e) {
            logger.warn("Oppgave returnerte 404, men kaster ikke feil. Uri: {}", uri);
        } catch (RestClientException e) {
            throw new OppslagException("Kan ikke oppdater Gosys-oppgave", e, uri, oppgave.getAktorId());
        }
    }

    private String formaterDato(LocalDate date) {
        return date.format(DateTimeFormatter.ISO_DATE);
    }
}
