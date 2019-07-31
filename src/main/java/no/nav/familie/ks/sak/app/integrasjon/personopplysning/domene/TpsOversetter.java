package no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene;

import static java.util.stream.Collectors.toSet;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import no.nav.tjeneste.virksomhet.person.v3.informasjon.*;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.PersonIdent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import no.nav.familie.ks.sak.app.integrasjon.felles.ws.DateUtil;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.adresse.TpsAdresseOversetter;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.relasjon.Familierelasjon;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.relasjon.RelasjonsRolleType;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.status.PersonstatusPeriode;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.status.PersonstatusType;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.tilhørighet.Landkode;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.tilhørighet.StatsborgerskapPeriode;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonhistorikkResponse;

@Component
public class TpsOversetter {

    private static final Logger log = LoggerFactory.getLogger(TpsOversetter.class);

    private TpsAdresseOversetter tpsAdresseOversetter;

    public TpsOversetter(TpsAdresseOversetter tpsAdresseOversetter) {
        this.tpsAdresseOversetter = tpsAdresseOversetter;
    }

    private Landkode utledLandkode(Statsborgerskap statsborgerskap) {
        Landkode landkode = Landkode.UDEFINERT;
        if (Optional.ofNullable(statsborgerskap).isPresent()) {
            landkode = new Landkode(statsborgerskap.getLand().getKodeRef());
        }
        return landkode;
    }

    public Personinfo tilPersonInfo(String personident, Person person) { // NOSONAR - ingen forbedring å forkorte metoden her
        String navn = person.getPersonnavn().getSammensattNavn();
        String adresse = tpsAdresseOversetter.finnAdresseFor(person);
        String adresseLandkode = tpsAdresseOversetter.finnAdresseLandkodeFor(person);

        LocalDate fødselsdato = finnFødselsdato(person);
        LocalDate dødsdato = finnDødsdato(person);

        Aktoer aktoer = person.getAktoer();
        PersonIdent pi = (PersonIdent) aktoer;
        String ident = pi.getIdent().getIdent();
        PersonstatusType personstatus = tilPersonstatusType(person.getPersonstatus());
        Set<Familierelasjon> familierelasjoner = person.getHarFraRolleI().stream()
                .map(this::tilRelasjon)
                .collect(toSet());

        Landkode landkode = utledLandkode(person.getStatsborgerskap());

        String diskresjonskode = person.getDiskresjonskode() == null ? null : person.getDiskresjonskode().getValue();

        AktørId id = new AktørId(personident);

        return new Personinfo.Builder()
                .medAktørId(id)
                .medPersonIdent(no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.PersonIdent.fra(ident))
                .medNavn(navn)
                .medAdresse(adresse)
                .medAdresseLandkode(adresseLandkode)
                .medFødselsdato(fødselsdato)
                .medDødsdato(dødsdato)
                .medPersonstatusType(personstatus)
                .medStatsborgerskap(landkode)
                .medFamilierelasjon(familierelasjoner)
                .medDiskresjonsKode(diskresjonskode)
                .medLandkode(landkode)
                .build();
    }

    public PersonhistorikkInfo tilPersonhistorikkInfo(String aktørId, HentPersonhistorikkResponse response) {

        PersonhistorikkInfo.Builder builder = PersonhistorikkInfo
                .builder()
                .medAktørId(aktørId);

        konverterPersonstatusPerioder(response, builder);

        konverterStatsborgerskapPerioder(response, builder);

        tpsAdresseOversetter.konverterBostedadressePerioder(response, builder);
        tpsAdresseOversetter.konverterMidlertidigAdressePerioder(response, builder);

        return builder.build();
    }

    private void konverterPersonstatusPerioder(HentPersonhistorikkResponse response, PersonhistorikkInfo.Builder builder) {
        Optional.ofNullable(response.getPersonstatusListe()).ifPresent(list -> {
            list.forEach(e -> {
                Personstatus personstatus = new Personstatus();
                personstatus.setPersonstatus(e.getPersonstatus());
                PersonstatusType personstatusType = tilPersonstatusType(personstatus);

                Periode gyldighetsperiode = Periode.innenfor(
                        DateUtil.convertToLocalDate(e.getPeriode().getFom()),
                        DateUtil.convertToLocalDate(e.getPeriode().getTom()));

                PersonstatusPeriode periode = new PersonstatusPeriode(gyldighetsperiode, personstatusType);
                builder.leggTil(periode);
            });
        });
    }

    private void konverterStatsborgerskapPerioder(HentPersonhistorikkResponse response, PersonhistorikkInfo.Builder builder) {
        Optional.ofNullable(response.getStatsborgerskapListe()).ifPresent(list -> {
            list.forEach(e -> {
                Periode periode = Periode.innenfor(
                        DateUtil.convertToLocalDate(e.getPeriode().getFom()),
                        DateUtil.convertToLocalDate(e.getPeriode().getTom()));

                Landkode landkoder = new Landkode(e.getStatsborgerskap().getLand().getValue());
                StatsborgerskapPeriode element = new StatsborgerskapPeriode(periode, landkoder);
                builder.leggTil(element);
            });
        });
    }

    private PersonstatusType tilPersonstatusType(Personstatus personstatus) {
        return PersonstatusType.valueOf(personstatus.getPersonstatus().getValue());
    }

    private LocalDate finnDødsdato(Person person) {
        LocalDate dødsdato = null;
        Doedsdato dødsdatoJaxb = person.getDoedsdato();
        if (dødsdatoJaxb != null) {
            dødsdato = DateUtil.convertToLocalDate(dødsdatoJaxb.getDoedsdato());
        }
        return dødsdato;
    }

    private LocalDate finnFødselsdato(Person person) {
        LocalDate fødselsdato = null;
        Foedselsdato fødselsdatoJaxb = person.getFoedselsdato();
        if (fødselsdatoJaxb != null) {
            fødselsdato = DateUtil.convertToLocalDate(fødselsdatoJaxb.getFoedselsdato());
        }
        return fødselsdato;
    }

    private Familierelasjon tilRelasjon(no.nav.tjeneste.virksomhet.person.v3.informasjon.Familierelasjon familierelasjon) {
        String rollekode = familierelasjon.getTilRolle().getValue();
        RelasjonsRolleType relasjonsrolle = RelasjonsRolleType.valueOf(rollekode);
        String adresse = tpsAdresseOversetter.finnAdresseFor(familierelasjon.getTilPerson());
        PersonIdent personIdent = (PersonIdent) familierelasjon.getTilPerson().getAktoer();
        no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.PersonIdent ident = no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.PersonIdent.fra(personIdent.getIdent().getIdent());
        Boolean harSammeBosted = familierelasjon.isHarSammeBosted();

        return new Familierelasjon(ident, relasjonsrolle,
                tilLocalDate(familierelasjon.getTilPerson().getFoedselsdato()), adresse, harSammeBosted);
    }

    private LocalDate tilLocalDate(Foedselsdato fødselsdatoJaxb) {
        if (fødselsdatoJaxb != null) {
            return DateUtil.convertToLocalDate(fødselsdatoJaxb.getFoedselsdato());
        }
        return null;
    }
}
