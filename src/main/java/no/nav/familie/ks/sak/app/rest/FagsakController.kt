package no.nav.familie.ks.sak.app.rest

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.familie.ks.sak.app.behandling.BehandlingslagerService
import no.nav.security.oidc.api.Unprotected
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/api")
class FagsakController @Autowired
internal constructor(
    private val restFagsakService: RestFagsakService,
    private val objectMapper: ObjectMapper) {

    @GetMapping(path = ["/fagsak/{fagsakId}"])
    fun fagsak(@PathVariable fagsakId: Long?, principal: Principal): ResponseEntity<Ressurs> {
        logger.info("{} henter fagsak med id {}", principal.name, fagsakId)

        val ressurs: Ressurs = when (fagsakId) {
            null -> Ressurs.failure("Oppgitt fagsak id var null")
            else -> when ( val fagsak = restFagsakService.hentRessursFagsak(fagsakId)) {
                null -> Ressurs.failure("Fant ikke fagsak med id fagsakId")
                else -> Ressurs.success( data = objectMapper.valueToTree(fagsak) )
            }
        }

        return ResponseEntity.ok(ressurs)
    }

    @GetMapping(path = ["/fagsak"])
    @Unprotected
    fun fagsak(principal: Principal): ResponseEntity<Ressurs> {
        logger.info("{} henter fagsaker", principal.name)

        val ressurs: Ressurs = Result.runCatching { restFagsakService.hentFagsaker() }
            .fold(
                onSuccess = { Ressurs.success( data = objectMapper.valueToTree(it)) },
                onFailure = { Ressurs.failure("Henting av fagsaker feilet.") }
            )

        return ResponseEntity.ok(ressurs)
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(BehandlingslagerService::class.java)
    }
}
