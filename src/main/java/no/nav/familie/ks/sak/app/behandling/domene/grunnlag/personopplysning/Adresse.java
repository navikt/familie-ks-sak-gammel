package no.nav.familie.ks.sak.app.behandling.domene.grunnlag.personopplysning;

import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.AdresseType;
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.Landkode;
import no.nav.familie.ks.sak.app.behandling.domene.typer.AktørId;
import no.nav.familie.ks.sak.app.behandling.domene.typer.BaseEntitet;

import javax.persistence.*;
import java.util.Objects;

@MappedSuperclass
public abstract class Adresse extends BaseEntitet {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PO_ADRESSE_SEQ")
    private Long id;

    @Embedded
    @AttributeOverrides(@AttributeOverride(name = "aktørId", column = @Column(name = "aktoer_id", updatable = false)))
    private AktørId aktørId;

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

    Adresse() {

    }

    Adresse(AktørId aktørId, AdresseType adresseType, String adresselinje1, String adresselinje2, String adresselinje3, String adresselinje4,
            String postnummer, String poststed, Landkode land) {
        this.aktørId = aktørId;
        this.adresseType = adresseType;
        this.adresselinje1 = adresselinje1;
        this.adresselinje2 = adresselinje2;
        this.adresselinje3 = adresselinje3;
        this.adresselinje4 = adresselinje4;
        this.postnummer = postnummer;
        this.poststed = poststed;
        this.land = land;
    }

    public AdresseType getAdresseType() {
        return adresseType;
    }

    void setAdresseType(AdresseType adresseType) {
        this.adresseType = adresseType;
    }

    public String getAdresselinje1() {
        return adresselinje1;
    }

    void setAdresselinje1(String adresselinje1) {
        this.adresselinje1 = adresselinje1;
    }

    public String getAdresselinje2() {
        return adresselinje2;
    }

    void setAdresselinje2(String adresselinje2) {
        this.adresselinje2 = adresselinje2;
    }

    public String getAdresselinje3() {
        return adresselinje3;
    }

    void setAdresselinje3(String adresselinje3) {
        this.adresselinje3 = adresselinje3;
    }

    public String getAdresselinje4() {
        return adresselinje4;
    }

    void setAdresselinje4(String adresselinje4) {
        this.adresselinje4 = adresselinje4;
    }

    public String getPostnummer() {
        return postnummer;
    }

    void setPostnummer(String postnummer) {
        this.postnummer = postnummer;
    }

    public String getPoststed() {
        return poststed;
    }

    void setPoststed(String poststed) {
        this.poststed = poststed;
    }

    public Landkode getLand() {
        return land;
    }

    void setLand(Landkode land) {
        this.land = land;
    }

    public AktørId getAktørId() {
        return aktørId;
    }

    void setAktørId(AktørId aktørId) {
        this.aktørId = aktørId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Adresse entitet = (Adresse) o;
        return Objects.equals(aktørId, entitet.aktørId) &&
            Objects.equals(adresseType, entitet.adresseType) &&
            Objects.equals(land, entitet.land);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aktørId, adresseType, land);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PersonAdresseEntitet{");
        sb.append("id=").append(id);
        sb.append(", aktørId=").append(aktørId);
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
