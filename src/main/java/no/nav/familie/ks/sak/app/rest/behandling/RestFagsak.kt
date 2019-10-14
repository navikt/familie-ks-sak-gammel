package no.nav.familie.ks.sak.app.rest.behandling

import no.nav.familie.ks.sak.app.behandling.domene.Fagsak
import java.time.LocalDateTime

data class RestFagsak(
    val opprettetTidspunkt: LocalDateTime,
    val id: Long,
    val søkerFødselsnummer: String,
    val saksnummer: String,
    val behandlinger: List<RestBehandling>)

fun Fagsak.toRestFagsak(restBehandlinger: List<RestBehandling>, søkerFødselsnummer: String) = RestFagsak(
    opprettetTidspunkt = this.opprettetTidspunkt,
    id = this.id,
    søkerFødselsnummer = søkerFødselsnummer,
    saksnummer = this.saksnummer,
    behandlinger = restBehandlinger
)
