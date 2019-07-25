package no.nav.familie.ks.sak.app.behandling.regler;

import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.fpsak.nare.doc.RuleDocumentation;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.specification.LeafSpecification;

@RuleDocumentation(SjekkDeltBosted.ID)
public class SjekkDeltBosted extends LeafSpecification<Faktagrunnlag> {

    public static final String ID = "PARAGRAF 123";

    public SjekkDeltBosted() {
        super(ID);
    }

    @Override
    public Evaluation evaluate(Faktagrunnlag grunnlag) {
        if (! grunnlag.getSøknad().kravTilSoker.ikkeAvtaltDeltBosted.equals("JA")) {
            return ja();
        }
        return nei();
    }
}
