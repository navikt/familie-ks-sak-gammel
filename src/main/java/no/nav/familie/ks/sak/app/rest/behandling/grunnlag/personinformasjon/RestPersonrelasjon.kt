package no.nav.familie.ks.sak.app.rest.behandling.grunnlag.personinformasjon

import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.personopplysning.PersonRelasjon
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.RelasjonsRolleType
import no.nav.familie.ks.sak.app.behandling.domene.typer.AktørId

data class RestPersonrelasjon(
    var fraAktørId: AktørId,
    var tilAktørId: AktørId,
    var relasjonsrolle: RelasjonsRolleType,
    var harSammeBosted: Boolean)

fun PersonRelasjon.toRestPersonrelasjon() = RestPersonrelasjon(
    fraAktørId = this.fraAktørId,
    tilAktørId = this.tilAktørId,
    relasjonsrolle = this.relasjonsrolle,
    harSammeBosted = this.harSammeBosted
)
