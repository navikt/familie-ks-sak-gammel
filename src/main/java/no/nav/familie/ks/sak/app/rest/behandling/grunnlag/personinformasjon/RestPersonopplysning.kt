package no.nav.familie.ks.sak.app.rest.behandling.grunnlag.personinformasjon

import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.personopplysning.PersonRelasjon
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.personopplysning.Personopplysning
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.personopplysning.PersonopplysningerInformasjon
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.Landkode

import java.time.LocalDate

data class RestPersonopplysning internal constructor(
    val fødselsnummer: String,
    val personopplysning: Personopplysning,
    val personopplysningerInformasjon: PersonopplysningerInformasjon)
{
    val navn: String = personopplysning.navn
    val fødselsdato: LocalDate = personopplysning.fødselsdato
    val dødsdato: LocalDate = personopplysning.dødsdato
    val statsborgerskap: Landkode = personopplysning.statsborgerskap
    val relasjoner: List<PersonRelasjon> = personopplysningerInformasjon.relasjoner
    val personhistorikk: RestPersonhistorikk = personopplysningerInformasjon.toRestPersonhistorikk()
}
