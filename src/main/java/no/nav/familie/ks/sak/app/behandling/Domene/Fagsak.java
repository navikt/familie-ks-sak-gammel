package no.nav.familie.ks.sak.app.behandling.Domene;

import javax.persistence.*;

@Entity(name = "Fagsak")
@Table(name = "FAGSAK")
public class Fagsak extends BaseEntitet {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_FAGSAK")
    private Long id;

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
}
