package no.nav.familie.ks.sak.app.rest.behandling.grunnlag.søknad

import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.søknad.AktørArbeidYtelseUtland
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.Standpunkt
import no.nav.familie.ks.sak.app.behandling.domene.typer.AktørId

data class RestAktørArbeidYtelseUtland(
    val aktørId: AktørId,
    val arbeidIUtlandet: Standpunkt,
    val arbeidIUtlandetForklaring: String,
    val ytelseIUtlandet: Standpunkt,
    val ytelseIUtlandetForklaring: String,
    val kontantstøtteIUtlandet: Standpunkt = Standpunkt.UBESVART,
    val kontantstøtteIUtlandetForklaring: String? = null)

fun AktørArbeidYtelseUtland.toRestAktørArbeidYtelseUtland() = RestAktørArbeidYtelseUtland(
    aktørId = this.aktørId,
    arbeidIUtlandet = this.arbeidIUtlandet,
    arbeidIUtlandetForklaring = this.arbeidIUtlandetForklaring,
    ytelseIUtlandet = this.ytelseIUtlandet,
    ytelseIUtlandetForklaring = this.ytelseIUtlandetForklaring,
    kontantstøtteIUtlandet = this.kontantstøtteIUtlandet,
    kontantstøtteIUtlandetForklaring = this.kontantstøtteIUtlandetForklaring
)
