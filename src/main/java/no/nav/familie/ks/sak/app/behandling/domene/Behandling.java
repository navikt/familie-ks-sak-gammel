package no.nav.familie.ks.sak.app.behandling.domene;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Objects;

@Entity(name = "Behandling")
@Table(name = "BEHANDLING")
public class Behandling extends BaseEntitet<Long> {

    @ManyToOne(optional = false)
    @JoinColumn(name = "fagsak_id", nullable = false, updatable = false)
    private Fagsak fagsak;

    Behandling() {
        // Hibernate
    }

    private Behandling(Fagsak fagsak) {
        Objects.requireNonNull(fagsak, "Behandling må tilknyttes parent Fagsak");
        this.fagsak = fagsak;
    }

    public static Behandling.Builder forFørstegangssøknad(Fagsak fagsak) {
        return new Builder(fagsak);
    }

    @Override
    public String toString() {
        return "Behandling{" +
                "id=" + getId() +
                "fagsak=" + fagsak +
                '}';
    }

    public Fagsak getFagsak() {
        return fagsak;
    }

    public static class Builder {
        private Fagsak fagsak;


        private Builder(Fagsak fagsak) {
            Objects.requireNonNull(fagsak, "fagsak"); //$NON-NLS-1$
            this.fagsak = fagsak;
        }

        public Behandling build() {
            return new Behandling(fagsak);
        }
    }
}
