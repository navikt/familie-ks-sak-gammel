package no.nav.familie.ks.sak.app.behandling.regel.mvp.barnmellom10og27mnd.regel;

import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.fpsak.nare.doc.RuleDocumentation;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.specification.LeafSpecification;

@RuleDocumentation(BarnMellom10Og27Måneder.ID)
public class BarnMellom10Og27Måneder extends LeafSpecification<Faktagrunnlag> {

    public static final String ID = "BARN_MELLOM_10_OG_26_MÅNEDER";

    public BarnMellom10Og27Måneder() {
        super(ID);
    }

    @Override
    public Evaluation evaluate(Faktagrunnlag faktagrunnlag) {
        var behandlingsdato = faktagrunnlag.getBehandlingstidspunkt();
        var førsteDagIFødselsmåneden = faktagrunnlag.getTpsFakta().getBarn().getPersoninfo().getFødselsdato().withDayOfMonth(1);
        var tiMånedersDato = førsteDagIFødselsmåneden.plusMonths(10);
        var tjuesyvMånedersDato = førsteDagIFødselsmåneden.plusMonths(27);

        if ((behandlingsdato.isEqual(tiMånedersDato) || behandlingsdato.isAfter(tiMånedersDato)) &&
            behandlingsdato.isBefore(tjuesyvMånedersDato)) {
            return ja();
        } else {
            return nei();
        }
    }
}
