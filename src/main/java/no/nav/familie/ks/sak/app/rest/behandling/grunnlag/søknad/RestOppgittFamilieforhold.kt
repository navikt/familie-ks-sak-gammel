package no.nav.familie.ks.sak.app.rest.behandling.grunnlag.søknad

data class RestOppgittFamilieforhold(
    val barna: Set<RestBarn>,
    val borBeggeForeldreSammen: Boolean)
