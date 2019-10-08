package no.nav.familie.ks.sak.app.rest.behandling.grunnlag.søknad

import no.nav.familie.ks.kontrakter.søknad.Standpunkt
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.søknad.AktørArbeidYtelseUtland
import no.nav.familie.ks.sak.app.integrasjon.OppslagTjeneste
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.PersonIdent

data class RestAktørArbeidYtelseUtland(
    val personIdent: PersonIdent,
    val arbeidIUtlandet: Standpunkt,
    val arbeidIUtlandetForklaring: String,
    val ytelseIUtlandet: Standpunkt,
    val ytelseIUtlandetForklaring: String,
    val kontantstøtteIUtlandet: Standpunkt = Standpunkt.UBESVART,
    val kontantstøtteIUtlandetForklaring: String? = null)

fun AktørArbeidYtelseUtland.toRestAktørArbeidYtelseUtland(oppslagTjeneste: OppslagTjeneste) = RestAktørArbeidYtelseUtland(
        personIdent = PersonIdent.fra(this.fnr),
        arbeidIUtlandet = this.arbeidIUtlandet,
        arbeidIUtlandetForklaring = this.arbeidIUtlandetForklaring,
        ytelseIUtlandet = this.ytelseIUtlandet,
        ytelseIUtlandetForklaring = this.ytelseIUtlandetForklaring,
        kontantstøtteIUtlandet = this.kontantstøtteIUtlandet,
        kontantstøtteIUtlandetForklaring = this.kontantstøtteIUtlandetForklaring
)
