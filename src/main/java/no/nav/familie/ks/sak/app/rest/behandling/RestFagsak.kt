package no.nav.familie.ks.sak.app.rest.behandling

import no.nav.familie.ks.sak.app.behandling.domene.Fagsak

data class RestFagsak(
    val fagsak: Fagsak,
    val behandlinger: List<RestBehandling>)
