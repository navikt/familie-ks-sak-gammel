package no.nav.familie.ks.sak.app.behandling.resultat;

import no.nav.familie.ks.sak.app.behandling.GradertPeriode;
import no.nav.familie.ks.sak.app.behandling.SamletVilkårsVurdering;
import no.nav.familie.ks.sak.app.behandling.vilkår.SamletVurdering;

public class Vedtak {

    private Long behandlingsId;
    private GradertPeriode stønadperiode;
    private SamletVurdering vilkårvurdering;

    public Vedtak(SamletVurdering vilkårvurdering, GradertPeriode stønadperiode) {
        this.vilkårvurdering = vilkårvurdering;
        this.stønadperiode = stønadperiode;
    }

    public Vedtak(SamletVurdering vilkårvurdering) {
        this.vilkårvurdering = vilkårvurdering;
    }

    public void setBehandlingsId(Long behandlingsId) {
        this.behandlingsId = behandlingsId;
    }

    public Long getBehandlingsId() {
        return behandlingsId;
    }

    public GradertPeriode getStønadperiode() {
        return stønadperiode;
    }

    public SamletVurdering getVilkårvurdering() { return vilkårvurdering; }
}
