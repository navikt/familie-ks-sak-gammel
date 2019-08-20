package no.nav.familie.ks.sak.app.behandling.domene;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

@Entity(name = "Fagsak")
@Table(name = "FAGSAK")
public class Fagsak extends BaseEntitet<Long> {

    @Column(name = "aktoer_id", nullable = false)
    private String aktørId;

    /**
     * Offisielt tildelt saksnummer fra GSAK.
     */
    @Column(name = "saksnummer", nullable = false)
    private String saksnummer;

    Fagsak() {
        // Hibernate
    }

    private Fagsak(String aktørId) {
        this(aktørId, null);
    }

    public Fagsak(String aktørId, String saksnummer) {
        this.aktørId = aktørId;
        if (saksnummer != null) {
            this.saksnummer = saksnummer;
        }
    }

    public static Fagsak opprettNy(String aktørId) {
        return new Fagsak(aktørId);
    }

    public static Fagsak opprettNy(String aktørId, String saksnummer) {
        return new Fagsak(aktørId, saksnummer);
    }

    public String getAktørId() {
        return aktørId;
    }

    public String getSaksnummer() {
        return saksnummer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Fagsak fagsak = (Fagsak) o;
        return Objects.equals(aktørId, fagsak.aktørId) &&
                Objects.equals(saksnummer, fagsak.saksnummer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aktørId, saksnummer);
    }

    @Override
    public String toString() {
        return "Fagsak{" +
                "id=" + getId() +
                ", aktørId='" + aktørId + '\'' +
                ", saksnummer='" + saksnummer + '\'' +
                '}';
    }
}
