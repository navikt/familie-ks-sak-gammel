package no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.adresse;

import static java.util.Objects.requireNonNull;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;

import no.nav.familie.ks.sak.app.integrasjon.felles.ws.DateUtil;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.Periode;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.PersonhistorikkInfo;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.TpsUtil;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.status.PersonstatusType;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Person;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Gateadresse;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Kodeverdi;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Landkoder;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Matrikkeladresse;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.MidlertidigPostadresseNorge;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.MidlertidigPostadresseUtland;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Personstatus;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.PostboksadresseNorsk;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.StedsadresseNorge;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.StrukturertAdresse;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.UstrukturertAdresse;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonhistorikkResponse;

@Component
@ApplicationScope
public class TpsAdresseOversetter {

    private static final String NORGE = "NOR";
    private static final Logger log = LoggerFactory.getLogger(TpsAdresseOversetter.class);
    private static final String HARDKODET_POSTNR = "XXXX";
    private static final String HARDKODET_POSTSTED = "UDEFINERT";
    private static final String POSTNUMMER_POSTSTED = "^\\d{4} \\D*";  // Mønster for postnummer og poststed, f.eks. "0034 OSLO"

    TpsAdresseOversetter() {
        // for CDI proxy
    }

    public void konverterBostedadressePerioder(HentPersonhistorikkResponse response, PersonhistorikkInfo.Builder builder) {
        if (Optional.ofNullable(response.getBostedsadressePeriodeListe()).isPresent()) {
            response.getBostedsadressePeriodeListe().forEach(e -> {
                StrukturertAdresse strukturertAdresse = e.getBostedsadresse().getStrukturertAdresse();
                Periode periode =
                        Periode
                                .innenfor(DateUtil.convertToLocalDate(e.getPeriode().getFom()), DateUtil.convertToLocalDate(e.getPeriode().getTom()));

                AdressePeriode adressePeriode = konverterStrukturertAdresse(strukturertAdresse, periode);
                builder.leggTil(adressePeriode);
            });
        }
    }

    public void konverterMidlertidigAdressePerioder(HentPersonhistorikkResponse response, PersonhistorikkInfo.Builder builder) {
        if (Optional.ofNullable(response.getMidlertidigAdressePeriodeListe()).isPresent()) {
            response.getMidlertidigAdressePeriodeListe().forEach(e -> {
                Periode periode =
                        Periode
                                .innenfor(DateUtil.convertToLocalDate(e.getPostleveringsPeriode().getFom()), DateUtil.convertToLocalDate(e.getPostleveringsPeriode().getTom()));
                if (e instanceof MidlertidigPostadresseNorge) {
                    StrukturertAdresse strukturertAdresse = ((MidlertidigPostadresseNorge) e).getStrukturertAdresse();
                    AdressePeriode adressePeriode = konverterStrukturertAdresse(strukturertAdresse, periode);
                    builder.leggTil(adressePeriode);
                } else if (e instanceof MidlertidigPostadresseUtland) {
                    UstrukturertAdresse ustrukturertAdresse = ((MidlertidigPostadresseUtland) e).getUstrukturertAdresse();
                    AdressePeriode adressePeriode = konverterUstrukturertAdresse(ustrukturertAdresse, periode);
                    builder.leggTil(adressePeriode);
                }
            });
        }
    }

    Adresseinfo konverterStrukturertAdresse(Person person,
                                            StrukturertAdresse adresse) {
        requireNonNull(adresse);
        if (adresse instanceof Gateadresse) {
            return konverterStrukturertAdresse(person, (Gateadresse) adresse);
        } else if (adresse instanceof Matrikkeladresse) {
            return konverterStrukturertAdresse(person, (Matrikkeladresse) adresse);
        } else if (adresse instanceof PostboksadresseNorsk) {
            return konverterStrukturertAdresse(person, (PostboksadresseNorsk) adresse);
        } else if (adresse instanceof StedsadresseNorge) {
            return konverterStrukturertAdresse(person, (StedsadresseNorge) adresse);
        } else {
            throw new IllegalArgumentException("Ikke-støttet klasse for strukturert adresse: " + adresse.getClass());
        }
    }

    private AdressePeriode konverterStrukturertAdresse(StrukturertAdresse adresse, Periode periode) {
        requireNonNull(adresse);
        requireNonNull(periode);

        Adresse strukturertAdresse;
        if (adresse instanceof Gateadresse) {
            strukturertAdresse = konverterStrukturertAdresse((Gateadresse) adresse);
        } else if (adresse instanceof Matrikkeladresse) {
            strukturertAdresse = konverterStrukturertAdresse((Matrikkeladresse) adresse);
        } else if (adresse instanceof PostboksadresseNorsk) {
            strukturertAdresse = konverterStrukturertAdresse((PostboksadresseNorsk) adresse);
        } else if (adresse instanceof StedsadresseNorge) {
            strukturertAdresse = konverterStrukturertAdresse((StedsadresseNorge) adresse);
        } else {
            throw new IllegalArgumentException("Ikke-støttet klasse for strukturert adresse: " + adresse.getClass());
        }

        return byggAdressePeriode(strukturertAdresse, periode);
    }

