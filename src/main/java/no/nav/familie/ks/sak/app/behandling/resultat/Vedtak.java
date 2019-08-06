package no.nav.familie.ks.sak.app.behandling.resultat;

import no.nav.familie.ks.sak.app.behandling.fastsetting.Vilkårvurdering;
import no.nav.familie.ks.sak.app.behandling.GradertPeriode;

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
