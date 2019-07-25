package no.nav.familie.ks.sak.app.grunnlag;

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

        var personInfoSøker = personopplysningerTjeneste.hentPersoninfoFor(søknad.person.fnr);
        Forelder forelder = genererForelder(personInfoSøker);

        var personidentForAnnenForelder = søknad.familieforhold.annenForelderFodselsnummer;
        Forelder annenForelder = null;
        if (! personidentForAnnenForelder.isEmpty()) {
            var personinfo = personopplysningerTjeneste.hentPersoninfoFor(personidentForAnnenForelder);
            annenForelder = genererForelder(personinfo);
        }

        Personinfo barn = finnBarnSøktFor(søknad, personInfoSøker);

        return new TpsFakta.Builder()
                .medForelder(forelder)
                .medBarn(barn)
                .medAnnenForelder(annenForelder)
                .build();
    }

    private Forelder genererForelder(Personinfo personinfo) {
        var fødselsdato = personinfo.getFødselsdato();
        PersonhistorikkInfo personhistorikkInfo = personopplysningerTjeneste.hentHistorikkFor(
                personinfo.getPersonIdent().getIdent(),
                fødselsdato.minusYears(5).minusMonths(2),
                LocalDate.now()
        );
        return new Forelder.Builder()
                .medPersonhistorikkInfo(personhistorikkInfo)
                .medPersoninfo(personinfo)
                .build();
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

        return personopplysningerTjeneste.hentPersoninfoFor(personIdentBarn);
    }
}
