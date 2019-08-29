package no.nav.familie.ks.sak.app.rest.Behandling;

import java.util.Set;

public class RestBehandlingsresultat {
    private Set<RestVilkårsresultat> vilkårsResultat;
    private boolean aktiv;

    public RestBehandlingsresultat(Set<RestVilkårsresultat> vilkårsResultat, boolean aktiv) {
        this.vilkårsResultat = vilkårsResultat;
        this.aktiv = aktiv;
    }
}
