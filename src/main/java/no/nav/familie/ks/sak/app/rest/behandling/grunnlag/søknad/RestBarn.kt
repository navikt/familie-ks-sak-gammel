package no.nav.familie.ks.sak.app.rest.behandling.grunnlag.søknad

import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.barnehagebarn.Barn
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.BarnehageplassStatus
import java.time.LocalDate

data class RestBarn(
    val fødselsnummer: String,
    val barnehageStatus: BarnehageplassStatus,
    val barnehageAntallTimer: Double?,
    val barnehageDato: LocalDate?,
    val barnehageKommune: String?)

fun Barn.toRestBarn() = RestBarn(
    fødselsnummer = this.fødselsnummer,
    barnehageStatus = this.barnehageStatus,
    barnehageAntallTimer = this.barnehageAntallTimer,
    barnehageDato = this.barnehageDato,
    barnehageKommune = this.barnehageKommune
)
