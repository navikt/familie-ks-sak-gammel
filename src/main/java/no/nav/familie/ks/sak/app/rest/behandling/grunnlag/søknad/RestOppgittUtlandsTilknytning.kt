package no.nav.familie.ks.sak.app.rest.behandling.grunnlag.søknad

data class RestOppgittUtlandsTilknytning(
        val aktørerArbeidYtelseIUtlandet: List<RestAktørArbeidYtelseUtland>,
        val aktørerTilknytningTilUtlandet: List<RestAktørTilknytningUtland>)
