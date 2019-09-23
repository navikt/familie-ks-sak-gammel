package no.nav.familie.ks.sak.app.behandling.domene.grunnlag.barnehagebarn;

import no.nav.familie.ks.sak.app.behandling.domene.Behandling;
import no.nav.familie.ks.sak.app.behandling.domene.typer.BaseEntitet;

import javax.persistence.*;

@Entity
@Table(name = "GR_BARNEHAGE_BARN")
public class BarnehageBarnGrunnlag extends BaseEntitet {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gr_barnehage_barn_seq")
    @SequenceGenerator(name = "gr_barnehage_barn_seq")
    private Long id;

    @Column(name = "behandling_id", nullable = false, updatable = false)
    private Long behandlingId;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "OPPGITT_FAMILIEFORHOLD_ID")
    private OppgittFamilieforhold familieforhold;

    @Column(name = "aktiv", nullable = false)
    private boolean aktiv = true;

    BarnehageBarnGrunnlag() {
    }

    public BarnehageBarnGrunnlag(Behandling behandling, OppgittFamilieforhold familieforhold) {
        this.behandlingId = behandling.getId();
        this.familieforhold = familieforhold;
    }

    public BarnehageBarnGrunnlag(Long behandlingId, OppgittFamilieforhold familieforhold) {
        this.behandlingId = behandlingId;
        this.familieforhold = familieforhold;
    }

    public OppgittFamilieforhold getFamilieforhold() {
        return familieforhold;
    }

    @Override
    public String toString() {
        return "BarnehageBarnGrunnlag{" +
                "id=" + id +
                "familieforhold=" + familieforhold +
                ", aktiv=" + aktiv +
                '}';
    }

    public Long getBehandlingId() {
        return behandlingId;
    }

    public boolean isAktiv() {
        return aktiv;
    }
}
