package no.nav.familie.ks.sak.app.rest

import no.nav.familie.ks.sak.app.behandling.domene.BehandlingRepository
import no.nav.familie.ks.sak.app.behandling.domene.Fagsak
import no.nav.familie.ks.sak.app.behandling.domene.FagsakRepository
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.barnehagebarn.BarnehageBarnGrunnlag
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.barnehagebarn.BarnehageBarnGrunnlagRepository
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.søknad.SøknadGrunnlag
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.søknad.SøknadGrunnlagRepository
import no.nav.familie.ks.sak.app.behandling.domene.resultat.BehandlingresultatRepository
import no.nav.familie.ks.sak.app.integrasjon.OppslagTjeneste
import no.nav.familie.ks.sak.app.rest.behandling.RestBehandling
import no.nav.familie.ks.sak.app.rest.behandling.RestFagsak
import no.nav.familie.ks.sak.app.rest.behandling.grunnlag.personinformasjon.RestPersoner
import no.nav.familie.ks.sak.app.rest.behandling.grunnlag.søknad.RestOppgittErklæring
import no.nav.familie.ks.sak.app.rest.behandling.grunnlag.søknad.RestOppgittFamilieforhold
import no.nav.familie.ks.sak.app.rest.behandling.grunnlag.søknad.RestOppgittUtlandsTilknytning
import no.nav.familie.ks.sak.app.rest.behandling.grunnlag.søknad.RestSøknad
import no.nav.familie.ks.sak.app.rest.behandling.grunnlag.søknad.toRestAktørArbeidYtelseUtland
import no.nav.familie.ks.sak.app.rest.behandling.grunnlag.søknad.toRestAktørTilknytningUtland
import no.nav.familie.ks.sak.app.rest.behandling.grunnlag.søknad.toRestBarn
import no.nav.familie.ks.sak.app.rest.behandling.resultat.RestBehandlingsresultat
import no.nav.familie.ks.sak.app.rest.behandling.resultat.RestVilkårsResultat
import no.nav.familie.ks.sak.app.rest.behandling.toRestFagsak
import org.springframework.stereotype.Service

@Service
class RestFagsakService (
        private val behandlingresultatRepository: BehandlingresultatRepository,
        private val barnehageBarnGrunnlagRepository: BarnehageBarnGrunnlagRepository,
        private val søknadGrunnlagRepository: SøknadGrunnlagRepository,
        private val behandlingRepository: BehandlingRepository,
        private val oppslagTjeneste: OppslagTjeneste,
        private val fagsakRepository: FagsakRepository) {

    fun hentRestFagsak(fagsakId: Long): RestFagsak? {
        val fagsak = fagsakRepository.findById(fagsakId)
        val behandlinger = behandlingRepository.finnBehandlinger(fagsakId)

        // Grunnlag fra søknag
        val restBehandlinger: List<RestBehandling> = behandlinger.map { behandling ->
            val søknadGrunnlag: SøknadGrunnlag = søknadGrunnlagRepository.finnGrunnlag(behandling.id)
            val barnehageBarnGrunnlag: BarnehageBarnGrunnlag = barnehageBarnGrunnlagRepository.finnGrunnlag(behandling.id)

            val barna = barnehageBarnGrunnlag.familieforhold.barna.map { barn ->
                barn.toRestBarn()
            }
            val familieforhold = RestOppgittFamilieforhold(barna, barnehageBarnGrunnlag.familieforhold.isBorBeggeForeldreSammen)

            val aktørerArbeidYtelseUtland = søknadGrunnlag.søknad.utlandsTilknytning.aktørerArbeidYtelseIUtlandet.map { aktørArbeidYtelseUtland ->
                aktørArbeidYtelseUtland.toRestAktørArbeidYtelseUtland()
            }
            val aktørerTilknytningUtland = søknadGrunnlag.søknad.utlandsTilknytning.aktørerTilknytningTilUtlandet.map { aktørTilknytningUtland ->
                aktørTilknytningUtland.toRestAktørTilknytningUtland()
            }

            val oppgittUtlandsTilknytning = RestOppgittUtlandsTilknytning(aktørerArbeidYtelseUtland, aktørerTilknytningUtland)

            val erklæring = søknadGrunnlag.søknad.erklæring
            val oppgittErklæring = RestOppgittErklæring(erklæring.isBarnetHjemmeværendeOgIkkeAdoptert, erklæring.isBorSammenMedBarnet, erklæring.isIkkeAvtaltDeltBosted, erklæring.isBarnINorgeNeste12Måneder)

            val søknad = RestSøknad(søknadGrunnlag.søknad.innsendtTidspunkt, familieforhold, oppgittUtlandsTilknytning, oppgittErklæring)

            // Grunnlag fra TPS
            val personopplysninger: RestPersoner? = null

            // Grunnlag fra regelkjøring
            val behandlingResultat = behandlingresultatRepository.finnBehandlingsresultat(behandling.id)
            val restVilkårsResultat = behandlingResultat.vilkårsResultat.vilkårsResultat.map { vilkårResultat ->
                RestVilkårsResultat(vilkårResultat.vilkårType, vilkårResultat.utfall)
            }

            val restBehandlingsresultat = RestBehandlingsresultat(restVilkårsResultat, behandlingResultat.isAktiv)

            RestBehandling(behandling.id, søknad, restBehandlingsresultat, personopplysninger)
        }

        return fagsak.map { it.toRestFagsak(restBehandlinger, oppslagTjeneste) }.orElse(null)
    }

    fun hentRessursFagsak(fagsakId: Long): RestFagsak? {
        return hentRestFagsak(fagsakId)
    }

    fun hentFagsaker(): List<Fagsak> {
        return fagsakRepository.findAll()
    }
}