    private AdressePeriode konverterUstrukturertAdresse(UstrukturertAdresse ustrukturertAdresse, Periode periode) {

        Adresse adresse = konverterUstrukturertAdresse(ustrukturertAdresse);

        return byggAdressePeriode(adresse, periode);
    }

    private Adresseinfo konverterStrukturertAdresse(Person person,
                                                    Matrikkeladresse matrikkeladresse) {

        Adresse adresse = konverterStrukturertAdresse(matrikkeladresse);

        return byggAddresseinfo(person, adresse);
    }

    private String adresseFraBolignummerOgEiendomsnavn(Matrikkeladresse matrikkeladresse) {
        return matrikkeladresse.getBolignummer() == null ? matrikkeladresse.getEiendomsnavn() : matrikkeladresseMedBolignummer(matrikkeladresse);
    }

    private String matrikkeladresseMedBolignummer(Matrikkeladresse matrikkeladresse) {
        return "Bolignummer " + matrikkeladresse.getBolignummer() + " " + matrikkeladresse.getEiendomsnavn();
    }

    private Adresseinfo konverterStrukturertAdresse(Person person,
                                                    Gateadresse gateadresse) {

        Adresse adresse = konverterStrukturertAdresse(gateadresse);
        return byggAddresseinfo(person, adresse);
    }

    private String adresseFraGateadresse(Gateadresse gateadresse) {
        return gateadresse.getGatenavn() +
                hvisfinnes(gateadresse.getHusnummer()) +
                hvisfinnes(gateadresse.getHusbokstav());
    }

    private Adresseinfo konverterStrukturertAdresse(Person person,
                                                    StedsadresseNorge stedsadresseNorge) {

        Adresse adresse = konverterStrukturertAdresse(stedsadresseNorge);
        return byggAddresseinfo(person, adresse);
    }

    private Adresseinfo konverterStrukturertAdresse(Person person,
                                                    PostboksadresseNorsk postboksadresseNorsk) {
        Adresse adresse = konverterStrukturertAdresse(postboksadresseNorsk);
        return byggAddresseinfo(person, adresse);
    }

    private Adresseinfo.Builder adresseBuilderForPerson(Person person) {
        Personstatus personstatus = person.getPersonstatus();
        return new Adresseinfo.Builder(
                TpsUtil.getPersonIdent(person),
                TpsUtil.getPersonnavn(person),
                personstatus == null ? null : tilPersonstatusType(personstatus));
    }

    private String postboksadresselinje(PostboksadresseNorsk postboksadresseNorsk) {
        return "Postboks" + hvisfinnes(postboksadresseNorsk.getPostboksnummer()) +
                hvisfinnes(postboksadresseNorsk.getPostboksanlegg());
    }

    private Adresseinfo byggAddresseinfo(Person person, Adresse adresse) {
        return adresseBuilderForPerson(person)
                .medPostNr(adresse.postnummer)
                .medPoststed(adresse.poststed)
                .medLand(adresse.land)
                .medAdresselinje1(adresse.adresselinje1)
                .medAdresselinje2(adresse.adresselinje2)
                .medAdresselinje3(adresse.adresselinje3)
                .medAdresselinje4(adresse.adresselinje4)
                .build();
    }

    private AdressePeriode byggAdressePeriode(Adresse adresse, Periode periode) {
        return AdressePeriode.builder()
                .medGyldighetsperiode(periode)
                .medAdresselinje1(adresse.adresselinje1)
                .medAdresselinje2(adresse.adresselinje2)
                .medAdresselinje3(adresse.adresselinje3)
                .medAdresselinje4(adresse.adresselinje4)
                .medLand(adresse.land)
                .medPostnummer(adresse.postnummer)
                .medPoststed(adresse.poststed)
                .build();
    }

    private Adresse konverterStrukturertAdresse(Gateadresse gateadresse) {

        String postnummer = Optional.ofNullable(gateadresse.getPoststed()).map(Kodeverdi::getValue).orElse(HARDKODET_POSTNR);

        Adresse adresse = new Adresse();
        adresse.postnummer = postnummer;
        adresse.poststed = tilPoststed(postnummer);
        adresse.land = tilLand(gateadresse.getLandkode());

        if (gateadresse.getTilleggsadresse() == null) {
            adresse.adresselinje1 = adresseFraGateadresse(gateadresse);
        } else {
            adresse.adresselinje1 = gateadresse.getTilleggsadresse();
            adresse.adresselinje2 = adresseFraGateadresse(gateadresse);
        }
        return adresse;
    }

    private Adresse konverterStrukturertAdresse(Matrikkeladresse matrikkeladresse) {
        Adresse adresse = new Adresse();
        adresse.postnummer = matrikkeladresse.getPoststed().getValue();
        adresse.poststed = tilPoststed(adresse.postnummer);

        if (matrikkeladresse.getLandkode() != null) {
            adresse.land = matrikkeladresse.getLandkode().getValue();
        }

        if (matrikkeladresse.getTilleggsadresse() == null) {
            adresse.adresselinje1 = adresseFraBolignummerOgEiendomsnavn(matrikkeladresse);
        } else {
            adresse.adresselinje1 = matrikkeladresse.getTilleggsadresse();
            adresse.adresselinje2 = adresseFraBolignummerOgEiendomsnavn(matrikkeladresse);
        }
        return adresse;
    }

