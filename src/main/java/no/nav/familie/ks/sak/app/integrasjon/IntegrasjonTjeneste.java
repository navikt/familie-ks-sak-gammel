package no.nav.familie.ks.sak.app.integrasjon;

import no.nav.familie.kontrakter.felles.Ressurs;
import no.nav.familie.kontrakter.felles.oppgave.Oppgave;
import no.nav.familie.kontrakter.felles.oppgave.Tema;
import no.nav.familie.ks.sak.app.behandling.domene.typer.AktørId;
import no.nav.familie.ks.sak.app.integrasjon.infotrygd.domene.AktivKontantstøtteInfo;
import no.nav.familie.ks.sak.app.integrasjon.medlemskap.MedlemskapsInfo;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.IntegrasjonException;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.PersonIdent;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.PersonhistorikkInfo;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.Personinfo;
import no.nav.familie.ks.sak.app.integrasjon.tilgangskontroll.Tilgang;
import no.nav.familie.ks.sak.app.rest.BaseService;
import no.nav.familie.log.NavHttpHeaders;
import no.nav.familie.sikkerhet.OIDCUtil;
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenService;
import no.nav.security.token.support.client.spring.ClientConfigurationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import static no.nav.familie.kontrakter.felles.ObjectMapperKt.getObjectMapper;

@Component
public class IntegrasjonTjeneste extends BaseService {

    private static final Logger logger = LoggerFactory.getLogger(IntegrasjonTjeneste.class);
    private static final Logger secureLogger = LoggerFactory.getLogger("secureLogger");
    private static final String OAUTH2_CLIENT_CONFIG_KEY = "integrasjoner-clientcredentials";

    private URI integrasjonerServiceUri;
    private OIDCUtil oidcUtil;

    @Autowired
    public IntegrasjonTjeneste(@Value("${FAMILIE_INTEGRASJONER_API_URL}") URI integrasjonerServiceUri,
                               RestTemplateBuilder restTemplateBuilderMedProxy,
                               ClientConfigurationProperties clientConfigurationProperties,
                               OAuth2AccessTokenService oAuth2AccessTokenService,
                               OIDCUtil oidcUtil) {
        super(OAUTH2_CLIENT_CONFIG_KEY, restTemplateBuilderMedProxy, clientConfigurationProperties, oAuth2AccessTokenService);

        this.integrasjonerServiceUri = integrasjonerServiceUri;
        this.oidcUtil = oidcUtil;
    }

    private <T> ResponseEntity<T> request(URI uri, Class<T> clazz) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        HttpEntity httpEntity = new HttpEntity(headers);

