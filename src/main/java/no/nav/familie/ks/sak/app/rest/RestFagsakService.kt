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
import no.nav.familie.ks.sak.app.rest.behandling.grunnlag.personinformasjon.RestPersonopplysninger
import no.nav.familie.ks.sak.app.rest.behandling.grunnlag.søknad.RestAktørArbeidYtelseUtland
import no.nav.familie.ks.sak.app.rest.behandling.grunnlag.søknad.RestAktørTilknytningUtland
import no.nav.familie.ks.sak.app.rest.behandling.grunnlag.søknad.RestBarn
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
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

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
        val restBehandlinger = ArrayList<RestBehandling>()
        behandlinger.forEach { behandling ->
            val søknadGrunnlag: SøknadGrunnlag = søknadGrunnlagRepository.finnGrunnlag(behandling.id)
            val barnehageBarnGrunnlag: BarnehageBarnGrunnlag = barnehageBarnGrunnlagRepository.finnGrunnlag(behandling.id)

            val barna = HashSet<RestBarn>()
            barnehageBarnGrunnlag.familieforhold.barna.forEach { barn ->
                barna.add( barn.toRestBarn(oppslagTjeneste) )
            }
            val familieforhold = RestOppgittFamilieforhold(barna, barnehageBarnGrunnlag.familieforhold.isBorBeggeForeldreSammen)

            val aktørerArbeidYtelseUtland = HashSet<RestAktørArbeidYtelseUtland>()
            val aktørerTilknytningUtland = HashSet<RestAktørTilknytningUtland>()
            søknadGrunnlag.søknad.utlandsTilknytning.aktørerArbeidYtelseIUtlandet.forEach { aktørArbeidYtelseUtland ->
                aktørerArbeidYtelseUtland.add(aktørArbeidYtelseUtland.toRestAktørArbeidYtelseUtland(oppslagTjeneste))
            }
            søknadGrunnlag.søknad.utlandsTilknytning.aktørerTilknytningTilUtlandet.forEach { aktørTilknytningUtland ->
                aktørerTilknytningUtland.add( aktørTilknytningUtland.toRestAktørTilknytningUtland(oppslagTjeneste))
            }

            val oppgittUtlandsTilknytning = RestOppgittUtlandsTilknytning(aktørerArbeidYtelseUtland, aktørerTilknytningUtland)

            val erklæring = søknadGrunnlag.søknad.erklæring
            val oppgittErklæring = RestOppgittErklæring(erklæring.isBarnetHjemmeværendeOgIkkeAdoptert, erklæring.isBorSammenMedBarnet, erklæring.isIkkeAvtaltDeltBosted, erklæring.isBarnINorgeNeste12Måneder)

            val søknad = RestSøknad(søknadGrunnlag.søknad.innsendtTidspunkt, familieforhold, oppgittUtlandsTilknytning, oppgittErklæring)

            // Grunnlag fra TPS
            val personopplysninger: RestPersonopplysninger? = null

            // Grunnlag fra regelkjøring
            val behandlingResultat = behandlingresultatRepository.finnBehandlingsresultat(behandling.id)
            val restVilkårsResultat = HashSet<RestVilkårsResultat>()
            behandlingResultat.vilkårsResultat.vilkårsResultat.forEach { vilkårResultat ->
                restVilkårsResultat.add(
                        RestVilkårsResultat(
                                vilkårResultat.vilkårType,
                                vilkårResultat.utfall))
            }

            val restBehandlingsresultat = RestBehandlingsresultat(restVilkårsResultat, behandlingResultat.isAktiv)

            restBehandlinger.add(RestBehandling(behandling.id, søknad, restBehandlingsresultat, personopplysninger))
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
