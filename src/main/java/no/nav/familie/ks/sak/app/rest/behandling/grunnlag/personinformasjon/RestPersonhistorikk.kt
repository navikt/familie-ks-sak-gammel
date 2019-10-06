package no.nav.familie.ks.sak.app.rest.behandling.grunnlag.personinformasjon

import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.personopplysning.PersonAdresse
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.personopplysning.Person
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.personopplysning.Statsborgerskap

data class RestPersonhistorikk(
    val adresseHistorikk: List<PersonAdresse>,
    val statsborgerskapHistorikk: List<Statsborgerskap>)

fun Person.toRestPersonhistorikk() = RestPersonhistorikk(
        adresseHistorikk = this.adresseHistorikk,
        statsborgerskapHistorikk = this.statsborgerskapHistorikk
)
