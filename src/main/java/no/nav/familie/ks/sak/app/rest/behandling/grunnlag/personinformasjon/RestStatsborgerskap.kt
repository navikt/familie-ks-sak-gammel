package no.nav.familie.ks.sak.app.rest.behandling.grunnlag.personinformasjon

import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.personopplysning.Statsborgerskap
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.Landkode
import no.nav.familie.ks.sak.app.behandling.domene.typer.DatoIntervallEntitet


data class RestStatsborgerskap (
    val periode: DatoIntervallEntitet,
    val statsborgerskap: String
)

fun Statsborgerskap.toRestStatsborgerskap() = RestStatsborgerskap(
    periode = this.periode,
    statsborgerskap = this.statsborgerskap.kode
)
