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
        Barnehageplass barnehageplass = grunnlag.getSøknad().barnehageplass;

        switch (barnehageplass.barnBarnehageplassStatus) {
            case garIkkeIBarnehage:
                return ja();
            case harBarnehageplass:
                return gradertBarnehageplass(barnehageplass.harBarnehageplassAntallTimer);
            case harSluttetIBarnehage:
                return gradertBarnehageplass(barnehageplass.harSluttetIBarnehageAntallTimer);
            case skalBegynneIBarnehage:
                return gradertBarnehageplass(barnehageplass.skalBegynneIBarnehageAntallTimer);
            case skalSlutteIBarnehage:
                return gradertBarnehageplass(barnehageplass.skalSlutteIBarnehageAntallTimer);
            default:
                return nei();
        }
    }

    private Evaluation gradertBarnehageplass(String antallTimer) {
        return Float.parseFloat(antallTimer) >= 33 ? ja() : nei();
    }
}
