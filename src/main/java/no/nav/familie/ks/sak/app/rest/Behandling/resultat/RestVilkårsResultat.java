package no.nav.familie.ks.sak.app.rest.Behandling.resultat;

import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.UtfallType;
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.VilkårType;

public class RestVilkårsResultat {
    public VilkårType vilkårType;
    public UtfallType utfall;

    public RestVilkårsResultat(VilkårType vilkårType, UtfallType utfall) {
        this.vilkårType = vilkårType;
        this.utfall = utfall;
    }
}
