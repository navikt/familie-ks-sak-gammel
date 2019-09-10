package no.nav.familie.ks.sak.app.behandling.domene.grunnlag.søknad;

import no.nav.familie.ks.sak.app.behandling.domene.Behandling;
import no.nav.familie.ks.sak.app.behandling.domene.typer.BaseEntitet;

import javax.persistence.*;

@Entity
@Table(name = "GR_SOKNAD")
public class SøknadGrunnlag extends BaseEntitet {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gr_soknad_seq")
    @SequenceGenerator(name = "gr_soknad_seq")
    private Long id;

    @Column(name = "behandling_id", nullable = false, updatable = false)
    private Long behandlingId;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "soknad_id", nullable = false, updatable = false)
    private Søknad søknad;

    @Column(name = "aktiv", nullable = false)
    private boolean aktiv = true;

    SøknadGrunnlag() {
    }

    public SøknadGrunnlag(Behandling behandling, Søknad søknad) {
        this.behandlingId = behandling.getId();
        this.søknad = søknad;
    }

    public Søknad getSøknad() {
        return søknad;
    }

    public boolean isAktiv() {
        return aktiv;
    }

    public Long getBehandlingId() {
        return behandlingId;
    }
}