        return request(uri, HttpMethod.GET, httpEntity, clazz);
    }

    private <T> ResponseEntity<T> postRequest(URI uri, Object requestBody, Class<T> clazz) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=UTF-8");
        HttpEntity httpEntity = new HttpEntity<>(requestBody, headers);

        return request(uri, HttpMethod.POST, httpEntity, clazz);
    }

    private <T> ResponseEntity<T> requestMedPersonIdent(URI uri, String personident, Class<T> clazz) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(NavHttpHeaders.NAV_PERSONIDENT.asString(), personident);

        HttpEntity httpEntity = new HttpEntity(headers);

        return request(uri, HttpMethod.GET, httpEntity, clazz);
    }

    private <T> ResponseEntity<T> requestMedAktørId(URI uri, String aktørId, Class<T> clazz) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Nav-Aktorid", aktørId);

        HttpEntity httpEntity = new HttpEntity(headers);
        return request(uri, HttpMethod.GET, httpEntity, clazz);
    }


    private <T> ResponseEntity<T> requestUtenRessurs(RestTemplate restTemplate, URI uri, String personident, Class<T> clazz) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(NavHttpHeaders.NAV_PERSONIDENT.asString(), personident);

        HttpEntity httpEntity = new HttpEntity(headers);

        return restTemplate.exchange(uri, HttpMethod.GET, httpEntity, clazz);
    }

    private <T> ResponseEntity<T> request(URI uri, HttpMethod method, HttpEntity httpEntity, Class<T> clazz) {
        var ressursResponse = getRestTemplate().exchange(uri, method, httpEntity, Ressurs.class);
        if (ressursResponse.getBody() == null) {
            throw new IntegrasjonException("Response kan ikke være tom", null, uri, null);
        }
        Ressurs<T> ressurs = ressursResponse.getBody();

        return ResponseEntity.status(ressursResponse.getStatusCode())
                             .body(getObjectMapper().convertValue(ressurs.getData(), clazz));
    }

    @Retryable(
        value = {IntegrasjonException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 5000))
    public AktørId hentAktørId(String personident) {
        if (personident == null || personident.isEmpty()) {
            throw new IntegrasjonException("Ved henting av aktør id er personident null eller tom");
        }
        URI uri = URI.create(integrasjonerServiceUri + "/aktoer/v1");
        logger.info("Henter aktørId fra " + integrasjonerServiceUri);
        try {
            ResponseEntity<Map> response = requestMedPersonIdent(uri, personident, Map.class);
            secureLogger.info("Vekslet inn fnr: {} til aktørId: {}", personident, response.getBody());

            String aktørId = response.getBody().get("aktørId").toString();
            if (aktørId == null || aktørId.isEmpty()) {
                throw new IntegrasjonException("AktørId fra integrasjonstjenesten er tom");
            } else {
                return new AktørId(aktørId);
            }
        } catch (RestClientException e) {
            throw new IntegrasjonException("Kall mot integrasjon feilet ved uthenting av aktørId", e, uri, personident);
        }
    }


    @Retryable(
        value = {IntegrasjonException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 5000))
    public PersonIdent hentPersonIdent(String aktørId) {
        if (aktørId == null || aktørId.isEmpty()) {
            throw new IntegrasjonException("Ved henting av personident er aktørId null eller tom");
        }
        URI uri = URI.create(integrasjonerServiceUri + "/aktoer/v1/fraaktorid");
        logger.info("Henter fnr fra " + integrasjonerServiceUri);
        try {
            ResponseEntity<Map> response = requestMedAktørId(uri, aktørId, Map.class);
            secureLogger.info("Vekslet inn aktørId: {} til fnr: {}", aktørId, response.getBody());

            String personIdent = response.getBody().get("personIdent").toString();
            if (personIdent == null || personIdent.isEmpty()) {
                throw new IntegrasjonException("Personident fra integrasjonstjenesten er tom");
            } else {
                return new PersonIdent(personIdent);
            }
        } catch (RestClientException e) {
            throw new IntegrasjonException("Kall mot integrasjon feilet ved uthenting av personIdent", e, uri, aktørId);
        }
    }

    @Retryable(
        value = {IntegrasjonException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 5000))
    public ResponseEntity<Tilgang> sjekkTilgangTilPerson(String personident, RestTemplate restTemplate) {
        if (personident == null) {
            throw new IntegrasjonException("Ved sjekking av tilgang: personident er null");
        }
        URI uri = URI.create(integrasjonerServiceUri + "/tilgang/person");
        logger.info("Sjekker tilgang  " + integrasjonerServiceUri);
        try {
            ResponseEntity<Tilgang> response = requestUtenRessurs(restTemplate, uri, personident, Tilgang.class);
            secureLogger.info("Saksbehandler {} forsøker å få tilgang til {} med resultat {}",
                              oidcUtil.getClaim("preferred_username"),
                              personident,
                              response.getBody());
            return response;
        } catch (RestClientException e) {
            throw new IntegrasjonException("Ukjent feil ved integrasjon mot '" + uri + "'.", e, uri, personident);
        }
    }

    @Retryable(
        value = {IntegrasjonException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 5000))
    public AktivKontantstøtteInfo hentInfoOmLøpendeKontantstøtteForBarn(String personident) {
        if (personident == null || personident.isEmpty()) {
            throw new IntegrasjonException("Personident null eller tom");
        }
        URI uri = URI.create(integrasjonerServiceUri + "/infotrygd/v1/harBarnAktivKontantstotte");
        logger.info("Henter info om kontantstøtte fra " + integrasjonerServiceUri);
        try {
            var response = requestMedPersonIdent(uri, personident, AktivKontantstøtteInfo.class);
            var aktivKontantstøtteInfo = response.getBody();

            if (aktivKontantstøtteInfo != null && aktivKontantstøtteInfo.getHarAktivKontantstotte() != null) {
                if (aktivKontantstøtteInfo.getHarAktivKontantstotte()) {
                    secureLogger.info("Personident {}: Har løpende kontantstøtte eller er under behandling for kontantstøtte.",
                                      personident);
                    logger.info("Har løpende kontantstøtte eller er under behandling for kontantstøtte.");
                } else {
                    secureLogger.info("Kontantstøtten for {} har opphørt", personident);
                    logger.info("Kontantstøtten for barnet har opphørt");
                }
                return aktivKontantstøtteInfo;
            } else {
                logger.info("AktivKontantstøtteInfo fra integrasjonstjenesten er tom");
                return new AktivKontantstøtteInfo(false);
            }
        } catch (HttpClientErrorException.NotFound e) {
            secureLogger.info("Personident ikke funnet i infotrygds kontantstøttedatabase. Personident: {}", personident);
            return new AktivKontantstøtteInfo(false);
        } catch (RestClientException e) {
            throw new IntegrasjonException("Ukjent feil ved integrasjon mot '" + uri + "'.", e, uri, personident);
        }
    }

    @Retryable(
        value = {IntegrasjonException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 5000))
    public PersonhistorikkInfo hentHistorikkFor(String personident, LocalDate fødselsdato) {
        final var fom = fødselsdato;
        final var tom = LocalDate.now();
        URI uri = URI.create(
            integrasjonerServiceUri + "/personopplysning/v1/historikk?fomDato=" + formaterDato(fom) + "&tomDato=" +
            formaterDato(tom));
        logger.info("Henter personhistorikkInfo fra " + integrasjonerServiceUri);
        try {
            ResponseEntity<PersonhistorikkInfo> response = requestMedPersonIdent(uri, personident, PersonhistorikkInfo.class);
            secureLogger.info("Personhistorikk for {}: {}", personident, response.getBody());
            return response.getBody();
        } catch (RestClientException e) {
            throw new IntegrasjonException("Kall mot integrasjon feilet ved uthenting av historikk", e, uri, personident);
        }
    }

    @Retryable(
        value = {IntegrasjonException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 5000))
    public Personinfo hentPersoninfoFor(String personIdent) {
        URI uri = URI.create(integrasjonerServiceUri + "/personopplysning/v1/info");
        logger.info("Henter personinfo fra " + integrasjonerServiceUri);
        try {
            ResponseEntity<Personinfo> response = requestMedPersonIdent(uri, personIdent, Personinfo.class);
            secureLogger.info("Personinfo for {}: {}", personIdent, response.getBody());
            return response.getBody();
        } catch (RestClientException e) {
            throw new IntegrasjonException("Kall mot integrasjon feilet ved uthenting av personinfo", e, uri, personIdent);
        }
    }

    @Retryable(
        value = {IntegrasjonException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 5000))
    public MedlemskapsInfo hentMedlemskapsUnntakFor(AktørId aktørId) {
        URI uri = URI.create(integrasjonerServiceUri + "/medlemskap/v1/?id=" + aktørId.getId());
        logger.info("Henter medlemskapsUnntak fra " + integrasjonerServiceUri);
        try {
            ResponseEntity<MedlemskapsInfo> response = request(uri, MedlemskapsInfo.class);
            secureLogger.info("MedlemskapsInfo for {}: {}", aktørId, response.getBody());
            return response.getBody();
        } catch (RestClientException e) {
            throw new IntegrasjonException("Kall mot integrasjon feilet ved uthenting av medlemskapsinfo",
                                           e,
                                           uri,
                                           aktørId.getId());
        }
    }

    @Retryable(
        value = {IntegrasjonException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 5000))
    public void oppdaterGosysOppgave(String fnr, String journalpostID, String beskrivelse) {
        URI uri = URI.create(integrasjonerServiceUri + "/oppgave/oppdater");
        logger.info("Sender \"oppdater oppgave\"-request til " + uri);
        Oppgave oppgave = new Oppgave(hentAktørId(fnr).getId(), journalpostID, null, beskrivelse, Tema.KON);
        try {
            postRequest(uri, oppgave, Map.class);
        } catch (HttpClientErrorException.NotFound e) {
            logger.warn("Oppgave returnerte 404, men kaster ikke feil. Uri: {}", uri);
        } catch (RestClientException e) {
            throw new IntegrasjonException("Kan ikke oppdater Gosys-oppgave", e, uri, oppgave.getAktorId());
        }
    }

    private String formaterDato(LocalDate date) {
        return date.format(DateTimeFormatter.ISO_DATE);
    }
}
