package no.nav.familie.ks.sak.app.behandling.regel.mvp.barn.regel;

import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.fpsak.nare.doc.RuleDocumentation;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.specification.LeafSpecification;

@RuleDocumentation(KunEtBarn.ID)
public class KunEtBarn extends LeafSpecification<Faktagrunnlag> {

    public static final String ID = "KS-BARN-1";

    public KunEtBarn() {
        super(ID);
    }

    @Override
    public Evaluation evaluate(Faktagrunnlag grunnlag) {
        return grunnlag.getBarnehageBarnGrunnlag().getFamilieforhold().getBarna().size() == 1 ? ja() : nei();
    }
}

