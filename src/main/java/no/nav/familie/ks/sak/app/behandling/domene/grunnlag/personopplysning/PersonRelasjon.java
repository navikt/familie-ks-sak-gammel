package no.nav.familie.ks.sak.app.behandling.domene.grunnlag.personopplysning;

import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.RelasjonsRolleType;
import no.nav.familie.ks.sak.app.behandling.domene.typer.AktørId;
import no.nav.familie.ks.sak.app.behandling.domene.typer.BaseEntitet;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.PersonIdent;

import javax.persistence.*;
import java.util.Objects;

@Entity(name = "PersonopplysningRelasjon")
@Table(name = "PO_RELASJON")
public class PersonRelasjon extends BaseEntitet {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PO_RELASJON_SEQ")
    private Long id;

    @Embedded
    @AttributeOverrides(@AttributeOverride(name = "aktørId", column = @Column(name = "fra_aktoer_id", updatable = false)))
    private AktørId fraAktørId;

    @Embedded
    @AttributeOverrides(@AttributeOverride(name = "aktørId", column = @Column(name = "til_aktoer_id", updatable = false)))
    private AktørId tilAktørId;

    @Embedded
    @AttributeOverrides(@AttributeOverride(name = "ident", column = @Column(name = "fra_person_ident", updatable = false, nullable = false)))
    private PersonIdent fraPersonIdent;

    @Embedded
    @AttributeOverrides(@AttributeOverride(name = "ident", column = @Column(name = "til_person_ident", updatable = false, nullable = false)))
    private PersonIdent tilPersonIdent;

    @Enumerated(EnumType.STRING)
    @Column(name = "relasjonsrolle")
    private RelasjonsRolleType relasjonsrolle;

    @Column(name = "har_samme_bosted")
    private Boolean harSammeBosted;

    @ManyToOne(optional = false)
    @JoinColumn(name = "fk_po_person_id", nullable = false, updatable = false)
    private Person person;

    PersonRelasjon() {
    }

    PersonRelasjon(PersonRelasjon relasjon) {
        this.fraAktørId = relasjon.getFraAktørId();
        this.tilAktørId = relasjon.getTilAktørId();
        this.relasjonsrolle = relasjon.getRelasjonsrolle();
        this.harSammeBosted = relasjon.getHarSammeBosted();
    }

    public PersonRelasjon(AktørId fraAktørId,
                          AktørId tilAktørId,
                          PersonIdent fraPersonIdent,
                          PersonIdent tilPersonIdent,
                          RelasjonsRolleType relasjonsrolle,
                          Boolean harSammeBosted) {
        this.fraAktørId = fraAktørId;
        this.tilAktørId = tilAktørId;
        this.fraPersonIdent = fraPersonIdent;
        this.tilPersonIdent = tilPersonIdent;
        this.relasjonsrolle = relasjonsrolle;
        this.harSammeBosted = harSammeBosted;
    }

    void setPerson(Person person) {
        this.person = person;
    }

    public AktørId getFraAktørId() {
        return fraAktørId;
    }

    void setFraAktørId(AktørId fraAktørId) {
        this.fraAktørId = fraAktørId;
    }

    public AktørId getTilAktørId() {
        return tilAktørId;
    }

    void setTilAktørId(AktørId tilAktørId) {
        this.tilAktørId = tilAktørId;
    }

    public PersonIdent getFraPersonIdent() {
        return fraPersonIdent;
    }

    public PersonIdent getTilPersonIdent() {
        return tilPersonIdent;
    }

    public void setFraPersonIdent(PersonIdent personIdent) {
        this.fraPersonIdent = personIdent;
    }

    public void setTilPersonIdent(PersonIdent personIdent) {
        this.tilPersonIdent = personIdent;
    }

    public RelasjonsRolleType getRelasjonsrolle() {
        return relasjonsrolle;
    }

    void setRelasjonsrolle(RelasjonsRolleType relasjonsrolle) {
        this.relasjonsrolle = relasjonsrolle;
    }

    public Boolean getHarSammeBosted() {
        return harSammeBosted;
    }

    void setHarSammeBosted(Boolean harSammeBosted) {
        this.harSammeBosted = harSammeBosted;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PersonRelasjonEntitet{");
        sb.append("fraAktør=").append(fraAktørId);
        sb.append("tilAktør=").append(tilAktørId);
        sb.append("relasjonsrolle=").append(relasjonsrolle);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonRelasjon entitet = (PersonRelasjon) o;
        return Objects.equals(fraAktørId, entitet.fraAktørId) &&
            Objects.equals(tilAktørId, entitet.tilAktørId) &&
            Objects.equals(harSammeBosted, entitet.harSammeBosted) &&
            Objects.equals(relasjonsrolle, entitet.relasjonsrolle);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fraAktørId, tilAktørId, harSammeBosted, relasjonsrolle);
    }
}
