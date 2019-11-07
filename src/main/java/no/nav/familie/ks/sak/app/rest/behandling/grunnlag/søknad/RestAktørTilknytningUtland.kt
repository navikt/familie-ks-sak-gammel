package no.nav.familie.ks.sak.app.rest.behandling.grunnlag.søknad

import no.nav.familie.ks.kontrakter.søknad.TilknytningTilUtlandVerdier
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.søknad.AktørTilknytningUtland

data class RestAktørTilknytningUtland(
    val fødselsnummer: String,
    val tilknytningTilUtland: TilknytningTilUtlandVerdier,
    val tilknytningTilUtlandForklaring: String)

fun AktørTilknytningUtland.toRestAktørTilknytningUtland() = RestAktørTilknytningUtland(
        fødselsnummer = this.fødselsnummer,
        tilknytningTilUtland = this.tilknytningTilUtland,
        tilknytningTilUtlandForklaring = this.tilknytningTilUtlandForklaring
)
