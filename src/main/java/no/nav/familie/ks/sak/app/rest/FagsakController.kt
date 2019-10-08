package no.nav.familie.ks.sak.app.rest

import no.nav.familie.ks.kontrakter.søknad.Søknad
import no.nav.familie.ks.sak.app.behandling.BehandlingslagerService
import no.nav.familie.ks.sak.app.behandling.Saksbehandling
import no.nav.familie.ks.sak.app.behandling.domene.Behandling
import no.nav.familie.ks.sak.app.behandling.domene.BehandlingRepository
import no.nav.familie.ks.sak.app.behandling.resultat.Vedtak
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
@RequestMapping("/api")
class FagsakController (
    private val saksbehandling: Saksbehandling,
    private val behandlingRepository: BehandlingRepository,
    private val restFagsakService: RestFagsakService) {

    @GetMapping(path = ["/fagsak/{fagsakId}"])
    fun fagsak(@PathVariable fagsakId: Long, principal: Principal?): ResponseEntity<Ressurs> {
        logger.info("{} henter fagsak med id {}", principal?.name ?: "Ukjent", fagsakId)

        val ressurs: Ressurs = Result.runCatching { restFagsakService.hentRessursFagsak(fagsakId) }
                .fold(
                    onSuccess = { when(it) {
                        null -> Ressurs.failure("Fant ikke fagsak med id fagsakId")
                        else -> Ressurs.success( data = it )
                    } },
                    onFailure = { e -> Ressurs.failure( String.format("Henting av fagsak med id %s feilet: %s", fagsakId, e.message), e) }
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
    
    @PostMapping(path = ["/behandle"])
    fun behandle(@RequestBody søknad: Søknad): ResponseEntity<Ressurs> {
        val behandling: Behandling? = Result.runCatching {
            val vedtak: Vedtak = saksbehandling.behandle(søknad, null)
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
