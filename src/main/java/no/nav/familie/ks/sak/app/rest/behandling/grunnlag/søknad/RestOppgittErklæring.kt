package no.nav.familie.ks.sak.app.rest.behandling.grunnlag.søknad

data class RestOppgittErklæring(
    val barnetHjemmeværendeOgIkkeAdoptert: Boolean,
    val borSammenMedBarnet: Boolean,
    val ikkeAvtaltDeltBosted: Boolean,
    val barnINorgeNeste12Måneder: Boolean)
