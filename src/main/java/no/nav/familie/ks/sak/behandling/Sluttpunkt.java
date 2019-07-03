package no.nav.familie.ks.sak.behandling;

import no.nav.familie.ks.sak.resultat.årsak.VilkårIkkeOppfyltÅrsak;
import no.nav.familie.ks.sak.resultat.årsak.VilkårOppfyltÅrsak;

class Sluttpunkt {

    private Sluttpunkt() {
        // For å hindre instanser
    }

    static VilkårUtfall oppfylt(String id, VilkårOppfyltÅrsak årsak) {
        return new VilkårUtfall(id, årsak);
    }

    static VilkårUtfall ikkeOppfylt(String id, VilkårIkkeOppfyltÅrsak årsak) {
        return new VilkårUtfall(id, årsak);
    }

}