    private Adresse konverterStrukturertAdresse(PostboksadresseNorsk postboksadresseNorsk) {
        Adresse adresse = new Adresse();
        adresse.postnummer = postboksadresseNorsk.getPoststed().getValue();
        adresse.poststed = tilPoststed(adresse.postnummer);
        adresse.land = tilLand(postboksadresseNorsk.getLandkode());

        if (postboksadresseNorsk.getTilleggsadresse() == null) {
            adresse.adresselinje1 = postboksadresselinje(postboksadresseNorsk);
        } else {
            adresse.adresselinje1 = postboksadresseNorsk.getTilleggsadresse();
            adresse.adresselinje2 = postboksadresselinje(postboksadresseNorsk);
        }

        return adresse;
    }

    private Adresse konverterStrukturertAdresse(StedsadresseNorge stedsadresseNorge) {

        Adresse adresse = new Adresse();
        adresse.postnummer = stedsadresseNorge.getPoststed().getValue();
        adresse.poststed = tilPoststed(adresse.postnummer);
        adresse.land = tilLand(stedsadresseNorge.getLandkode());
        adresse.adresselinje1 = stedsadresseNorge.getBolignummer();
        adresse.adresselinje2 = stedsadresseNorge.getTilleggsadresse();

        return adresse;
    }

    private Adresse konverterUstrukturertAdresse(UstrukturertAdresse ustrukturertAdresse) {
        Adresse adresse = new Adresse();
        adresse.adresselinje1 = ustrukturertAdresse.getAdresselinje1();
        adresse.adresselinje2 = ustrukturertAdresse.getAdresselinje2();
        adresse.adresselinje3 = ustrukturertAdresse.getAdresselinje3();
        adresse.land = tilLand(ustrukturertAdresse.getLandkode());


        String linje4 = ustrukturertAdresse.getAdresselinje4();
        // Ustrukturert adresse kan ha postnr + poststed i adresselinje4
        if (linje4 != null && linje4.matches(POSTNUMMER_POSTSTED)) {
            adresse.postnummer = linje4.substring(0, 4);
            adresse.poststed = linje4.substring(5);
        } else {
            adresse.adresselinje4 = linje4;
        }
        return adresse;
    }

    public String finnAdresseLandkodeFor(Person person) {
        Adresseinfo adresseinfo = tilAdresseInfo(person);
        return adresseinfo.getLand();
    }

    public String finnAdresseFor(Person person) {
        if (person instanceof Person) {
            Adresseinfo adresseinfo = tilAdresseInfo(person);
            return byggOppAdresse(adresseinfo);
        }
        return "UDEFINERT ADRESSE";
    }

    private String byggOppAdresse(Adresseinfo adresseinfo) {
        String linje1 = adresseinfo.getAdresselinje1();
        String linje2 = Optional.ofNullable(adresseinfo.getAdresselinje2()).map(linje -> "\n" + linje).orElse("");
        String linje3 = Optional.ofNullable(adresseinfo.getAdresselinje3()).map(linje -> "\n" + linje).orElse("");
        String linje4 = Optional.ofNullable(adresseinfo.getAdresselinje4()).map(linje -> "\n" + linje).orElse("");
        String postnr = Optional.ofNullable(adresseinfo.getPostNr()).map(nr -> "\n" + nr).orElse("");
        String poststed = Optional.ofNullable(adresseinfo.getPoststed()).map(sted -> " " + sted).orElse("");
        String land = Optional.ofNullable(adresseinfo.getLand()).map(landKode -> "\n" + landKode).orElse("");
        return linje1 + linje2 + linje3 + linje4 + postnr + poststed + land;
    }

    Adresseinfo tilAdresseInfo(Person person) {
        if (person instanceof Person) {
            return konverterStrukturertAdresse(person, person.getBostedsadresse().getStrukturertAdresse());
        }
        throw new IllegalArgumentException("Ukjent persontype " + person);
    }

    private String tilPoststed(String postnummer) {
        if (HARDKODET_POSTNR.equals(postnummer)) {
            return HARDKODET_POSTSTED;
        }
        // FIXME: Slå opp mot poststed-tabell elns
        return HARDKODET_POSTSTED;
    }

    private String tilLand(Landkoder landkoder) {
        return null == landkoder ? null : landkoder.getValue();
    }

    private PersonstatusType tilPersonstatusType(Personstatus personstatus) {
        return PersonstatusType.valueOf(personstatus.getPersonstatus().getValue());
    }

    private String hvisfinnes(Object object) {
        return object == null ? "" : " " + object.toString().trim();
    }

    private class Adresse {

        String adresselinje1;
        String adresselinje2;
        String adresselinje3;
        String adresselinje4;
        String postnummer;
        String poststed;
        String land;
    }
}
