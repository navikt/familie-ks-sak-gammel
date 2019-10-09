package no.nav.familie.ks.sak.app.rest.behandling.grunnlag.personinformasjon

import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.personopplysning.*

data class RestPersonhistorikk(
    val adresser: List<RestAdresseinfo>,
    val statsborgerskap: List<RestStatsborgerskap>)

fun Person.toRestPersonhistorikk() = RestPersonhistorikk(
    adresser = this.adresseHistorikk.map { it.toRestAdresseInfo() },
    statsborgerskap = this.statsborgerskapHistorikk.map { it.toRestStatsborgerskap() }
)
