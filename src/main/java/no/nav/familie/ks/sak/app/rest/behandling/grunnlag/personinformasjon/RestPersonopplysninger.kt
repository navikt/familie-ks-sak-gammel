package no.nav.familie.ks.sak.app.rest.behandling.grunnlag.personinformasjon

data class RestPersonopplysninger(
    val søker: RestPersonopplysning,
    val barna: List<RestPersonopplysning>,
    val annenPart: RestPersonopplysning)
