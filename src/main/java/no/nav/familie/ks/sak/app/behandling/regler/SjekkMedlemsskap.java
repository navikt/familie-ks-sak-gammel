package no.nav.familie.ks.sak.app.behandling.regler;

import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.fpsak.nare.doc.RuleDocumentation;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.specification.LeafSpecification;

@RuleDocumentation(SjekkMedlemsskap.ID)
public class SjekkMedlemsskap extends LeafSpecification<Faktagrunnlag> {

    public static final String ID = "SVP_VK 14.4.6";
    private static final String GYLDIG_STATSBORGERSKAPKODE = "NOR";

    public SjekkMedlemsskap() {
        super(ID);
    }

    @Override
    public Evaluation evaluate(Faktagrunnlag grunnlag) {
        var statsborgerskap = grunnlag.getTpsFakta().getStatsborgerskap();

        // TODO: Skal også sjekke om foreldre har bodd i Norge fem år. Sjekk for begge personer.
        if (! statsborgerskap.equals(GYLDIG_STATSBORGERSKAPKODE)) {
            return ja();
        }
        return nei();
    }
}
