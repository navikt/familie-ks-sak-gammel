package no.nav.familie.ks.sak.app.integrasjon;

import no.nav.familie.http.client.NavHttpHeaders;
import no.nav.familie.ks.kontrakter.oppgave.Oppgave;
import no.nav.familie.ks.kontrakter.oppgave.OppgaveKt;
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
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;


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

        return getRestTemplate().exchange(uri, HttpMethod.GET, httpEntity, clazz);
    }

    private <T> ResponseEntity<T> postRequest(URI uri , String requestBody, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(NavHttpHeaders.NAV_CALLID.asString(), MDC.get(MDCConstants.MDC_CALL_ID));

        return getRestTemplate().exchange(uri, HttpMethod.POST, new HttpEntity<>(requestBody, headers), responseType);
    }

    private <T> ResponseEntity<T> requestMedPersonIdent(URI uri, String personident, Class<T> clazz) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(NavHttpHeaders.NAV_CALLID.asString(), MDC.get(MDCConstants.MDC_CALL_ID));
        headers.add(NavHttpHeaders.NAV_PERSONIDENT.asString(), personident);

        HttpEntity httpEntity = new HttpEntity(headers);

        return getRestTemplate().exchange(uri, HttpMethod.GET, httpEntity, clazz);
    }

    private <T> ResponseEntity<T> requestMedAktørId(URI uri, String aktørId, Class<T> clazz) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(NavHttpHeaders.NAV_CALLID.asString(), MDC.get(MDCConstants.MDC_CALL_ID));
        headers.add("Nav-Aktorid", aktørId);

        HttpEntity httpEntity = new HttpEntity(headers);

        return getRestTemplate().exchange(uri, HttpMethod.GET, httpEntity, clazz);
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
            throw new OppslagException("Ukjent feil ved oppslag mot '" + uri + "'.", e, uri, personident);
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
            throw new OppslagException("Ukjent feil ved oppslag mot '" + uri + "'.", e, uri, aktørId);
        }
    }

    @Retryable(
        value = { OppslagException.class },
        maxAttempts = 3,
        backoff = @Backoff(delay = 5000))
    public ResponseEntity<Tilgang> sjekkTilgangTilPerson(String personident) {
        if (personident == null) {
            throw new OppslagException("Ved sjekking av tilgang: personident er null");
        }
        URI uri = URI.create(oppslagServiceUri + "/tilgang/person");
        logger.info("Sjekker tilgang  " + oppslagServiceUri);
        try {
            ResponseEntity<Tilgang> response = requestMedPersonIdent(uri, personident, Tilgang.class);
            secureLogger.info("Saksbehandler {} forsøker å få tilgang til {} med resultat {}", oidcUtil.getClaim("preferred_username"), personident, response.getBody());
            return response;
        } catch (RestClientException e) {
            throw new OppslagException("Ukjent feil ved oppslag mot '" + uri + "'.", e, uri, personident);
        }
    }

    @Retryable(
        value = { OppslagException.class },
        maxAttempts = 3,
        backoff = @Backoff(delay = 5000))
    public AktivKontantstøtteInfo hentInfoOmLøpendeKontantstøtteForBarn(String personident) {
        if (personident == null || personident.isEmpty()) {
            throw new OppslagException("Personident null eller tom");
        }
        URI uri = URI.create(oppslagServiceUri + "/infotrygd/harBarnAktivKontantstotte");
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
        value = { OppslagException.class },
        maxAttempts = 3,
        backoff = @Backoff(delay = 5000))
    public PersonhistorikkInfo hentHistorikkFor(String personident, LocalDate fødselsdato) {
        final var fom = fødselsdato;
        final var tom = LocalDate.now();
        URI uri = URI.create(oppslagServiceUri + "/personopplysning/historikk?fomDato=" + formaterDato(fom) + "&tomDato=" + formaterDato(tom));
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
            throw new OppslagException("Ukjent feil ved oppslag mot '" + uri + "'.", e, uri, personident);
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
            throw new OppslagException("Ukjent feil ved oppslag mot '" + uri + "'.", e, uri, personIdent);
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
            throw new OppslagException("Ukjent feil ved oppslag mot '" + uri + "'.", e, uri, aktørId.getId());
        }
    }

    @Retryable(
        value = { OppslagException.class },
        maxAttempts = 3,
        backoff = @Backoff(delay = 5000))
    public String oppdaterGosysOppgave(String fnr, String journalpostID, String beskrivelse) {
        URI uri = URI.create(oppslagServiceUri + "/oppgave/oppdater");
        logger.info("Sender \"oppdater oppgave\"-request til " + uri);
        Oppgave oppgave = new Oppgave(hentAktørId(fnr).getId(), journalpostID, null, beskrivelse);
        return sendOppgave(oppgave, uri, String.class);
    }

    private <T> T sendOppgave(Oppgave request, URI uri, Class<T> responsType) {
        try {
            ResponseEntity<T> response = postRequest(uri, OppgaveKt.toJson(request), responsType);

            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                String feilmelding = Optional.ofNullable(response.getHeaders().getFirst("message")).orElse("Ingen feilmelding");
                logger.warn("Kall mot oppslag feilet ved oppdatering av Gosys-oppgave: " + feilmelding);
                throw new OppslagException(feilmelding);
            }
        } catch (RestClientException e) {
            throw new OppslagException("Ukjent feil ved oppslag mot '" + uri + "'.", e, uri, request.getAktorId());
        }
    }

    private String formaterDato(LocalDate date) {
        return date.format(DateTimeFormatter.ISO_DATE);
    }
}
