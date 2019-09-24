package no.nav.familie.ks.sak.app.rest.Behandling.resultat;

import java.util.Set;

public class RestBehandlingsresultat {
    public Set<RestVilkårsResultat> vilkårsResultat;
    public boolean aktiv;

    public RestBehandlingsresultat(Set<RestVilkårsResultat> vilkårsResultat, boolean aktiv) {
        this.vilkårsResultat = vilkårsResultat;
        this.aktiv = aktiv;
    }
}
