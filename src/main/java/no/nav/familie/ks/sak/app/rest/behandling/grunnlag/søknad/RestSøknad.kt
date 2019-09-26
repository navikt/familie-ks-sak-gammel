package no.nav.familie.ks.sak.app.rest.behandling.grunnlag.søknad

import java.time.LocalDateTime

data class RestSøknad(
    val innsendtTidspunkt: LocalDateTime,
    val familieforhold: RestOppgittFamilieforhold,
    val utlandsTilknytning: RestOppgittUtlandsTilknytning,
    val erklæring: RestOppgittErklæring)
