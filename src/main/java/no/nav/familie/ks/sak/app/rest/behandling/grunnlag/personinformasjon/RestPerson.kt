package no.nav.familie.ks.sak.app.rest.behandling.grunnlag.personinformasjon

import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.personopplysning.Person

import no.nav.familie.ks.sak.app.integrasjon.OppslagTjeneste

import java.time.LocalDate

data class RestPerson internal constructor(
    val fødselsnummer: String,
    val navn: String,
    val fødselsdato: LocalDate,
    val statsborgerskap: String,
    val relasjoner: List<RestPersonrelasjon>,
    val personhistorikk: RestPersonhistorikk)


fun Person.toRestPerson() = RestPerson(
        fødselsnummer = this.personIdent.ident,
        navn = this.navn,
        fødselsdato = this.fødselsdato,
        statsborgerskap = this.statsborgerskap.kode,
        relasjoner = this.relasjoner.map { it.toRestPersonrelasjon() },
        personhistorikk = this.toRestPersonhistorikk()
)
