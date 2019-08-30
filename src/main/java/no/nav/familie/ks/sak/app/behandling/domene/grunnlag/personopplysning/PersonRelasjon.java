package no.nav.familie.ks.sak.app.behandling.domene.grunnlag.personopplysning;

import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.RelasjonsRolleType;
import no.nav.familie.ks.sak.app.behandling.domene.typer.AktørId;
import no.nav.familie.ks.sak.app.behandling.domene.typer.BaseEntitet;

import javax.persistence.*;
import java.util.Objects;

@Entity(name = "PersonopplysningRelasjon")
@Table(name = "PO_RELASJON")
public class PersonRelasjon extends BaseEntitet {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_PO_RELASJON")
    private Long id;

    @Embedded
    @AttributeOverrides(@AttributeOverride(name = "aktørId", column = @Column(name = "fra_aktoer_id", updatable = false, nullable = false)))
    private AktørId fraAktørId;

    @Embedded
    @AttributeOverrides(@AttributeOverride(name = "aktørId", column = @Column(name = "til_aktoer_id", updatable = false, nullable = false)))
    private AktørId tilAktørId;

    @Enumerated(EnumType.STRING)
    @Column(name = "relasjonsrolle")
    private RelasjonsRolleType relasjonsrolle;

    @Column(name = "har_samme_bosted")
    private Boolean harSammeBosted;

    @ManyToOne(optional = false)
    @JoinColumn(name = "po_informasjon_id", nullable = false, updatable = false)
    private PersonInformasjon personopplysningInformasjon;

    PersonRelasjon() {
    }

    PersonRelasjon(PersonRelasjon relasjon) {
        this.fraAktørId = relasjon.getFraAktørId();
        this.tilAktørId = relasjon.getTilAktørId();
        this.relasjonsrolle = relasjon.getRelasjonsrolle();
        this.harSammeBosted = relasjon.getHarSammeBosted();
    }

    void setFraAktørId(AktørId fraAktørId) {
        this.fraAktørId = fraAktørId;
    }

    void setPersonopplysningInformasjon(PersonInformasjon personopplysningInformasjon) {
        this.personopplysningInformasjon = personopplysningInformasjon;
    }

    public AktørId getFraAktørId() {
        return fraAktørId;
    }

    public AktørId getTilAktørId() {
        return tilAktørId;
    }

    void setTilAktørId(AktørId tilAktørId) {
        this.tilAktørId = tilAktørId;
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
