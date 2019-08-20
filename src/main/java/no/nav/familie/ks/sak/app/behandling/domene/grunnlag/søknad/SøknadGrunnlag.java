package no.nav.familie.ks.sak.app.behandling.domene.grunnlag.søknad;

import no.nav.familie.ks.sak.app.behandling.domene.BaseEntitet;
import no.nav.familie.ks.sak.app.behandling.domene.Behandling;

import javax.persistence.*;

@Entity
@Table(name = "GR_SOKNAD")
public class SøknadGrunnlag extends BaseEntitet<Long> {

    @OneToOne
    @JoinColumn(name = "behandling_id", nullable = false, updatable = false, unique = true)
    private Behandling behandling;

    @ManyToOne
    @JoinColumn(name = "soknad_id", nullable = false, updatable = false, unique = true)
    private Søknad søknad;

    @Column(name = "aktiv", nullable = false)
    private boolean aktiv = true;

    SøknadGrunnlag() {
    }

    SøknadGrunnlag(Behandling behandling, Søknad søknad) {
        this.behandling = behandling;
        this.søknad = søknad;
    }

    public Søknad getSøknad() {
        return søknad;
    }

    public boolean isAktiv() {
        return aktiv;
    }
}
