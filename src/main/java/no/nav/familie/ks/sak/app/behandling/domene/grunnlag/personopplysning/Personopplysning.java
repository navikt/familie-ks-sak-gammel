package no.nav.familie.ks.sak.app.behandling.domene.grunnlag.personopplysning;

import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.Landkode;
import no.nav.familie.ks.sak.app.behandling.domene.typer.AktørId;
import no.nav.familie.ks.sak.app.behandling.domene.typer.BaseEntitet;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity(name = "Personopplysning")
@Table(name = "PO_PERSONOPPLYSNING")
public class Personopplysning extends BaseEntitet {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PO_PERSONOPPLYSNING_SEQ")
    private Long id;

    @Embedded
    @AttributeOverrides(@AttributeOverride(name = "aktørId", column = @Column(name = "aktoer_id", updatable = false)))
    private AktørId aktørId;

    @Column(name = "navn")
    private String navn;

    @Column(name = "foedselsdato", nullable = false)
    private LocalDate fødselsdato;

    @Column(name = "doedsdato")
    private LocalDate dødsdato;

    @Embedded
    @AttributeOverrides(@AttributeOverride(name = "kode", column = @Column(name = "statsborgerskap")))
    private Landkode statsborgerskap = Landkode.UDEFINERT;

    @ManyToOne(optional = false)
    @JoinColumn(name = "po_informasjon_id", nullable = false, updatable = false)
    private PersonopplysningerInformasjon personopplysningInformasjon;

    Personopplysning() {
    }

    Personopplysning(Personopplysning personopplysning) {
        this.aktørId = personopplysning.getAktørId();
        this.navn = personopplysning.getNavn();
        this.fødselsdato = personopplysning.getFødselsdato();
        this.dødsdato = personopplysning.getDødsdato();
        this.statsborgerskap = personopplysning.getStatsborgerskap();
    }

    public Personopplysning(AktørId aktørId) {
        this.aktørId = aktørId;
    }

    void setPersonopplysningInformasjon(PersonopplysningerInformasjon personopplysningInformasjon) {
        this.personopplysningInformasjon = personopplysningInformasjon;
    }

    public AktørId getAktørId() {
        return aktørId;
    }

    void setAktørId(AktørId aktørId) {
        this.aktørId = aktørId;
    }

    public Landkode getStatsborgerskap() {
        return statsborgerskap;
    }

    void setStatsborgerskap(Landkode statsborgerskap) {
        this.statsborgerskap = statsborgerskap;
    }

    public Personopplysning medStatsborgerskap(Landkode statsborgerskap) {
        this.statsborgerskap = statsborgerskap;
        return this;
    }

    public String getNavn() {
        return navn;
    }

    void setNavn(String navn) {
        this.navn = navn;
    }

    public Personopplysning medNavn(String navn) {
        this.navn = navn;
        return this;
    }

    public LocalDate getFødselsdato() {
        return fødselsdato;
    }

    void setFødselsdato(LocalDate fødselsdato) {
        this.fødselsdato = fødselsdato;
    }

    public Personopplysning medFødselsdato(LocalDate fødselsdato) {
        this.fødselsdato = fødselsdato;
        return this;
    }

    public LocalDate getDødsdato() {
        return dødsdato;
    }

    void setDødsdato(LocalDate dødsdato) {
        this.dødsdato = dødsdato;
    }

    public Personopplysning medDødsdato(LocalDate dødsdato) {
        this.dødsdato = dødsdato;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Personopplysning entitet = (Personopplysning) o;
        return Objects.equals(aktørId, entitet.aktørId) &&
            Objects.equals(navn, entitet.navn) &&
            Objects.equals(fødselsdato, entitet.fødselsdato) &&
            Objects.equals(dødsdato, entitet.dødsdato);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aktørId, navn, fødselsdato, dødsdato);
    }

    @Override
    public String toString() {
        return "PersonopplysningEntitet{" + "id=" + id +
            ", navn='" + navn + '\'' +
            ", fødselsdato=" + fødselsdato +
            ", dødsdato=" + dødsdato +
            '}';
    }
}
