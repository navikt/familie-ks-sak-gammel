package no.nav.familie.ks.sak.app.rest.behandling.grunnlag.personinformasjon

import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.personopplysning.*

data class RestPersonhistorikk(
    val adresser: List<PersonAdresse>,
    val statsborgerskap: List<Statsborgerskap>)

fun Person.toRestPersonhistorikk() = RestPersonhistorikk(
    adresser = this.adresseHistorikk,
    statsborgerskap = this.statsborgerskapHistorikk
)
