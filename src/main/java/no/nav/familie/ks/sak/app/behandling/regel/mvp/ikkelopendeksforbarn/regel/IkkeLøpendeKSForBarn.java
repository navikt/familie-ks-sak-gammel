package no.nav.familie.ks.sak.app.behandling.regel.mvp.ikkelopendeksforbarn.regel;

import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.fpsak.nare.doc.RuleDocumentation;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.specification.LeafSpecification;

@RuleDocumentation(IkkeLøpendeKSForBarn.ID)
public class IkkeLøpendeKSForBarn extends LeafSpecification<Faktagrunnlag> {
    
    public static final String ID = "IKKE_LØPENDE_KS_FOR_BARN";

    public IkkeLøpendeKSForBarn() {
        super(ID);
    }

    @Override
    public Evaluation evaluate(Faktagrunnlag faktagrunnlag) {
        // TODO: Støtte for flerlinger.
        // TODO: Dette er en MVP-regel, som skal erstattes når vi har på plass et endepunkt mot infotrygd som gir mer data.
        // Dette faktapunktet gir true for barn som har løpende kontantstøtte ELLER der saken er under behandling.
        var barnHarLøpendeKontantstøtte = faktagrunnlag.getInfotrygdFakta().getAktivKontantstøtteInfo().getHarAktivKontantstotte();
        
        if (!barnHarLøpendeKontantstøtte) {
            return ja();
        } else {
            return nei();
        }
    }
}