package no.nav.familie.ks.sak.app.behandling.regel.mvp.barnmellom10og14mnd.regel;

import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.fpsak.nare.doc.RuleDocumentation;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.specification.LeafSpecification;

@RuleDocumentation(BarnMellom10Og14Måneder.ID)
public class BarnMellom10Og14Måneder extends LeafSpecification<Faktagrunnlag> {
    
    public static final String ID = "BARN_MELLOM_10_OG_14_MÅNEDER";

    public BarnMellom10Og14Måneder() {
        super(ID);
    }

    @Override
    public Evaluation evaluate(Faktagrunnlag faktagrunnlag) {
        var behandlingsdato = faktagrunnlag.getBehandlingstidspunkt();
        var førsteDagIFødselsmåneden = faktagrunnlag.getTpsFakta().getBarn().getPersoninfo().getFødselsdato().withDayOfMonth(1);
        var tiMånedersDato = førsteDagIFødselsmåneden.plusMonths(10);
        var fjortenMånedersDato = førsteDagIFødselsmåneden.plusMonths(14);
        
        if ((behandlingsdato.isEqual(tiMånedersDato) || behandlingsdato.isAfter(tiMånedersDato)) && 
            behandlingsdato.isBefore(fjortenMånedersDato)) {
            return ja();
        } else {
            return nei();
        }
    }
}
