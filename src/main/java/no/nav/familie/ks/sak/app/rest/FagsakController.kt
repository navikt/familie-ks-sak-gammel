package no.nav.familie.ks.sak.app.rest

import no.nav.familie.ks.kontrakter.søknad.Søknad
import no.nav.familie.ks.kontrakter.søknad.toSøknad
import no.nav.familie.ks.sak.app.behandling.BehandlingslagerService
import no.nav.familie.ks.sak.app.behandling.Saksbehandling
import no.nav.familie.ks.sak.app.behandling.domene.Behandling
import no.nav.familie.ks.sak.app.behandling.domene.BehandlingRepository
import no.nav.familie.ks.sak.app.behandling.resultat.Vedtak
import no.nav.familie.ks.sak.app.rest.tilgangskontroll.OIDCUtil
import no.nav.familie.ks.sak.app.rest.tilgangskontroll.TilgangskontrollService
import no.nav.familie.ks.sak.util.SporingsLoggActionType
import no.nav.familie.ks.sak.util.SporingsLoggHelper
import no.nav.security.token.support.core.api.ProtectedWithClaims
import no.nav.security.token.support.core.context.TokenValidationContextHolder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/api")
@ProtectedWithClaims( issuer = "azuread" )
class FagsakController (
    private val saksbehandling: Saksbehandling,
    private val tilgangskontrollService: TilgangskontrollService,
    private val behandlingRepository: BehandlingRepository,
    private val restFagsakService: RestFagsakService,
    private val oidcUtil: OIDCUtil) {

    @GetMapping(path = ["/fagsak/{fagsakId}"])
    fun fagsak(@PathVariable fagsakId: Long): ResponseEntity<Ressurs> {

        val saksbehandlerId = oidcUtil.navIdent

        logger.info("{} henter fagsak med id {}", saksbehandlerId ?: "Ukjent", fagsakId)

        SporingsLoggHelper.logSporing(FagsakController::class.java, fagsakId, saksbehandlerId ?: "Ukjent", SporingsLoggActionType.READ, "fagsak")

        if (!tilgangskontrollService.harTilgang(fagsakId, saksbehandlerId)){
            return ResponseEntity.ok(Ressurs.ikkeTilgang("Du har ikke tilgang til denne fagsaken"))
        }

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
    fun fagsak(@RequestHeader filter: String?): ResponseEntity<Ressurs> {
        val saksbehandlerId = oidcUtil.navIdent

        logger.info("{} henter fagsaker", saksbehandlerId ?: "Ukjent")

        val ressurs: Ressurs = Result.runCatching { restFagsakService.hentFagsaker(filter) }
            .fold(
                onSuccess = { Ressurs.success( data = it ) },
                onFailure = { e -> Ressurs.failure("Henting av fagsaker feilet.", e) }
            )

        return ResponseEntity.ok(ressurs)
    }

    @Profile("dev")
    @PostMapping(path = ["/behandle"])
    fun behandle(@RequestBody søknadJson: String): ResponseEntity<Ressurs> {
        val søknad: Søknad = søknadJson.toSøknad()
        val behandling: Behandling? = Result.runCatching {
            val vedtak: Vedtak = saksbehandling.behandle(søknad, "GSAK", "journalpostID")
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
    }
}
