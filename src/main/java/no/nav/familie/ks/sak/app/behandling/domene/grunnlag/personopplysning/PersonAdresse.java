package no.nav.familie.ks.sak.app.behandling.domene.grunnlag.personopplysning;

import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.AdresseType;
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.Landkode;
import no.nav.familie.ks.sak.app.behandling.domene.typer.AktørId;
import no.nav.familie.ks.sak.app.behandling.domene.typer.DatoIntervallEntitet;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.adresse.AdressePeriode;

import javax.persistence.*;

@Entity(name = "PersonopplysningAdresse")
@Table(name = "PO_ADRESSE")
public class PersonAdresse extends Adresse {

    @Embedded
    private DatoIntervallEntitet periode;

    @ManyToOne(optional = false)
    @JoinColumn(name = "fk_po_person_id", nullable = false, updatable = false)
    private Person person;

    PersonAdresse() {
    }

    PersonAdresse(AktørId aktørId, AdresseType adresseType,
                  String adresselinje1, String adresselinje2,
                  String adresselinje3, String adresselinje4,
                  String postnummer, String poststed, Landkode land, DatoIntervallEntitet periode) {
        super(aktørId, adresseType,
            adresselinje1, adresselinje2,
            adresselinje3, adresselinje4,
            postnummer, poststed, land);
        this.periode = periode;
    }

    void setPerson(Person person) {
        this.person = person;
    }

    public DatoIntervallEntitet getPeriode() {
        return periode;
    }

    void setPeriode(DatoIntervallEntitet periode) {
        this.periode = periode;
    }

    public static PersonAdresse opprettNy(AktørId aktørId, AdressePeriode adressePeriode) {
        return new PersonAdresse(aktørId, adressePeriode.getAdresse().getAdresseType(),
            adressePeriode.getAdresse().getAdresselinje1(), adressePeriode.getAdresse().getAdresselinje2(),
            adressePeriode.getAdresse().getAdresselinje3(), adressePeriode.getAdresse().getAdresselinje4(),
            adressePeriode.getAdresse().getPostnummer(), adressePeriode.getAdresse().getPoststed(),
            new Landkode(adressePeriode.getAdresse().getLand()),
            DatoIntervallEntitet.fraOgMedTilOgMed(adressePeriode.getPeriode().getFom(), adressePeriode.getPeriode().getTom()));
    }


}
