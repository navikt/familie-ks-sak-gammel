package no.nav.familie.ks.sak.app.behandling.resultat;

import no.nav.familie.ks.sak.app.behandling.GradertPeriode;
import no.nav.familie.ks.sak.app.behandling.SamletVilkårsVurdering;
import no.nav.familie.ks.sak.app.behandling.vilkår.SamletVurdering;

public class Vedtak {

    private Long behandlingsId;
    private Long fagsakId;
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

    public Long getFagsakId() {
        return fagsakId;
    }

    public void setFagsakId(Long fagsakId) {
        this.fagsakId = fagsakId;
    }

    public GradertPeriode getStønadperiode() {
        return stønadperiode;
    }

    public SamletVurdering getVilkårvurdering() { return vilkårvurdering; }
}
