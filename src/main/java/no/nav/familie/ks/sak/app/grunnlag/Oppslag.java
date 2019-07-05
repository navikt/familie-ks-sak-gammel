package no.nav.familie.ks.sak.app.grunnlag;

import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.AktørId;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.PersonopplysningerTjeneste;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.PersonhistorikkInfo;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.Personinfo;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.relasjon.RelasjonsRolleType;

import javax.inject.Inject;
import java.time.LocalDate;

public class Oppslag {

    @Inject
    private PersonopplysningerTjeneste personopplysningerTjeneste;

    public TpsFakta hentTpsFakta(Søknad søknad) {
        AktørId aktørIdSøker = hentAktørId(søknad.person.fnr);
        Personinfo personInfoSøker = personopplysningerTjeneste.hentPersoninfoFor(aktørIdSøker);
        LocalDate fødselsdatoSøker = personInfoSøker.getFødselsdato();
        PersonhistorikkInfo personhistorikkInfoSøker = personopplysningerTjeneste.hentHistorikkFor(aktørIdSøker, fødselsdatoSøker, LocalDate.now());

        Forelder forelder = new Forelder.Builder()
                .medPersonhistorikkInfo(personhistorikkInfoSøker)
                .medPersoninfo(personInfoSøker)
                .build();
        Personinfo barn = finnBarnSøktFor(søknad, personInfoSøker);
        Forelder annenForelder = finnAnnenForelderFraSøknad(søknad);

        return new TpsFakta.Builder()
                .medForelder(forelder)
                .medBarn(barn)
                .medAnnenForelder(annenForelder)
                .build();
    }

    private Forelder finnAnnenForelderFraSøknad(Søknad søknad) {
        String annenForelderFnr = søknad.familieforhold.annenForelderFodselsnummer;
        if (! annenForelderFnr.isEmpty()) {
            var aktørId = hentAktørId(annenForelderFnr);
            Personinfo personinfo = personopplysningerTjeneste.hentPersoninfoFor(aktørId);
            var fødselsdato = personinfo.getFødselsdato();
            PersonhistorikkInfo personhistorikkInfo = personopplysningerTjeneste.hentHistorikkFor(aktørId, fødselsdato, LocalDate.now());
            return new Forelder.Builder()
                    .medPersonhistorikkInfo(personhistorikkInfo)
                    .medPersoninfo(personinfo)
                    .build();
        }
        return null;
    }

    private AktørId hentAktørId(String personIdent) {
        // TODO: Hent aktørid
        String aktørId = personIdent;
        return new AktørId(aktørId);
    }

    private Personinfo finnBarnSøktFor(Søknad søknad, Personinfo personinfo) {
        // TODO: Returner fnr for valgt barn i tillegg til fødselsdato
        String personIdentBarn = søknad.mineBarn.fodselsdato;

        personinfo
                .getFamilierelasjoner()
                .stream()
                .filter( relasjon -> relasjon.getRelasjonsrolle().equals(RelasjonsRolleType.BARN))
                .filter( barn -> barn.getPersonIdent().equals(personIdentBarn))
                .findFirst()
                .orElseThrow(
                () -> new IllegalArgumentException("Finner ikke relasjon til barn søkt for: " + personIdentBarn));

        return personopplysningerTjeneste.hentPersoninfoFor(hentAktørId(personIdentBarn));
    }
}
