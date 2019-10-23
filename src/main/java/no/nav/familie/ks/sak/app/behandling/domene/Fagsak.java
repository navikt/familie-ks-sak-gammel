package no.nav.familie.ks.sak.app.behandling.domene;

import no.nav.familie.ks.sak.app.behandling.domene.typer.AktørId;
import no.nav.familie.ks.sak.app.behandling.domene.typer.BaseEntitet;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.PersonIdent;

import javax.persistence.*;
import java.util.Objects;

@Entity(name = "Fagsak")
@Table(name = "FAGSAK")
public class Fagsak extends BaseEntitet {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fagsak_seq")
    @SequenceGenerator(name = "fagsak_seq")
    private Long id;

    @Embedded
    @AttributeOverrides(@AttributeOverride(name = "aktørId", column = @Column(name = "aktoer_id", updatable = false)))
    private AktørId aktørId;

    @Embedded
    @AttributeOverrides(@AttributeOverride(name = "ident", column = @Column(name = "person_ident", updatable = false)))
    private PersonIdent personIdent;

    /**
     * Offisielt tildelt saksnummer fra GSAK.
     */
    @Column(name = "saksnummer", nullable = false)
    private String saksnummer;

    Fagsak() {
        // Hibernate
    }

    private Fagsak(AktørId aktørId, PersonIdent personIdent) {
        this(aktørId, personIdent, null);
    }

    public Fagsak(AktørId aktørId, PersonIdent personIdent, String saksnummer) {
        this.aktørId = aktørId;
        this.personIdent = personIdent;
        if (saksnummer != null) {
            this.saksnummer = saksnummer;
        }
    }

    public static Fagsak opprettNy(AktørId aktørId, PersonIdent personIdent) {
        return new Fagsak(aktørId, personIdent);
    }

    public static Fagsak opprettNy(AktørId aktørId, PersonIdent personIdent, String saksnummer) {
        return new Fagsak(aktørId, personIdent, saksnummer);
    }

    public Long getId() {
        return id;
    }

    public AktørId getAktørId() {
        return aktørId;
    }

    public PersonIdent getPersonIdent() {
        return personIdent;
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
                "id=" + id +
                ", aktørId='" + aktørId + '\'' +
                ", saksnummer='" + saksnummer + '\'' +
                '}';
    }
}
