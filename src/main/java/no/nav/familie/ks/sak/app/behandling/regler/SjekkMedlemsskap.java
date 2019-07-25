package no.nav.familie.ks.sak.app.behandling.regler;

import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.PersonhistorikkInfo;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.Personinfo;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.adresse.AdressePeriode;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.adresse.AdresseType;
import no.nav.fpsak.nare.doc.RuleDocumentation;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.specification.LeafSpecification;
import java.time.Period;

@RuleDocumentation(SjekkMedlemsskap.ID)
public class SjekkMedlemsskap extends LeafSpecification<Faktagrunnlag> {

    public static final String ID = "PARAGRAF 123";
    private static final String GYLDIG_STATSBORGERSKAP = "NOR";
    private static int MIN_ANTALL_ÅR = 5;
    private static int ANTALL_MÅNEDER_I_ÅRET = 12;

    public SjekkMedlemsskap() {
        super(ID);
    }

    @Override
    public Evaluation evaluate(Faktagrunnlag grunnlag) {
        var forelder = grunnlag.getTpsFakta().getForelder();
        var annenForelder = grunnlag.getTpsFakta().getAnnenForelder();
        if (! norskMedlemsskap(forelder.getPersoninfo(), forelder.getPersonhistorikkInfo())
                || (annenForelder != null && ! norskMedlemsskap(annenForelder.getPersoninfo(), annenForelder.getPersonhistorikkInfo()))) {
            return ja();
        }
        return nei();
    }

    private boolean norskMedlemsskap(Personinfo personinfo, PersonhistorikkInfo personhistorikkInfo) {
        var statsborgerskap = personinfo.getStatsborgerskap();
        var antallMånederINorge = personhistorikkInfo.getAdressehistorikk().stream()
                .filter( adressePeriode ->  erNorskBostedsadresse(adressePeriode))
                .map( adressePeriode -> adressePeriode.getPeriode() )
                .map ( periode -> Period.between(periode.getFom(), periode.getTom()))
                .map ( periode -> periode.getYears() * ANTALL_MÅNEDER_I_ÅRET + periode.getMonths())
                .mapToInt(Integer::intValue)
                .sum();

        var boddINorgeFemÅr = antallMånederINorge >= MIN_ANTALL_ÅR * ANTALL_MÅNEDER_I_ÅRET + 2;
        return statsborgerskap.erNorge() && boddINorgeFemÅr;
    }

    private boolean erNorskBostedsadresse(AdressePeriode adressePeriode) {
        return adressePeriode.getAdresse().getLand().equals(GYLDIG_STATSBORGERSKAP) && adressePeriode.getAdresse().getAdresseType().equals(AdresseType.BOSTEDSADRESSE);
    }
}
