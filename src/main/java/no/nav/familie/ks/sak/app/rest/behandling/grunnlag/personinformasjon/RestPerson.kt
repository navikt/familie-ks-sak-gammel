package no.nav.familie.ks.sak.app.rest.behandling.grunnlag.personinformasjon

import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.personopplysning.Bostedsadresse
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.personopplysning.Person

import java.time.LocalDate

data class RestPerson internal constructor(
        val personIdent: String,
        val navn: String,
        val kjønn: String?,
        val fødselsdato: LocalDate,
        val bostedsadresse: Bostedsadresse?,
        val statsborgerskap: String,
        val relasjoner: List<RestPersonrelasjon>,
        val personhistorikk: RestPersonhistorikk)


fun Person.toRestPerson() = RestPerson(
        personIdent = this.personIdent.ident,
        navn = this.navn,
        kjønn = this.kjønn,
        fødselsdato = this.fødselsdato,
        bostedsadresse = when(this.bostedsadresse.isPresent){
            true -> this.bostedsadresse.get()
            false -> null
        },
        statsborgerskap = this.statsborgerskap.kode,
        relasjoner = this.relasjoner.map { it.toRestPersonrelasjon() },
        personhistorikk = this.toRestPersonhistorikk()
)
