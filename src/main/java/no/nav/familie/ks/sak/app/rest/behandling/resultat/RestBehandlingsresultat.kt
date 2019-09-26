package no.nav.familie.ks.sak.app.rest.behandling.resultat

data class RestBehandlingsresultat(
    val vilkårsResultat: Set<RestVilkårsResultat>,
    val aktiv: Boolean)
