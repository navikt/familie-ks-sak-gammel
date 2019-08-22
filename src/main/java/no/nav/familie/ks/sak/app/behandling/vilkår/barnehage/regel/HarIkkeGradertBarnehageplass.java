package no.nav.familie.ks.sak.app.behandling.vilkår.barnehage.regel;

import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.grunnlag.søknad.Barnehageplass;
import no.nav.fpsak.nare.doc.RuleDocumentation;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.specification.LeafSpecification;

@RuleDocumentation(HarIkkeGradertBarnehageplass.ID)
public class HarIkkeGradertBarnehageplass extends LeafSpecification<Faktagrunnlag> {

    public static final String ID = "KS-BHAG-1";

    public HarIkkeGradertBarnehageplass() {
        super(ID);
    }

    @Override
    public Evaluation evaluate(Faktagrunnlag grunnlag) {
        Barnehageplass barnehageplass = grunnlag.getSøknad().barnehageplass;

        switch (barnehageplass.barnBarnehageplassStatus) {
            case garIkkeIBarnehage:
                return ja();
            case harBarnehageplass:
                return fullBarnehageplass(barnehageplass.harBarnehageplassAntallTimer);
            case harSluttetIBarnehage:
                return fullBarnehageplass(barnehageplass.harSluttetIBarnehageAntallTimer);
            case skalBegynneIBarnehage:
                return fullBarnehageplass(barnehageplass.skalBegynneIBarnehageAntallTimer);
            case skalSlutteIBarnehage:
                return fullBarnehageplass(barnehageplass.skalSlutteIBarnehageAntallTimer);
            default:
                return nei();
        }
    }

    private Evaluation fullBarnehageplass(String antallTimer) {
        return Float.parseFloat(antallTimer) >= 33 ? ja() : nei();
    }
}
