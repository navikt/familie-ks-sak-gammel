package no.nav.familie.ks.sak.app.rest

import no.nav.familie.ks.kontrakter.søknad.Søknad
import no.nav.familie.ks.kontrakter.søknad.toSøknad
import no.nav.familie.ks.sak.app.behandling.BehandlingslagerService
import no.nav.familie.ks.sak.app.behandling.Saksbehandling
import no.nav.familie.ks.sak.app.behandling.domene.Behandling
import no.nav.familie.ks.sak.app.behandling.domene.BehandlingRepository
import no.nav.familie.ks.sak.app.behandling.resultat.Vedtak
import no.nav.familie.ks.sak.util.SporingsLoggActionType
import no.nav.familie.ks.sak.util.SporingsLoggHelper
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import java.security.Principal
import java.util.stream.Collectors



@RestController
@RequestMapping("/api")
@ProtectedWithClaims( issuer = "azuread" )
//@PreAuthorize("hasAnyRole('GROUP_xxx', 'GROUP_xxx)")
class FagsakController (
    private val saksbehandling: Saksbehandling,
    private val behandlingRepository: BehandlingRepository,
    private val restFagsakService: RestFagsakService) {

    @GetMapping(path = ["/fagsak/{fagsakId}"])
    //@Secured("ROLE_xxx")
    fun fagsak(@PathVariable fagsakId: Long, principal: Principal?): ResponseEntity<Ressurs> {
        val authentication: Authentication = SecurityContextHolder.getContext ().getAuthentication()

        secureLogger.info(authentication.authorities.toString())

        logger.info("{} henter fagsak med id {}", principal?.name ?: "Ukjent", fagsakId)
        SporingsLoggHelper.logSporing(FagsakController::class.java, fagsakId, principal?.name ?: "Ukjent", SporingsLoggActionType.READ, "fagsak")

        val ressurs: Ressurs = Result.runCatching { restFagsakService.hentRessursFagsak(fagsakId) }
                .fold(
                    onSuccess = { when(it) {
                        null -> Ressurs.failure("Fant ikke fagsak med fagsakId: $fagsakId")
                        else -> Ressurs.success( data = it )
                    } },
                    onFailure = { e -> Ressurs.failure( "Henting av fagsak med fagsakId $fagsakId feilet: ${e.message}", e) }
                )

        return ResponseEntity.ok(ressurs)
    }

    @GetMapping(path = ["/fagsak"])
    fun fagsak(principal: Principal?): ResponseEntity<Ressurs> {
        logger.info("{} henter fagsaker", principal?.name ?: "Ukjent")

        val ressurs: Ressurs = Result.runCatching { restFagsakService.hentFagsaker() }
            .fold(
                onSuccess = { Ressurs.success( data = it) },
                onFailure = { e -> Ressurs.failure("Henting av fagsaker feilet.", e) }
            )

        return ResponseEntity.ok(ressurs)
    }

    @Profile("dev")
    @PostMapping(path = ["/behandle"])
    fun behandle(@RequestBody søknadJson: String): ResponseEntity<Ressurs> {
        val søknad: Søknad = søknadJson.toSøknad()
        val behandling: Behandling? = Result.runCatching {
            val vedtak: Vedtak = saksbehandling.behandle(søknad, "GSAK")
            behandlingRepository.getOne(vedtak.behandlingsId)
        }.fold(
            onSuccess = { it },
            onFailure = { null }
        )

        val ressurs: Ressurs = when(behandling) {
            null -> Ressurs.failure("Behandling feilet")
            else -> Result.runCatching { restFagsakService.hentRessursFagsak(behandling.fagsak.id) }
                .fold(
                    onSuccess = { Ressurs.success( data = it) },
                    onFailure = { e -> Ressurs.failure("Henting av fagsak feilet.", e) }
                )
        }

        return ResponseEntity.ok(ressurs)
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(BehandlingslagerService::class.java)
        val secureLogger = LoggerFactory.getLogger("secureLogger")
    }
}
