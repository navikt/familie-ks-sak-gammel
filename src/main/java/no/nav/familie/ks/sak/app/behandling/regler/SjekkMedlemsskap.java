package no.nav.familie.ks.sak.app.behandling.regler;

import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.PersonhistorikkInfo;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.Personinfo;
import no.nav.fpsak.nare.doc.RuleDocumentation;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.specification.LeafSpecification;
import java.time.Period;

@RuleDocumentation(SjekkMedlemsskap.ID)
public class SjekkMedlemsskap extends LeafSpecification<Faktagrunnlag> {

    public static final String ID = "SVP_VK 14.4.6";
    private static final String GYLDIG_STATSBORGERSKAPKODE = "NOR";
    private static int ANTALL_DAGER_I_ÅRET = 365;
    private static int ANTALL_MÅNEDER_I_ÅRET = 12;

    public SjekkMedlemsskap() {
        super(ID);
    }

    @Override
    public Evaluation evaluate(Faktagrunnlag grunnlag) {
        var statsborgerskap = grunnlag.getTpsFakta().getForelder().getPersoninfo().getStatsborgerskap();
        //TODO: Sjekk medlemsskap for begge foreldre
        if (! statsborgerskap.erNorge()) {
            return ja();
        }
        return nei();
    }

    private boolean norskMedlemsskap(Personinfo personinfo, PersonhistorikkInfo personhistorikkInfo) {
        var statsborgerskap = personinfo.getStatsborgerskap();
        var antallDagerINorge = personhistorikkInfo.getAdressehistorikk().stream()
                .filter( adressePeriode ->  adressePeriode.getAdresse().getLand().equals("NOR"))
                .map( adressePeriode -> adressePeriode.getPeriode() )
                .map ( periode -> Period.between(periode.getFom(), periode.getTom()))
                .map ( periode -> periode.getDays() + periode.getMonths() * ANTALL_MÅNEDER_I_ÅRET + periode.getYears() * ANTALL_DAGER_I_ÅRET)
                .mapToInt(Integer::intValue)
                .sum();

        var boddINorgeFemÅr = antallDagerINorge / ANTALL_MÅNEDER_I_ÅRET >= 5;
        return statsborgerskap.erNorge() && boddINorgeFemÅr;
    }
}
