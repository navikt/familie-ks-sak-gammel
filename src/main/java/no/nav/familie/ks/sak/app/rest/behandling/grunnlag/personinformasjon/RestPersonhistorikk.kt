package no.nav.familie.ks.sak.app.rest.behandling.grunnlag.personinformasjon

import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.personopplysning.PersonAdresse
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.personopplysning.Personopplysning
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.personopplysning.PersonopplysningerInformasjon
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.personopplysning.Statsborgerskap

data class RestPersonhistorikk(
    val adresser: List<PersonAdresse>,
    val personopplysninger: List<Personopplysning>,
    val statsborgerskap: List<Statsborgerskap>)

fun PersonopplysningerInformasjon.toRestPersonhistorikk() = RestPersonhistorikk(
    adresser = this.adresser,
    personopplysninger = this.personopplysninger,
    statsborgerskap = this.statsborgerskap
)
