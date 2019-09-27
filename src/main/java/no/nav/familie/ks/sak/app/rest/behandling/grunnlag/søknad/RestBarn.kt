package no.nav.familie.ks.sak.app.rest.behandling.grunnlag.søknad

import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.barnehagebarn.Barn
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.BarnehageplassStatus
import no.nav.familie.ks.sak.app.integrasjon.OppslagTjeneste
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.PersonIdent
import java.time.LocalDate

data class RestBarn(
    val personIdent: PersonIdent,
    val barnehageStatus: BarnehageplassStatus,
    val barnehageAntallTimer: Double?,
    val barnehageDato: LocalDate?,
    val barnehageKommune: String?)

fun Barn.toRestBarn(oppslagTjeneste: OppslagTjeneste) = RestBarn(
    personIdent = oppslagTjeneste.hentPersonIdent(this.aktørId.id),
    barnehageStatus = this.barnehageStatus,
    barnehageAntallTimer = this.barnehageAntallTimer,
    barnehageDato = this.barnehageDato,
    barnehageKommune = this.barnehageKommune
)
