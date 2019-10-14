package no.nav.familie.ks.sak.app.rest.behandling.grunnlag.personinformasjon

import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.personopplysning.PersonRelasjon
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.RelasjonsRolleType
import no.nav.familie.ks.sak.app.integrasjon.OppslagTjeneste

data class RestPersonrelasjon(
    var fraFødselsnummer: String,
    var tilFødselsnummer: String,
    var relasjonsrolle: RelasjonsRolleType,
    var harSammeBosted: Boolean)

fun PersonRelasjon.toRestPersonrelasjon() = RestPersonrelasjon(
    fraFødselsnummer = this.fraPersonIdent.ident,
    tilFødselsnummer = this.tilPersonIdent.ident,
    relasjonsrolle = this.relasjonsrolle,
    harSammeBosted = this.harSammeBosted
)
