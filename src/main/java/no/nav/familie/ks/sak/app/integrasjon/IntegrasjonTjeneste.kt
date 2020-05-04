package no.nav.familie.ks.sak.app.integrasjon

import no.nav.familie.kontrakter.felles.Ressurs
import no.nav.familie.kontrakter.felles.objectMapper
import no.nav.familie.kontrakter.felles.oppgave.Oppgave
import no.nav.familie.kontrakter.felles.oppgave.Tema
import no.nav.familie.ks.sak.app.behandling.domene.typer.AktørId
import no.nav.familie.ks.sak.app.integrasjon.infotrygd.domene.AktivKontantstøtteInfo
import no.nav.familie.ks.sak.app.integrasjon.medlemskap.MedlemskapsInfo
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.IntegrasjonException
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.PersonIdent
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.PersonhistorikkInfo
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.Personinfo
import no.nav.familie.ks.sak.app.integrasjon.tilgangskontroll.Tilgang
import no.nav.familie.ks.sak.app.rest.BaseService
import no.nav.familie.log.NavHttpHeaders
import no.nav.familie.sikkerhet.OIDCUtil
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenService
import no.nav.security.token.support.client.spring.ClientConfigurationProperties
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import java.net.URI
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Component
class IntegrasjonTjeneste @Autowired constructor(@param:Value("\${FAMILIE_INTEGRASJONER_API_URL}")
                                                 private val integrasjonerServiceUri: URI,
                                                 restTemplateBuilderMedProxy: RestTemplateBuilder?,
                                                 clientConfigurationProperties: ClientConfigurationProperties?,
                                                 oAuth2AccessTokenService: OAuth2AccessTokenService?,
                                                 private val oidcUtil: OIDCUtil) : BaseService(OAUTH2_CLIENT_CONFIG_KEY,
                                                                                               restTemplateBuilderMedProxy!!,
                                                                                               clientConfigurationProperties!!,
                                                                                               oAuth2AccessTokenService!!) {

    private fun <T> request(uri: URI, clazz: Class<T>): ResponseEntity<T> {
        val headers: MultiValueMap<String, String> = LinkedMultiValueMap()
        val httpEntity: HttpEntity<*> = HttpEntity<Any?>(headers)
        return request(uri, HttpMethod.GET, httpEntity, clazz)
    }

    private fun <T> postRequest(uri: URI, requestBody: Any, clazz: Class<T>): ResponseEntity<T> {
        val headers = HttpHeaders()
        headers.add("Content-Type", "application/json;charset=UTF-8")
        val httpEntity: HttpEntity<*> =
                HttpEntity(requestBody, headers)
        return request(uri, HttpMethod.POST, httpEntity, clazz)
    }

    private fun <T> requestMedPersonIdent(uri: URI,
                                          personident: String,
                                          clazz: Class<T>): ResponseEntity<T> {
        val headers: MultiValueMap<String, String> = LinkedMultiValueMap()
        headers.add(NavHttpHeaders.NAV_PERSONIDENT.asString(), personident)
        val httpEntity: HttpEntity<*> = HttpEntity<Any?>(headers)
        return request(uri, HttpMethod.GET, httpEntity, clazz)
    }

    private fun <T> requestMedAktørId(uri: URI, aktørId: String, clazz: Class<T>): ResponseEntity<T> {
        val headers: MultiValueMap<String, String> = LinkedMultiValueMap()
        headers.add("Nav-Aktorid", aktørId)
        val httpEntity: HttpEntity<*> = HttpEntity<Any?>(headers)
        return request(uri, HttpMethod.GET, httpEntity, clazz)
    }

    private fun <T> requestUtenRessurs(restTemplate: RestTemplate,
                                       uri: URI,
                                       personident: String,
                                       clazz: Class<T>): ResponseEntity<T> {
        val headers: MultiValueMap<String, String> = LinkedMultiValueMap()
        headers.add(NavHttpHeaders.NAV_PERSONIDENT.asString(), personident)
        val httpEntity: HttpEntity<*> = HttpEntity<Any?>(headers)
        return restTemplate.exchange(uri, HttpMethod.GET, httpEntity, clazz)
    }

    private fun <T> request(uri: URI,
                            method: HttpMethod,
                            httpEntity: HttpEntity<*>,
                            clazz: Class<T>): ResponseEntity<T> {
        val ressursResponse =
                restTemplate.exchange(uri,
                                      method,
                                      httpEntity,
                                      Ressurs::class.java)
        if (ressursResponse.body == null) {
            throw IntegrasjonException("Response kan ikke være tom", null, uri, null)
        }
        val ressurs: Ressurs<*>? = ressursResponse.body
        return ResponseEntity.status(ressursResponse.statusCode)
                .body(objectMapper.convertValue(ressurs!!.data, clazz))
    }

    @Retryable(value = [IntegrasjonException::class],
               maxAttempts = 3,
               backoff = Backoff(delay = 5000))
    fun hentAktørId(personident: String?): AktørId {
        if (personident == null || personident.isEmpty()) {
            throw IntegrasjonException("Ved henting av aktør id er personident null eller tom")
        }
        val uri = URI.create("$integrasjonerServiceUri/aktoer/v1")
        logger.info("Henter aktørId fra $integrasjonerServiceUri")
        return try {
            val response = requestMedPersonIdent(uri,
                                                 personident,
                                                 Map::class.java)
            secureLogger.info("Vekslet inn fnr: {} til aktørId: {}",
                              personident,
                              response.body)
            val aktørId = response.body["aktørId"].toString()
            if (aktørId.isEmpty()) {
                throw IntegrasjonException("AktørId fra integrasjonstjenesten er tom")
            } else {
                AktørId(aktørId)
            }
        } catch (e: RestClientException) {
            throw IntegrasjonException("Kall mot integrasjon feilet ved uthenting av aktørId", e, uri, personident)
        }
    }

    @Retryable(value = [IntegrasjonException::class],
               maxAttempts = 3,
               backoff = Backoff(delay = 5000))
    fun hentPersonIdent(aktørId: String?): PersonIdent {
        if (aktørId == null || aktørId.isEmpty()) {
            throw IntegrasjonException("Ved henting av personident er aktørId null eller tom")
        }
        val uri = URI.create("$integrasjonerServiceUri/aktoer/v1/fraaktorid")
        logger.info("Henter fnr fra $integrasjonerServiceUri")
        return try {
            val response =
                    requestMedAktørId(uri, aktørId, Map::class.java)
            secureLogger.info("Vekslet inn aktørId: {} til fnr: {}", aktørId, response.body)
            val personIdent = response.body["personIdent"].toString()
            if (personIdent.isEmpty()) {
                throw IntegrasjonException("Personident fra integrasjonstjenesten er tom")
            } else {
                PersonIdent(personIdent)
            }
        } catch (e: RestClientException) {
            throw IntegrasjonException("Kall mot integrasjon feilet ved uthenting av personIdent", e, uri, aktørId)
        }
    }

    @Retryable(value = [IntegrasjonException::class],
               maxAttempts = 3,
               backoff = Backoff(delay = 5000))
    fun sjekkTilgangTilPerson(personident: String?, restTemplate: RestTemplate): ResponseEntity<Tilgang> {
        if (personident == null) {
            throw IntegrasjonException("Ved sjekking av tilgang: personident er null")
        }
        val uri = URI.create("$integrasjonerServiceUri/tilgang/person")
        logger.info("Sjekker tilgang  $integrasjonerServiceUri")
        return try {
            val response =
                    requestUtenRessurs(restTemplate, uri, personident, Tilgang::class.java)
            secureLogger.info("Saksbehandler {} forsøker å få tilgang til {} med resultat {}",
                              oidcUtil.getClaim("preferred_username"),
                              personident,
                              response.body)
            response
        } catch (e: RestClientException) {
            throw IntegrasjonException("Ukjent feil ved integrasjon mot '$uri'.", e, uri, personident)
        }
    }

    @Retryable(value = [IntegrasjonException::class],
               maxAttempts = 3,
               backoff = Backoff(delay = 5000)) fun hentInfoOmLøpendeKontantstøtteForBarn(
            personident: String?): AktivKontantstøtteInfo {
        if (personident == null || personident.isEmpty()) {
            throw IntegrasjonException("Personident null eller tom")
        }
        val uri =
                URI.create("$integrasjonerServiceUri/infotrygd/v1/harBarnAktivKontantstotte")
        logger.info("Henter info om kontantstøtte fra $integrasjonerServiceUri")
        return try {
            val response =
                    requestMedPersonIdent(uri, personident, AktivKontantstøtteInfo::class.java)
            val aktivKontantstøtteInfo = response.body
            if (aktivKontantstøtteInfo != null && aktivKontantstøtteInfo.harAktivKontantstotte != null) {
                if (aktivKontantstøtteInfo.harAktivKontantstotte) {
                    secureLogger.info("Personident {}: Har løpende kontantstøtte eller er under behandling for kontantstøtte.",
                                      personident)
                    logger.info("Har løpende kontantstøtte eller er under behandling for kontantstøtte.")
                } else {
                    secureLogger.info("Kontantstøtten for {} har opphørt", personident)
                    logger.info("Kontantstøtten for barnet har opphørt")
                }
                aktivKontantstøtteInfo
            } else {
                logger.info("AktivKontantstøtteInfo fra integrasjonstjenesten er tom")
                AktivKontantstøtteInfo(false)
            }
        } catch (e: HttpClientErrorException.NotFound) {
            secureLogger.info("Personident ikke funnet i infotrygds kontantstøttedatabase. Personident: {}",
                              personident)
            AktivKontantstøtteInfo(false)
        } catch (e: RestClientException) {
            throw IntegrasjonException("Ukjent feil ved integrasjon mot '$uri'.", e, uri, personident)
        }
    }

    @Retryable(value = [IntegrasjonException::class],
               maxAttempts = 3,
               backoff = Backoff(delay = 5000))
    fun hentHistorikkFor(personident: String, fødselsdato: LocalDate): PersonhistorikkInfo {
        val tom = LocalDate.now()
        val uri = URI.create(
                integrasjonerServiceUri.toString() + "/personopplysning/v1/historikk?fomDato="
                + formaterDato(fødselsdato) + "&tomDato=" +
                formaterDato(tom))
        logger.info("Henter personhistorikkInfo fra $integrasjonerServiceUri")
        return try {
            val response =
                    requestMedPersonIdent(uri, personident, PersonhistorikkInfo::class.java)
            secureLogger.info("Personhistorikk for {}: {}", personident, response.body)
            response.body
        } catch (e: RestClientException) {
            throw IntegrasjonException("Kall mot integrasjon feilet ved uthenting av historikk", e, uri, personident)
        }
    }

    @Retryable(value = [IntegrasjonException::class],
               maxAttempts = 3,
               backoff = Backoff(delay = 5000))
    fun hentPersoninfoFor(personIdent: String): Personinfo {
        val uri = URI.create("$integrasjonerServiceUri/personopplysning/v1/info")
        logger.info("Henter personinfo fra $integrasjonerServiceUri")
        return try {
            val response = requestMedPersonIdent(uri, personIdent, Personinfo::class.java)
            secureLogger.info("Personinfo for {}: {}", personIdent, response.body)
            response.body
        } catch (e: RestClientException) {
            throw IntegrasjonException("Kall mot integrasjon feilet ved uthenting av personinfo", e, uri, personIdent)
        }
    }

    @Retryable(value = [IntegrasjonException::class],
               maxAttempts = 3,
               backoff = Backoff(delay = 5000))
    fun hentMedlemskapsUnntakFor(aktørId: AktørId): MedlemskapsInfo {
        val uri = URI.create(integrasjonerServiceUri.toString() + "/medlemskap/v1/?id=" + aktørId.id)
        logger.info("Henter medlemskapsUnntak fra $integrasjonerServiceUri")
        return try {
            val response = request(uri, MedlemskapsInfo::class.java)
            secureLogger.info("MedlemskapsInfo for {}: {}", aktørId, response.body)
            response.body
        } catch (e: RestClientException) {
            throw IntegrasjonException("Kall mot integrasjon feilet ved uthenting av medlemskapsinfo",
                                       e,
                                       uri,
                                       aktørId.id)
        }
    }

    @Retryable(value = [IntegrasjonException::class],
               maxAttempts = 3,
               backoff = Backoff(delay = 5000))
    fun oppdaterGosysOppgave(fnr: String?,
                             journalpostID: String?,
                             beskrivelse: String?) {
        val uri = URI.create("$integrasjonerServiceUri/oppgave/oppdater")
        logger.info("Sender \"oppdater oppgave\"-request til $uri")
        val oppgave = Oppgave(aktoerId = hentAktørId(fnr).id,
                              journalpostId = journalpostID,
                              eksisterendeOppgaveId = null,
                              beskrivelse = beskrivelse,
                              tema = Tema.KON)
        try {
            postRequest(uri, oppgave, Map::class.java)
        } catch (e: HttpClientErrorException.NotFound) {
            logger.warn("Oppgave returnerte 404, men kaster ikke feil. Uri: {}", uri)
        } catch (e: RestClientException) {
            throw IntegrasjonException("Kan ikke oppdater Gosys-oppgave", e, uri, oppgave.aktoerId)
        }
    }

    private fun formaterDato(date: LocalDate): String {
        return date.format(DateTimeFormatter.ISO_DATE)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(IntegrasjonTjeneste::class.java)
        private val secureLogger = LoggerFactory.getLogger("secureLogger")
        private const val OAUTH2_CLIENT_CONFIG_KEY = "integrasjoner-clientcredentials"
    }

}
