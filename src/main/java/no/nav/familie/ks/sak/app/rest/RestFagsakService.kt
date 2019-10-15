package no.nav.familie.ks.sak.app.rest

import no.nav.familie.ks.sak.app.behandling.domene.BehandlingRepository
import no.nav.familie.ks.sak.app.behandling.domene.Fagsak
import no.nav.familie.ks.sak.app.behandling.domene.FagsakRepository
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.barnehagebarn.BarnehageBarnGrunnlagRepository
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.personopplysning.PersonopplysningGrunnlagRepository
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.søknad.SøknadGrunnlagRepository
import no.nav.familie.ks.sak.app.behandling.domene.resultat.BehandlingresultatRepository
import no.nav.familie.ks.sak.app.integrasjon.OppslagTjeneste
import no.nav.familie.ks.sak.app.rest.behandling.RestBehandling
import no.nav.familie.ks.sak.app.rest.behandling.RestFagsak
import no.nav.familie.ks.sak.app.rest.behandling.grunnlag.personinformasjon.RestPersoner
import no.nav.familie.ks.sak.app.rest.behandling.grunnlag.personinformasjon.toRestPerson
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
        private val personopplysningGrunnlagRepository: PersonopplysningGrunnlagRepository,
        private val fagsakRepository: FagsakRepository) {


    fun hentRestFagsaker(saksnummer: String): List<RestFagsak> {
        val fagsaker = fagsakRepository.finnFagsak(saksnummer)
        return fagsaker.map { hentRestFagsak(it) }.filterNotNull()
    }

    fun hentRestFagsak(fagsak: Fagsak): RestFagsak? {
        val behandlinger = behandlingRepository.finnBehandlinger(fagsak.id)
        var søkerFødselsnummer = ""

        // Grunnlag fra søknag
        val restBehandlinger: List<RestBehandling> = behandlinger.map {
            val søknad: RestSøknad = søknadGrunnlagRepository.finnGrunnlag(it.id).map { søknadGrunnlag ->
                val aktørerArbeidYtelseUtland = søknadGrunnlag.søknad.utlandsTilknytning.aktørerArbeidYtelseIUtlandet.map { aktørArbeidYtelseUtland ->
                    aktørArbeidYtelseUtland.toRestAktørArbeidYtelseUtland()
                }
                val aktørerTilknytningUtland = søknadGrunnlag.søknad.utlandsTilknytning.aktørerTilknytningTilUtlandet.map { aktørTilknytningUtland ->
                    aktørTilknytningUtland.toRestAktørTilknytningUtland()
                }

                val oppgittUtlandsTilknytning = RestOppgittUtlandsTilknytning(aktørerArbeidYtelseUtland, aktørerTilknytningUtland)

                val erklæring = søknadGrunnlag.søknad.erklæring
                val oppgittErklæring = RestOppgittErklæring(erklæring.isBarnetHjemmeværendeOgIkkeAdoptert, erklæring.isBorSammenMedBarnet, erklæring.isIkkeAvtaltDeltBosted, erklæring.isBarnINorgeNeste12Måneder)

                val familieforhold = barnehageBarnGrunnlagRepository.finnGrunnlag(it.id).map { barnehageBarnGrunnlag ->
                    val barna = barnehageBarnGrunnlag.familieforhold.barna.map { barn ->
                        barn.toRestBarn()
                    }

                    RestOppgittFamilieforhold(barna, barnehageBarnGrunnlag.familieforhold.isBorBeggeForeldreSammen)
                }.orElseThrow()

                søkerFødselsnummer = søknadGrunnlag.søknad.søkerFødselsnummer;

                RestSøknad(søknadGrunnlag.søknad.innsendtTidspunkt, familieforhold, oppgittUtlandsTilknytning, oppgittErklæring)
            }.orElseThrow()

            // Grunnlag fra TPS
            val personopplysninger: RestPersoner = personopplysningGrunnlagRepository.findByBehandlingAndAktiv(it.id).map { personopplysningGrunnlag ->
                RestPersoner(
                    søker = personopplysningGrunnlag.søker.toRestPerson(),
                    annenPart = personopplysningGrunnlag.annenPart?.toRestPerson(),
                    barna = personopplysningGrunnlag.barna.map { barn -> barn.toRestPerson() }
                )
            }.orElseThrow()

            // Grunnlag fra regelkjøring
            val restBehandlingsresultat = behandlingresultatRepository.finnBehandlingsresultat(it.id).map { behandlingsresultat ->
                val restVilkårsResultat = behandlingsresultat.vilkårsResultat.vilkårsResultat.map { vilkårResultat ->
                    RestVilkårsResultat(vilkårResultat.vilkårType, vilkårResultat.utfall)
                }

                RestBehandlingsresultat(restVilkårsResultat, behandlingsresultat.isAktiv)
            }.orElseThrow()

            RestBehandling(it.id, søknad, restBehandlingsresultat, personopplysninger)
        }

        return fagsak.toRestFagsak(restBehandlinger, søkerFødselsnummer )
    }

    fun hentRessursFagsak(saksnummer: String): List<RestFagsak> {
        return hentRestFagsaker(saksnummer)
    }

    fun hentFagsaker(): List<Fagsak> {
        return fagsakRepository.findAll()
    }
}
