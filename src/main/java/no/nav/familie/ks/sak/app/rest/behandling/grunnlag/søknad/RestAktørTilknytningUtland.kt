package no.nav.familie.ks.sak.app.rest.behandling.grunnlag.søknad

import no.nav.familie.ks.kontrakter.søknad.TilknytningTilUtlandVerdier
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.søknad.AktørTilknytningUtland
import no.nav.familie.ks.sak.app.integrasjon.OppslagTjeneste
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.PersonIdent

data class RestAktørTilknytningUtland(
    val personIdent: PersonIdent,
    val tilknytningTilUtland: TilknytningTilUtlandVerdier,
    val tilknytningTilUtlandForklaring: String)

fun AktørTilknytningUtland.toRestAktørTilknytningUtland(oppslagTjeneste: OppslagTjeneste) = RestAktørTilknytningUtland(
    personIdent = oppslagTjeneste.hentPersonIdent(this.aktør.id),
    tilknytningTilUtland = this.tilknytningTilUtland,
    tilknytningTilUtlandForklaring = this.tilknytningTilUtlandForklaring
)
