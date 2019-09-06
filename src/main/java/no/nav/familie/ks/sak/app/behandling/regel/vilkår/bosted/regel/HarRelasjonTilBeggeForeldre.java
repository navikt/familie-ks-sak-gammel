package no.nav.familie.ks.sak.app.behandling.regel.vilkår.bosted.regel;

import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.grunnlag.TpsFakta;
import no.nav.fpsak.nare.doc.RuleDocumentation;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.specification.LeafSpecification;

@RuleDocumentation(HarRelasjonTilBeggeForeldre.ID)
public class HarRelasjonTilBeggeForeldre extends LeafSpecification<Faktagrunnlag> {

    public static final String ID = "KS-BOSTED-2";

    public HarRelasjonTilBeggeForeldre() {
        super(ID);
    }

    @Override
    public Evaluation evaluate(Faktagrunnlag grunnlag) {
        TpsFakta tpsFakta = grunnlag.getTpsFakta();
        return tpsFakta.getAnnenForelder() != null ? ja() : nei();
    }
}
