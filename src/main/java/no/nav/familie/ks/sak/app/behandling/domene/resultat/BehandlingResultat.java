package no.nav.familie.ks.sak.app.behandling.domene.resultat;

import no.nav.familie.ks.sak.app.behandling.domene.Behandling;
import no.nav.familie.ks.sak.app.behandling.domene.resultat.vilkår.SamletVilkårResultat;
import no.nav.familie.ks.sak.app.behandling.domene.typer.BaseEntitet;

import javax.persistence.*;

@Entity
@Table(name = "BEHANDLING_RESULTAT")
public class BehandlingResultat extends BaseEntitet {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "behandling_resultat_seq")
    @SequenceGenerator(name = "behandling_resultat_seq")
    private Long id;

    @Column(name = "behandling_id", nullable = false, updatable = false)
    private Long behandlingId;

    @ManyToOne
    @JoinColumn(name = "samlet_vilkar_resultat_id", updatable = false)
    private SamletVilkårResultat samletVilkårResultat;

    @Column(name = "aktiv")
    private boolean aktiv = true;

    BehandlingResultat() {
    }

    public BehandlingResultat(Behandling behandling, SamletVilkårResultat samletVilkårResultat) {
        this.behandlingId = behandling.getId();
        this.samletVilkårResultat = samletVilkårResultat;
    }

    public SamletVilkårResultat getVilkårsResultat() {
        return samletVilkårResultat;
    }

    public Long getBehandlingId() {
        return behandlingId;
    }

    public boolean isAktiv() {
        return aktiv;
    }

    @Override
    public String toString() {
        return "BehandlingResultat{" +
                "id=" + id +
                ", behandlingId=" + behandlingId +
                ", samletVilkårResultat=" + samletVilkårResultat +
                ", aktiv=" + aktiv +
                '}';
    }
}
