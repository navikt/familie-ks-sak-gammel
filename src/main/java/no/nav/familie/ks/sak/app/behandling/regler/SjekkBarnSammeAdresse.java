package no.nav.familie.ks.sak.app.behandling.regler;

import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.fpsak.nare.doc.RuleDocumentation;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.specification.LeafSpecification;

@RuleDocumentation(SjekkBarnSammeAdresse.ID)
public class SjekkBarnSammeAdresse extends LeafSpecification<Faktagrunnlag> {

    public static final String ID = "PARAGRAF 123";

    public SjekkBarnSammeAdresse() {
        super(ID);
    }

    @Override
    public Evaluation evaluate(Faktagrunnlag grunnlag) {
        var adresseBarn = grunnlag.getTpsFakta().getBarn().getAdresse();
        var adresseSøker = grunnlag.getTpsFakta().getForelder().getPersoninfo().getAdresse();
        var adresseAnnenForelder = adresseSøker;
        if (grunnlag.getTpsFakta().getAnnenForelder() != null) {
            adresseAnnenForelder = grunnlag.getTpsFakta().getForelder().getPersoninfo().getAdresse();
        }
        if (adresseBarn.equals(adresseSøker) && adresseBarn.equals(adresseAnnenForelder)) {
            return nei();
        }
        return ja();
    }
}
