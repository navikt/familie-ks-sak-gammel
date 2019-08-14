package no.nav.familie.ks.sak.app.behandling.resultat;

import no.nav.familie.ks.sak.app.behandling.GradertPeriode;
import no.nav.familie.ks.sak.app.behandling.SamletVilkårsVurdering;

public class Vedtak {

    private GradertPeriode stønadperiode;
    private SamletVilkårsVurdering vilkårvurdering;

    public Vedtak(SamletVilkårsVurdering vilkårvurdering, GradertPeriode stønadperiode) {
        this.vilkårvurdering = vilkårvurdering;
        this.stønadperiode = stønadperiode;
    }

    public Vedtak(SamletVilkårsVurdering vilkårvurdering) {
        this.vilkårvurdering = vilkårvurdering;
    }

    public GradertPeriode getStønadperiode() {
        return stønadperiode;
    }

    public SamletVilkårsVurdering getVilkårvurdering() { return vilkårvurdering; }
}
