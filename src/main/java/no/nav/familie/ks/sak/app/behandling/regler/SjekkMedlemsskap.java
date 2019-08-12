package no.nav.familie.ks.sak.app.behandling.regler;

import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.behandling.regler.medlemskap.HarVærtBosattFemÅrINorge;
import no.nav.familie.ks.sak.app.behandling.regler.medlemskap.MinstEnErNorskStatsborger;
import no.nav.fpsak.nare.doc.RuleDocumentation;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.specification.LeafSpecification;

@RuleDocumentation(SjekkMedlemsskap.ID)
public class SjekkMedlemsskap extends LeafSpecification<Faktagrunnlag> {

    public static final String ID = "MEDL-root";

    public SjekkMedlemsskap() {
        super(ID);
    }

    @Override
    public Evaluation evaluate(Faktagrunnlag grunnlag) {
        return new HarVærtBosattFemÅrINorge().og(new MinstEnErNorskStatsborger()).evaluate(grunnlag);
    }

}
