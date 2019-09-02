package no.nav.familie.ks.sak.app.integrasjon;

import no.nav.familie.ks.sak.app.behandling.domene.Behandling;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.personopplysning.*;
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.Landkode;
import no.nav.familie.ks.sak.app.behandling.domene.typer.AktørId;
import no.nav.familie.ks.sak.app.behandling.domene.typer.DatoIntervallEntitet;
import no.nav.familie.ks.sak.app.grunnlag.Søknad;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.Personinfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegisterInnhentingService {

    private PersonopplysningService personopplysningService;
    private OppslagTjeneste oppslagTjeneste;

    @Autowired
    public RegisterInnhentingService(PersonopplysningService personopplysningService, OppslagTjeneste oppslagTjeneste) {
        this.personopplysningService = personopplysningService;
        this.oppslagTjeneste = oppslagTjeneste;
    }

    public void innhentPersonopplysninger(Behandling behandling, Søknad søknad) {
        final var søkerAktørId = behandling.getFagsak().getAktørId();
        final var annenPartAktørId = oppslagTjeneste.hentAktørId(søknad.getFamilieforhold().getAnnenForelderFødselsnummer());
        final var barnAktørId = oppslagTjeneste.hentAktørId(søknad.getMineBarn().getFødselsnummer());
        final var informasjon = new PersonopplysningerInformasjon();

        final var søkerPersoninfo = oppslagTjeneste.hentPersoninfoFor(søkerAktørId);
        final var barnPersoninfo = oppslagTjeneste.hentPersoninfoFor(barnAktørId);
        mapPersonopplysninger(søkerAktørId, søkerPersoninfo, informasjon);
        mapPersonopplysninger(barnAktørId, barnPersoninfo, informasjon);
        Personinfo annenPartPersoninfo = null;

        if (annenPartAktørId != null) {
            annenPartPersoninfo = oppslagTjeneste.hentPersoninfoFor(annenPartAktørId);
            mapPersonopplysninger(annenPartAktørId, annenPartPersoninfo, informasjon);
        }

        mapRelasjoner(søkerPersoninfo, annenPartPersoninfo, barnPersoninfo, informasjon);

        personopplysningService.lagre(behandling, informasjon);
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
