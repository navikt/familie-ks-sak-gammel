package no.nav.familie.ks.sak.app.behandling.domene.søknad;

import no.nav.familie.ks.sak.app.behandling.domene.BaseEntitet;
import no.nav.familie.ks.sak.app.grunnlag.Søknad;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "SO_SOKNAD")
public class SøknadEntitet extends BaseEntitet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "innsendt_tidspunkt", nullable = false, updatable = false)
    private LocalDate innsendtTidspunkt;

    @OneToOne
    @JoinColumn(name = "OPPGITT_UTLAND_ID", updatable = false, nullable = false)
    private OppgittUtlandsTilknytning utlandsTilknytning;

    SøknadEntitet() {
        // hibernate
    }

    /**
     * Deep copy.
     */
    SøknadEntitet(Søknad søknad) {
        this.innsendtTidspunkt = LocalDate.from(søknad.innsendingsTidspunkt);
    }
}
