package no.nav.familie.ks.sak.app.rest.behandling.grunnlag.personinformasjon

import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.personopplysning.Person
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.personopplysning.PersonRelasjon

import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.Landkode
import no.nav.familie.ks.sak.app.integrasjon.OppslagTjeneste

import java.time.LocalDate

data class RestPerson (
    val fødselsnummer: String,
    val navn: String,
    val fødselsdato: LocalDate,
    val dødsdato: LocalDate,
    val statsborgerskap: Landkode,
    val relasjoner: List<RestPersonrelasjon>,
    val personhistorikk: RestPersonhistorikk)


fun Person.toRestPerson(oppslagTjeneste: OppslagTjeneste) = RestPerson(
        fødselsnummer = oppslagTjeneste.hentPersonIdent(this.aktørId.id).ident,
        navn = this.navn,
        fødselsdato = this.fødselsdato,
        dødsdato = this.dødsdato,
        statsborgerskap = this.statsborgerskap,
        relasjoner = this.relasjoner.map { it.toRestPersonrelasjon(oppslagTjeneste) },
        personhistorikk = this.toRestPersonhistorikk()
)
