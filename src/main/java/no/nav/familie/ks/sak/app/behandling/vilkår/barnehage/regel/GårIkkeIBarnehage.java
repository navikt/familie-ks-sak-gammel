package no.nav.familie.ks.sak.app.behandling.vilkår.barnehage.regel;

import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.grunnlag.søknad.Barnehageplass.BarnehageplassVerdier;
import no.nav.fpsak.nare.doc.RuleDocumentation;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.specification.LeafSpecification;

@RuleDocumentation(GårIkkeIBarnehage.ID)
public class GårIkkeIBarnehage extends LeafSpecification<Faktagrunnlag> {

    public static final String ID = "KS-BHAG-1";

    public GårIkkeIBarnehage() {
        super(ID);
    }

    @Override
    public Evaluation evaluate(Faktagrunnlag grunnlag) {
        BarnehageplassVerdier svar = grunnlag.getSøknad().barnehageplass.barnBarnehageplassStatus;
        return svar.equals(BarnehageplassVerdier.garIkkeIBarnehage) ? ja() : nei();
    }
}
