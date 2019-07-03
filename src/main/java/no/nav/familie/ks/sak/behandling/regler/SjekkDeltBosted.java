package no.nav.familie.ks.sak.behandling.regler;

import no.nav.familie.ks.sak.behandling.grunnlag.Faktagrunnlag;
import no.nav.fpsak.nare.doc.RuleDocumentation;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.specification.LeafSpecification;

@RuleDocumentation(SjekkDeltBosted.ID)
public class SjekkDeltBosted extends LeafSpecification<Faktagrunnlag> {

    public static final String ID = "SVP_VK 14.4.7";

    public SjekkDeltBosted() {
        super(ID);
    }

    @Override
    public Evaluation evaluate(Faktagrunnlag grunnlag) {
        //TODO: Flagg hentes fra TPS?
        if (! grunnlag.getTpsFakta().getStatsborgerskap().equals("NOR")) {
            return ja();
        }
        return nei();
    }
}
