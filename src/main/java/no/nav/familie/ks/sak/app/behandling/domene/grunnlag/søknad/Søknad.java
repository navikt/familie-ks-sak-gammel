package no.nav.familie.ks.sak.app.behandling.domene.grunnlag.søknad;

import no.nav.familie.ks.sak.app.behandling.domene.typer.BaseEntitet;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "SO_SOKNAD")
public class Søknad extends BaseEntitet {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "so_soknad_seq")
    @SequenceGenerator(name = "so_soknad_seq")
    private Long id;

    @Column(name = "SOKER_FNR", nullable = false, updatable = false)
    private String søkerFødselsnummer;

    @Column(name = "OPPGITT_ANNENPART_FNR", nullable = false, updatable = false)
    private String oppgittAnnenPartFødselsnummer;

    @Column(name = "innsendt_tidspunkt", nullable = false, updatable = false)
    private LocalDateTime innsendtTidspunkt;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "OPPGITT_UTLAND_ID", updatable = false, nullable = false)
    private OppgittUtlandsTilknytning utlandsTilknytning;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "OPPGITT_ERKLAERING_ID", updatable = false, nullable = false)
    private OppgittErklæring erklæring;

    Søknad() {
        // hibernate
    }

    public Søknad(LocalDateTime innsendtTidspunkt, String søkerFødselsnummer, String oppgittAnnenPartFødselsnummer, OppgittUtlandsTilknytning utlandsTilknytning, OppgittErklæring erklæring) {
        this.søkerFødselsnummer = søkerFødselsnummer;
        this.oppgittAnnenPartFødselsnummer = oppgittAnnenPartFødselsnummer;
        this.innsendtTidspunkt = innsendtTidspunkt;
        this.utlandsTilknytning = utlandsTilknytning;
        this.erklæring = erklæring;
    }

    public String getSøkerFødselsnummer() {
        return søkerFødselsnummer;
    }

    public String getOppgittAnnenPartFødselsnummer() {
        return oppgittAnnenPartFødselsnummer;
    }

    public OppgittErklæring getErklæring() {
        return erklæring;
    }

    public LocalDateTime getInnsendtTidspunkt() {
        return innsendtTidspunkt;
    }

    public OppgittUtlandsTilknytning getUtlandsTilknytning() {
        return utlandsTilknytning;
    }

    @Override
    public String toString() {
        return "Søknad{" +
                "id=" + id +
                "innsendtTidspunkt=" + innsendtTidspunkt +
                ", utlandsTilknytning=" + utlandsTilknytning +
                ", erklæring=" + erklæring +
                '}';
    }
}
