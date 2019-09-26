package no.nav.familie.ks.sak.app.rest.behandling.grunnlag.søknad

import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.søknad.AktørTilknytningUtland
import no.nav.familie.ks.sak.app.behandling.domene.typer.AktørId
import no.nav.familie.ks.sak.app.grunnlag.søknad.TilknytningTilUtland

data class RestAktørTilknytningUtland(
    val aktør: AktørId,
    val tilknytningTilUtland: TilknytningTilUtland.TilknytningTilUtlandVerdier,
    val tilknytningTilUtlandForklaring: String)

fun AktørTilknytningUtland.toRestAktørTilknytningUtland() = RestAktørTilknytningUtland(
    aktør = this.aktør,
    tilknytningTilUtland = this.tilknytningTilUtland,
    tilknytningTilUtlandForklaring = this.tilknytningTilUtlandForklaring
)
