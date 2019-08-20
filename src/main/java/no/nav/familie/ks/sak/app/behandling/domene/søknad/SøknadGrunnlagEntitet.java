package no.nav.familie.ks.sak.app.behandling.domene.søknad;

import no.nav.familie.ks.sak.app.behandling.domene.BaseEntitet;
import no.nav.familie.ks.sak.app.behandling.domene.Behandling;

import javax.persistence.*;

@Entity(name = "SøknadGrunnlag")
@Table(name = "GR_SOKNAD")
public class SøknadGrunnlagEntitet extends BaseEntitet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "behandling_id", nullable = false, updatable = false, unique = true)
    private Behandling behandling;

    @OneToOne
    @JoinColumn(name = "soknad_id", nullable = false, updatable = false, unique = true)
    private SøknadEntitet søknad;

    @Column(name = "aktiv", nullable = false)
    private boolean aktiv = true;

    SøknadGrunnlagEntitet() {
    }

    SøknadGrunnlagEntitet(Behandling behandling, SøknadEntitet søknad) {
        this.behandling = behandling;
        this.søknad = søknad;
    }

    public SøknadEntitet getSøknad() {
        return søknad;
    }

    public boolean isAktiv() {
        return aktiv;
    }
}
