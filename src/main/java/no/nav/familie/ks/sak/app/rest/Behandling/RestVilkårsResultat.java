package no.nav.familie.ks.sak.app.rest.Behandling;

import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.UtfallType;
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.VilkårType;

public class RestVilkårsResultat {
    private VilkårType vilkårType;
    private UtfallType utfall;

    public RestVilkårsResultat(VilkårType vilkårType, UtfallType utfall) {
        this.vilkårType = vilkårType;
        this.utfall = utfall;
    }
}
