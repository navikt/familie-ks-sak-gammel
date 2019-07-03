package no.nav.familie.ks.sak.resultat;

import no.nav.familie.ks.sak.behandling.grunnlag.Vilkårvurdering;
import no.nav.familie.ks.sak.felles.GradertPeriode;

public class Vedtak {

    private GradertPeriode stønadperiode;
    private Vilkårvurdering vilkårvurdering;

    public Vedtak(Vilkårvurdering vilkårvurdering, GradertPeriode stønadperiode) {
        this.vilkårvurdering = vilkårvurdering;
        this.stønadperiode = stønadperiode;
    }

    public Vedtak(Vilkårvurdering vilkårvurdering) {
        this.vilkårvurdering = vilkårvurdering;
    }

    public GradertPeriode getStønadperiode() {
        return stønadperiode;
    }

    public Vilkårvurdering getVilkårvurdering() { return vilkårvurdering; }
}
