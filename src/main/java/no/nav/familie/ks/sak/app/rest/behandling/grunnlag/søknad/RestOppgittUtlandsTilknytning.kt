package no.nav.familie.ks.sak.app.rest.behandling.grunnlag.søknad

data class RestOppgittUtlandsTilknytning(
    val aktørerArbeidYtelseIUtlandet: Set<RestAktørArbeidYtelseUtland>,
    val aktørerTilknytningTilUtlandet: Set<RestAktørTilknytningUtland>)
