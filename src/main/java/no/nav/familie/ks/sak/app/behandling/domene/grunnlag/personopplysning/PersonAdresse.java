package no.nav.familie.ks.sak.app.behandling.domene.grunnlag.personopplysning;

import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.AdresseType;
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.Landkode;
import no.nav.familie.ks.sak.app.behandling.domene.typer.AktørId;
import no.nav.familie.ks.sak.app.behandling.domene.typer.BaseEntitet;
import no.nav.familie.ks.sak.app.behandling.domene.typer.DatoIntervallEntitet;

import javax.persistence.*;
import java.util.Objects;

@Entity(name = "PersonopplysningAdresse")
@Table(name = "PO_ADRESSE")
public class PersonAdresse extends BaseEntitet {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PO_ADRESSE_SEQ")
    private Long id;

    @Embedded
    @AttributeOverrides(@AttributeOverride(name = "aktørId", column = @Column(name = "aktoer_id", updatable = false)))
    private AktørId aktørId;

    @Embedded
    private DatoIntervallEntitet periode;

    @Enumerated(EnumType.STRING)
    @Column(name = "adresse_type", nullable = false)
    private AdresseType adresseType;

    @Column(name = "adresselinje1")
    private String adresselinje1;

    @Column(name = "adresselinje2")
    private String adresselinje2;

    @Column(name = "adresselinje3")
    private String adresselinje3;

    @Column(name = "adresselinje4")
    private String adresselinje4;

    @Column(name = "postnummer")
    private String postnummer;

    @Column(name = "poststed")
    private String poststed;

    @Embedded
    @AttributeOverrides(@AttributeOverride(name = "kode", column = @Column(name = "land")))
    private Landkode land = Landkode.UDEFINERT;

    @ManyToOne(optional = false)
    @JoinColumn(name = "po_informasjon_id", nullable = false, updatable = false)
    private PersonopplysningerInformasjon personopplysningInformasjon;

    PersonAdresse() {
    }

    PersonAdresse(PersonAdresse adresse) {
        this.adresselinje1 = adresse.getAdresselinje1();
        this.adresselinje2 = adresse.getAdresselinje2();
        this.adresselinje3 = adresse.getAdresselinje3();
        this.adresselinje4 = adresse.getAdresselinje4();
        this.adresseType = adresse.getAdresseType();
        this.postnummer = adresse.getPostnummer();
        this.poststed = adresse.getPoststed();
        this.land = adresse.getLand();

        this.aktørId = adresse.getAktørId();
        this.periode = adresse.getPeriode();
    }

    public PersonAdresse(AktørId aktørId, DatoIntervallEntitet periode) {
        this.aktørId = aktørId;
        this.periode = periode;
    }

    void setPersonopplysningInformasjon(PersonopplysningerInformasjon personopplysningInformasjon) {
        this.personopplysningInformasjon = personopplysningInformasjon;
    }

    public AdresseType getAdresseType() {
        return adresseType;
    }

    void setAdresseType(AdresseType adresseType) {
        this.adresseType = adresseType;
    }

    public PersonAdresse medAdresseType(AdresseType adresseType) {
        this.adresseType = adresseType;
        return this;
    }

    public String getAdresselinje1() {
        return adresselinje1;
    }

    void setAdresselinje1(String adresselinje1) {
        this.adresselinje1 = adresselinje1;
    }

    public PersonAdresse medAdresselinje1(String adresselinje1) {
        this.adresselinje1 = adresselinje1;
        return this;
    }

    public String getAdresselinje2() {
        return adresselinje2;
    }

    void setAdresselinje2(String adresselinje2) {
        this.adresselinje2 = adresselinje2;
    }

    public PersonAdresse medAdresselinje2(String adresselinje2) {
        this.adresselinje2 = adresselinje2;
        return this;
    }

    public String getAdresselinje3() {
        return adresselinje3;
    }

    void setAdresselinje3(String adresselinje3) {
        this.adresselinje3 = adresselinje3;
    }

    public PersonAdresse medAdresselinje3(String adresselinje3) {
        this.adresselinje3 = adresselinje3;
        return this;
    }

    public String getAdresselinje4() {
        return adresselinje4;
    }

    void setAdresselinje4(String adresselinje4) {
        this.adresselinje4 = adresselinje4;
    }

    public PersonAdresse medAdresselinje4(String adresselinje4) {
        this.adresselinje4 = adresselinje4;
        return this;
    }

    public String getPostnummer() {
        return postnummer;
    }

    void setPostnummer(String postnummer) {
        this.postnummer = postnummer;
    }

    public PersonAdresse medPostnummer(String postnummer) {
        this.postnummer = postnummer;
        return this;
    }

    public String getPoststed() {
        return poststed;
    }

    void setPoststed(String poststed) {
        this.poststed = poststed;
    }

    public PersonAdresse medPoststed(String poststed) {
        this.poststed = poststed;
        return this;
    }

    public Landkode getLand() {
        return land;
    }

    void setLand(Landkode land) {
        this.land = land;
    }

    public PersonAdresse medLand(Landkode land) {
        this.land = land;
        return this;
    }

    public AktørId getAktørId() {
        return aktørId;
    }

    void setAktørId(AktørId aktørId) {
        this.aktørId = aktørId;
    }

    public DatoIntervallEntitet getPeriode() {
        return periode;
    }

    void setPeriode(DatoIntervallEntitet periode) {
        this.periode = periode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonAdresse entitet = (PersonAdresse) o;
        return Objects.equals(aktørId, entitet.aktørId) &&
            Objects.equals(periode, entitet.periode) &&
            Objects.equals(adresseType, entitet.adresseType) &&
            Objects.equals(land, entitet.land);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aktørId, periode, adresseType, land);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PersonAdresseEntitet{");
        sb.append("id=").append(id);
        sb.append(", aktørId=").append(aktørId);
        sb.append(", gyldighetsperiode=").append(periode);
        sb.append(", adresseType=").append(adresseType);
        sb.append(", adresselinje1='").append(adresselinje1).append('\'');
        sb.append(", adresselinje2='").append(adresselinje2).append('\'');
        sb.append(", adresselinje3='").append(adresselinje3).append('\'');
        sb.append(", adresselinje4='").append(adresselinje4).append('\'');
        sb.append(", postnummer='").append(postnummer).append('\'');
        sb.append(", poststed='").append(poststed).append('\'');
        sb.append(", land='").append(land).append('\'');
        sb.append('}');
        return sb.toString();
    }

}
