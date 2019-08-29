package no.nav.familie.ks.sak.app.behandling.vilkår;

import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.årsak.VilkårIkkeOppfyltÅrsak;

public class Sluttpunkt {

    private Sluttpunkt() {
        // For å hindre instanser
    }

    public static Oppfylt oppfylt() {
        return new Oppfylt();
    }

    public static IkkeOppfylt ikkeOppfylt(VilkårIkkeOppfyltÅrsak årsak) {
        return new IkkeOppfylt(årsak);
    }

}
