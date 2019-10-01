package no.nav.familie.ks.sak.app.rest.behandling.grunnlag.søknad

data class RestOppgittFamilieforhold(
        val barna: List<RestBarn>,
        val borBeggeForeldreSammen: Boolean)
