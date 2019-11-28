package no.nav.familie.ks.sak.app.rest.tilgangskontroll

import no.nav.familie.ks.sak.app.behandling.domene.BehandlingRepository
import no.nav.familie.ks.sak.app.behandling.domene.FagsakRepository
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.personopplysning.PersonopplysningGrunnlagRepository
import no.nav.familie.ks.sak.app.integrasjon.OppslagTjeneste
import no.nav.familie.ks.sak.app.rest.BaseService
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenService
import no.nav.security.token.support.client.spring.ClientConfigurationProperties
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.stereotype.Service

@Service
class TilgangskontrollService(
        restTemplateBuilderMedProxy: RestTemplateBuilder,
        clientConfigurationProperties: ClientConfigurationProperties,
        oAuth2AccessTokenService: OAuth2AccessTokenService,
        private val oppslagTjeneste: OppslagTjeneste,
        private val behandlingRepository: BehandlingRepository,
        private val personopplysningGrunnlagRepository: PersonopplysningGrunnlagRepository,
        private val fagsakRepository: FagsakRepository) : BaseService("integrasjoner-onbehalfof", restTemplateBuilderMedProxy, clientConfigurationProperties, oAuth2AccessTokenService) {

    fun harTilgang(fagsakId: Long): Boolean {
        val optionalFagsak = fagsakRepository.finnFagsak(fagsakId)
        if (optionalFagsak.isEmpty) {
            return true
        }

        val fagsak = optionalFagsak.get()
        val behandlinger = behandlingRepository.finnBehandlinger(fagsak.id)

        for (behandling in behandlinger) {
            for (personopplysning in personopplysningGrunnlagRepository.findByBehandlingAndAktiv(behandling.id).stream()) {
                for (person in personopplysning.registrertePersoner.get().iterator()) {
                    val respons = oppslagTjeneste.sjekkTilgangTilPerson(person.personIdent.ident, restTemplate)
                    if (!respons.body.isHarTilgang) {
                        return false
                    }
                }
            }
        }
        return true
    }

    companion object {
        val secureLogger = LoggerFactory.getLogger("secureLogger")
        val logger: Logger = LoggerFactory.getLogger(TilgangskontrollService::class.java)
    }
}
