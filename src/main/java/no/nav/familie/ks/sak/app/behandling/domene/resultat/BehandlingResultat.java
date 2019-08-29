package no.nav.familie.ks.sak.app.behandling.domene.resultat;

import no.nav.familie.ks.sak.app.behandling.domene.BaseEntitet;
import no.nav.familie.ks.sak.app.behandling.domene.Behandling;
import no.nav.familie.ks.sak.app.behandling.domene.resultat.vilkår.VilkårsResultat;

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
    @JoinColumn(name = "vilkars_resultat_id", updatable = false)
    private VilkårsResultat vilkårsResultat;

    @Column(name = "aktiv")
    private boolean aktiv = true;

    BehandlingResultat() {
    }

    public BehandlingResultat(Behandling behandling, VilkårsResultat vilkårsResultat) {
        this.behandlingId = behandling.getId();
        this.vilkårsResultat = vilkårsResultat;
    }

    public VilkårsResultat getVilkårsResultat() {
        return vilkårsResultat;
    }

    public boolean isAktiv() {
        return aktiv;
    }

    @Override
    public String toString() {
        return "BehandlingResultat{" +
                "id=" + id +
                ", behandlingId=" + behandlingId +
                ", vilkårsResultat=" + vilkårsResultat +
                ", aktiv=" + aktiv +
                '}';
    }
}
