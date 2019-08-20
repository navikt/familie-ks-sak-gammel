package no.nav.familie.ks.sak.app.behandling.domene.grunnlag.søknad;

import no.nav.familie.ks.sak.app.behandling.domene.BaseEntitet;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "SO_SOKNAD")
public class Søknad extends BaseEntitet<Long> {

    @Column(name = "innsendt_tidspunkt", nullable = false, updatable = false)
    private LocalDate innsendtTidspunkt;

    @OneToOne
    @JoinColumn(name = "OPPGITT_UTLAND_ID", updatable = false, nullable = false)
    private OppgittUtlandsTilknytning utlandsTilknytning;

    Søknad() {
        // hibernate
    }

    /**
     * Deep copy.
     */
    Søknad(LocalDate innsendingstidspunkt) {
        this.innsendtTidspunkt = innsendingstidspunkt;
    }

    public LocalDate getInnsendtTidspunkt() {
        return innsendtTidspunkt;
    }

    public OppgittUtlandsTilknytning getUtlandsTilknytning() {
        return utlandsTilknytning;
    }
}
