package no.nav.familie.ks.sak.app.behandling.regler;

import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.grunnlag.søknad.Barnehageplass;
import no.nav.fpsak.nare.doc.RuleDocumentation;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.specification.LeafSpecification;

@RuleDocumentation(SjekkBarnehage.ID)
public class SjekkBarnehage extends LeafSpecification<Faktagrunnlag> {

    public static final String ID = "PARAGRAF 123";

    public SjekkBarnehage() {
        super(ID);
    }

    @Override
    public Evaluation evaluate(Faktagrunnlag grunnlag) {
        if (grunnlag.getSøknad().barnehageplass.barnBarnehageplassStatus.equals(Barnehageplass.BarnehageplassVerdier.garIkkeIBarnehage)) {
            return ja();
        }
        return nei();
    }
}
