package no.nav.familie.ks.sak.app.behandling.domene.grunnlag.personopplysning;


import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.AdresseType;
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.Landkode;
import no.nav.familie.ks.sak.app.behandling.domene.typer.AktørId;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.adresse.Adresseinfo;

import javax.persistence.*;

@Entity(name = "Bostedsadresse")
@Table(name = "PO_BOSTEDSADRESSE")
public class Bostedsadresse extends Adresse {

    Bostedsadresse() { }

    Bostedsadresse(AktørId aktørId, AdresseType adresseType,
                   String adresselinje1, String adresselinje2,
                   String adresselinje3, String adresselinje4,
                   String postnummer, String poststed, Landkode land) {
        super(aktørId, adresseType,
            adresselinje1, adresselinje2,
            adresselinje3, adresselinje4,
            postnummer, poststed, land);
    }

    public static Bostedsadresse opprettNy(AktørId aktørId, Adresseinfo adresseinfo) {
        return new Bostedsadresse(aktørId, adresseinfo.getGjeldendePostadresseType(),
            adresseinfo.getAdresselinje1(), adresseinfo.getAdresselinje2(),
            adresseinfo.getAdresselinje3(), adresseinfo.getAdresselinje4(),
            adresseinfo.getPostNr(), adresseinfo.getPoststed(),
            new Landkode(adresseinfo.getLand()));
    }

}
