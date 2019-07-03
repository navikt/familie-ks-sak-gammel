package no.nav.familie.ks.sak.behandling.regler;

import no.nav.familie.ks.sak.behandling.grunnlag.Faktagrunnlag;
import no.nav.fpsak.nare.doc.RuleDocumentation;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.specification.LeafSpecification;

@RuleDocumentation(SjekkBarnSammeAdresse.ID)
public class SjekkBarnSammeAdresse extends LeafSpecification<Faktagrunnlag> {

    public static final String ID = "SVP_VK 14.4.7";

    public SjekkBarnSammeAdresse() {
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
