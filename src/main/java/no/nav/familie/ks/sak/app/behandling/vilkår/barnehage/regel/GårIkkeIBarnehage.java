package no.nav.familie.ks.sak.app.behandling.vilkår.barnehage.regel;

import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.barnehagebarn.Barn;
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.BarnehageplassStatus;
import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.fpsak.nare.doc.RuleDocumentation;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.specification.LeafSpecification;

import java.util.Set;

@RuleDocumentation(GårIkkeIBarnehage.ID)
public class GårIkkeIBarnehage extends LeafSpecification<Faktagrunnlag> {

    public static final String ID = "KS-BHAG-1";

    public GårIkkeIBarnehage() {
        super(ID);
    }

    @Override
    public Evaluation evaluate(Faktagrunnlag grunnlag) {
        Set<Barn> barna = grunnlag.getBarnehageBarnGrunnlag().getFamilieforhold().getBarna();
        long barnUtenBarnehageplass = barna.stream().filter(barn -> barn.getBarnehageStatus().equals(BarnehageplassStatus.HAR_IKKE))
            .count();
        return barnUtenBarnehageplass == barna.size() ? ja() : nei();
    }
}
