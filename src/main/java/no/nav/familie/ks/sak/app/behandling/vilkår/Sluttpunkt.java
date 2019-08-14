package no.nav.familie.ks.sak.app.behandling.vilkår;

import no.nav.familie.ks.sak.app.behandling.resultat.årsak.VilkårIkkeOppfyltÅrsak;
import no.nav.familie.ks.sak.app.behandling.resultat.årsak.VilkårOppfyltÅrsak;

public class Sluttpunkt {

    private Sluttpunkt() {
        // For å hindre instanser
    }

    public static VilkårUtfall oppfylt(String utfallKode, VilkårOppfyltÅrsak årsak) {
        return new VilkårUtfall(utfallKode, årsak);
    }

    public static VilkårUtfall ikkeOppfylt(String utfallKode, VilkårIkkeOppfyltÅrsak årsak) {
        return new VilkårUtfall(utfallKode, årsak);
    }

}
