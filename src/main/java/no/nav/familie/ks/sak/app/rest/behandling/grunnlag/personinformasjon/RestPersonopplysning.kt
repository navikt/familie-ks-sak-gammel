package no.nav.familie.ks.sak.app.rest.behandling.grunnlag.personinformasjon

import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.personopplysning.PersonRelasjon
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.personopplysning.Person
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.Landkode

import java.time.LocalDate

data class RestPersonopplysning internal constructor(
    val fødselsnummer: String,
    val person: Person)
{
    val navn: String = person.navn
    val fødselsdato: LocalDate = person.fødselsdato
    val dødsdato: LocalDate = person.dødsdato
    val statsborgerskap: Landkode = person.statsborgerskap
    val relasjoner: List<PersonRelasjon> = person.relasjoner
    val personhistorikk: RestPersonhistorikk = person.toRestPersonhistorikk()
}
