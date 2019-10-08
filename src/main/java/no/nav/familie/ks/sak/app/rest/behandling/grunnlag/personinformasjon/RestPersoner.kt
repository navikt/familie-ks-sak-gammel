package no.nav.familie.ks.sak.app.rest.behandling.grunnlag.personinformasjon

data class RestPersoner(
    val søker: RestPerson,
    val barna: List<RestPerson>,
    val annenPart: RestPerson)
