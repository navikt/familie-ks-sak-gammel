package no.nav.familie.ks.sak.app.rest.behandling.resultat

data class RestBehandlingsresultat(
        val vilkårsResultat: List<RestVilkårsResultat>,
        val aktiv: Boolean)
