package no.nav.familie.ks.sak.app.rest.Behandling;

import java.util.Set;

public class RestBehandlingsresultat {
    private Set<RestVilkårsResultat> vilkårsResultat;
    private boolean aktiv;

    public RestBehandlingsresultat(Set<RestVilkårsResultat> vilkårsResultat, boolean aktiv) {
        this.vilkårsResultat = vilkårsResultat;
        this.aktiv = aktiv;
    }
}
