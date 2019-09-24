package no.nav.familie.ks.sak.app.rest.Behandling.grunnlag.personinformasjon;

import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.personopplysning.PersonAdresse;
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.AdresseType;
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.Landkode;
import no.nav.familie.ks.sak.app.behandling.domene.typer.DatoIntervallEntitet;

public class RestAdresseinfo {
    public AdresseType adresseType;
    public String adresselinje1;
    public String adresselinje2;
    public String adresselinje3;
    public String adresselinje4;
    public String postnummer;
    public String poststed;
    public Landkode land;
    public DatoIntervallEntitet periode;

    public RestAdresseinfo(PersonAdresse adresse) {
        this.adresseType = adresse.getAdresseType();
        this.adresselinje1 = adresse.getAdresselinje1();
        this.adresselinje2 = adresse.getAdresselinje2();
        this.adresselinje3 = adresse.getAdresselinje3();
        this.adresselinje4 = adresse.getAdresselinje4();
        this.postnummer = adresse.getPostnummer();
        this.poststed = adresse.getPoststed();
        this.land = adresse.getLand();
        this.periode = adresse.getPeriode();
    }
}
