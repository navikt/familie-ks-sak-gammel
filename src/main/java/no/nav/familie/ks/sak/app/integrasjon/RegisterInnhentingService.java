package no.nav.familie.ks.sak.app.integrasjon;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import no.nav.familie.ks.sak.app.behandling.domene.Behandling;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.personopplysning.*;
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.Landkode;
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.RelasjonsRolleType;
import no.nav.familie.ks.sak.app.behandling.domene.typer.AktørId;
import no.nav.familie.ks.sak.app.behandling.domene.typer.DatoIntervallEntitet;
import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.grunnlag.PersonMedHistorikk;
import no.nav.familie.ks.sak.app.grunnlag.Søknad;
import no.nav.familie.ks.sak.app.grunnlag.TpsFakta;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.PersonhistorikkInfo;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.Personinfo;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.relasjon.Familierelasjon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RegisterInnhentingService {

    private static final Logger secureLogger = LoggerFactory.getLogger("secureLogger");
    private PersonopplysningService personopplysningService;
    private OppslagTjeneste oppslagTjeneste;
    private Counter oppgittAnnenPartStemmer = Metrics.counter("soknad.kontantstotte.funksjonell.oppgittannenpart", "erliktsomitps", "JA", "beskrivelse", "Ja");
    private Counter oppgittAnnenPartStemmerIkke = Metrics.counter("soknad.kontantstotte.funksjonell.oppgittannenpart", "erliktsomitps", "NEI", "beskrivelse", "Nei");
    private Counter oppgittAnnenPartIkkeOppgitt = Metrics.counter("soknad.kontantstotte.funksjonell.oppgittannenpart", "erliktsomitps", "IKKE_OPPGITT", "beskrivelse", "Ikke oppgitt");

    @Autowired
    public RegisterInnhentingService(PersonopplysningService personopplysningService, OppslagTjeneste oppslagTjeneste) {
        this.personopplysningService = personopplysningService;
        this.oppslagTjeneste = oppslagTjeneste;
    }

    public TpsFakta innhentPersonopplysninger(Behandling behandling, Søknad søknad) throws RegisterInnhentingException {
        final var søkerAktørId = behandling.getFagsak().getAktørId();
        final var annenForelderFødselsnummer = søknad.getFamilieforhold().getAnnenForelderFødselsnummer();
        final var oppgittAnnenPartAktørId = annenForelderFødselsnummer != null && !annenForelderFødselsnummer.isEmpty() ? oppslagTjeneste.hentAktørId(annenForelderFødselsnummer) : null;
        final var barnAktørId = oppslagTjeneste.hentAktørId(søknad.getMineBarn().getFødselsnummer());
        final var personopplysningerInformasjon = new PersonopplysningerInformasjon();

        final PersonMedHistorikk søkerPersonMedHistorikk = hentPersonMedHistorikk(søkerAktørId);
        final PersonMedHistorikk barnPersonMedHistorikk = hentPersonMedHistorikk(barnAktørId);

        mapPersonopplysninger(søkerAktørId, søkerPersonMedHistorikk.getPersoninfo(), personopplysningerInformasjon);
        mapPersonopplysninger(barnAktørId, barnPersonMedHistorikk.getPersoninfo(), personopplysningerInformasjon);

        PersonMedHistorikk annenPartPersonMedHistorikk = null;
        if (søknad.getFamilieforhold().getAnnenForelderFødselsnummer() != null && !søknad.getFamilieforhold().getAnnenForelderFødselsnummer().isEmpty()) {
            final Optional<Familierelasjon> annenPartFamilierelasjon = barnPersonMedHistorikk.getPersoninfo().getFamilierelasjoner().stream().filter(
                familierelasjon ->
                    (familierelasjon.getRelasjonsrolle().equals(RelasjonsRolleType.FARA) || familierelasjon.getRelasjonsrolle().equals(RelasjonsRolleType.MORA))
                        && familierelasjon.getAktørId() != søkerAktørId)
                .findFirst();

            if (annenPartFamilierelasjon.isPresent()) {
                AktørId annenPartAktørId = annenPartFamilierelasjon.get().getAktørId();

                if (annenPartAktørId.equals(oppgittAnnenPartAktørId)) {
                    annenPartPersonMedHistorikk = hentPersonMedHistorikk(annenPartAktørId);

                    personopplysningService.lagre(behandling, oppgittAnnenPartAktørId);
                    mapPersonopplysninger(annenPartAktørId, annenPartPersonMedHistorikk.getPersoninfo(), personopplysningerInformasjon);

                    oppgittAnnenPartStemmer.increment();
                    mapRelasjoner(søkerPersonMedHistorikk.getPersoninfo(), annenPartPersonMedHistorikk.getPersoninfo(), barnPersonMedHistorikk.getPersoninfo(), personopplysningerInformasjon);
                } else {
                    secureLogger.info("Fant annen part: {}. Oppgitt annen part fra søker: {}", annenPartAktørId, oppgittAnnenPartAktørId);
                    oppgittAnnenPartStemmerIkke.increment();
                    throw new RegisterInnhentingException("Oppgitt annen part fra søker stemmer ikke med registerinformasjon");
                }
            } else {
                mapRelasjoner(søkerPersonMedHistorikk.getPersoninfo(), null, barnPersonMedHistorikk.getPersoninfo(), personopplysningerInformasjon);
            }
        } else {
            mapRelasjoner(søkerPersonMedHistorikk.getPersoninfo(), null, barnPersonMedHistorikk.getPersoninfo(), personopplysningerInformasjon);
            oppgittAnnenPartIkkeOppgitt.increment();
        }

        personopplysningService.lagre(behandling, personopplysningerInformasjon);
        return new TpsFakta.Builder()
            .medForelder(søkerPersonMedHistorikk)
            .medBarn(List.of(barnPersonMedHistorikk))
            .medAnnenForelder(annenPartPersonMedHistorikk)
            .build();
    }

    private PersonMedHistorikk hentPersonMedHistorikk(AktørId aktørId) {
        final Personinfo personinfo = oppslagTjeneste.hentPersoninfoFor(aktørId);
        final PersonhistorikkInfo personhistorikkInfo = oppslagTjeneste.hentHistorikkFor(aktørId);
        return new PersonMedHistorikk.Builder()
            .medInfo(personinfo)
            .medPersonhistorikk(personhistorikkInfo)
            .build();
    }

    private void mapRelasjoner(Personinfo søker, Personinfo annenPart, Personinfo barn, PersonopplysningerInformasjon informasjon) {
        barn.getFamilierelasjoner()
            .stream()
            .filter(it -> it.getAktørId().equals(søker.getAktørId()) || (annenPart != null && it.getAktørId().equals(annenPart.getAktørId())))
            .forEach(relasjon -> informasjon.leggTilPersonrelasjon(new PersonRelasjon(barn.getAktørId(), relasjon.getAktørId(), relasjon.getRelasjonsrolle(), relasjon.getHarSammeBosted())));

        søker.getFamilierelasjoner()
            .stream()
            .filter(it -> it.getAktørId().equals(barn.getAktørId()) || (annenPart != null && it.getAktørId().equals(annenPart.getAktørId())))
            .forEach(relasjon -> informasjon.leggTilPersonrelasjon(new PersonRelasjon(søker.getAktørId(), relasjon.getAktørId(), relasjon.getRelasjonsrolle(), relasjon.getHarSammeBosted())));

        if (annenPart != null) {
            annenPart.getFamilierelasjoner()
                .stream()
                .filter(it -> it.getAktørId().equals(barn.getAktørId()) || it.getAktørId().equals(søker.getAktørId()))
                .forEach(relasjon -> informasjon.leggTilPersonrelasjon(new PersonRelasjon(annenPart.getAktørId(), relasjon.getAktørId(), relasjon.getRelasjonsrolle(), relasjon.getHarSammeBosted())));
        }
    }

    private void mapPersonopplysninger(AktørId aktørId, Personinfo personinfo, PersonopplysningerInformasjon informasjon) {
        final var personhistorikk = oppslagTjeneste.hentHistorikkFor(aktørId);

        informasjon.leggTilPersonopplysning(new Personopplysning(aktørId)
            .medFødselsdato(personinfo.getFødselsdato())
            .medDødsdato(personinfo.getDødsdato())
            .medNavn(personinfo.getNavn())
            .medStatsborgerskap(new Landkode(personinfo.getStatsborgerskap().getKode())));

        if (personhistorikk != null) {
            personhistorikk.getStatsborgerskaphistorikk()
                .forEach(statsborgerskap -> informasjon.leggTilStatsborgerskap(new Statsborgerskap(aktørId,
                    DatoIntervallEntitet.fraOgMedTilOgMed(statsborgerskap.getPeriode().getFom(), statsborgerskap.getPeriode().getTom()),
                    new Landkode(statsborgerskap.getTilhørendeLand().getKode()))));

            personhistorikk.getAdressehistorikk()
                .forEach(adresse -> informasjon.leggTilAdresse(new PersonAdresse(aktørId,
                    DatoIntervallEntitet.fraOgMedTilOgMed(adresse.getPeriode().getFom(), adresse.getPeriode().getTom()))
                    .medAdresseType(adresse.getAdresse().getAdresseType())
                    .medAdresselinje1(adresse.getAdresse().getAdresselinje1())
                    .medAdresselinje2(adresse.getAdresse().getAdresselinje2())
                    .medAdresselinje3(adresse.getAdresse().getAdresselinje3())
                    .medAdresselinje4(adresse.getAdresse().getAdresselinje4())
                    .medPostnummer(adresse.getAdresse().getPostnummer())
                    .medPoststed(adresse.getAdresse().getPoststed())
                    .medLand(new Landkode(adresse.getAdresse().getLand()))));
        }
    }
}
