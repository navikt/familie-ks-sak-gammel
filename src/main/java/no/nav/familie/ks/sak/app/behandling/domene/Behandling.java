package no.nav.familie.ks.sak.app.behandling.domene;

import no.nav.familie.ks.sak.app.behandling.domene.typer.BaseEntitet;

import javax.persistence.*;
import java.util.Objects;

@Entity(name = "Behandling")
@Table(name = "BEHANDLING")
public class Behandling extends BaseEntitet {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "behandling_seq")
    @SequenceGenerator(name = "behandling_seq")
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "fagsak_id", nullable = false, updatable = false)
    private Fagsak fagsak;

    @Column(name = "journalpostID")
    private String journalpostID;

    Behandling() {
        // Hibernate
    }

    private Behandling(Fagsak fagsak, String journalpostID) {
        Objects.requireNonNull(fagsak, "behandling må tilknyttes parent Fagsak");
        this.fagsak = fagsak;
        this.journalpostID = journalpostID;
    }

    public static Behandling.Builder forFørstegangssøknad(Fagsak fagsak, String journalpostID) {
        return new Builder(fagsak, journalpostID);
    }

    @Override
    public String toString() {
        return "behandling{" +
                "id=" + id +
                "fagsak=" + fagsak +
                "journalpostID=" + journalpostID +
                '}';
    }

    public Fagsak getFagsak() {
        return fagsak;
    }

    public String getJournalpostID() {
        return journalpostID;
    }

    public Long getId() {
        return id;
    }

    public static class Builder {
        private Fagsak fagsak;
        private String journalpostID;


        private Builder(Fagsak fagsak, String journalpostID) {
            Objects.requireNonNull(fagsak, "fagsak"); //$NON-NLS-1$
            this.fagsak = fagsak;
            this.journalpostID = journalpostID;
        }

        public Behandling build() {
            return new Behandling(fagsak, journalpostID);
        }
    }
}
