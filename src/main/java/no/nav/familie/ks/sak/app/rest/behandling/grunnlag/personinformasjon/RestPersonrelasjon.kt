package no.nav.familie.ks.sak.app.rest.behandling.grunnlag.personinformasjon

import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.personopplysning.PersonRelasjon
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.RelasjonsRolleType

data class RestPersonrelasjon(
        var fraPersonIdent: String,
        var tilPersonIdent: String,
        var relasjonsrolle: RelasjonsRolleType,
        var harSammeBosted: Boolean)

fun PersonRelasjon.toRestPersonrelasjon() = RestPersonrelasjon(
    fraPersonIdent = this.fraPersonIdent.ident,
    tilPersonIdent = this.tilPersonIdent.ident,
    relasjonsrolle = this.relasjonsrolle,
    harSammeBosted = this.harSammeBosted
)
