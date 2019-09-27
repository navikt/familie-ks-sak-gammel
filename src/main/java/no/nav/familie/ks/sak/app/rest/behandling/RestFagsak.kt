package no.nav.familie.ks.sak.app.rest.behandling

import no.nav.familie.ks.sak.app.behandling.domene.Fagsak
import no.nav.familie.ks.sak.app.integrasjon.OppslagTjeneste
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.PersonIdent

data class RestFagsak(
    val id: Long,
    val søkerPersonIdent: PersonIdent,
    val saksnummer: String,
    val behandlinger: List<RestBehandling>)

fun Fagsak.toRestFagsak(restBehandlinger: List<RestBehandling>, oppslagTjeneste: OppslagTjeneste) = RestFagsak(
    id = this.id,
    søkerPersonIdent = oppslagTjeneste.hentPersonIdent(this.aktørId.id),
    saksnummer = this.saksnummer,
    behandlinger = restBehandlinger
)
