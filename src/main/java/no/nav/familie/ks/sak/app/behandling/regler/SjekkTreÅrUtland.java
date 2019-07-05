package no.nav.familie.ks.sak.app.behandling.regler;

import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.fpsak.nare.doc.RuleDocumentation;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.specification.LeafSpecification;

@RuleDocumentation(SjekkTreÅrUtland.ID)
public class SjekkTreÅrUtland extends LeafSpecification<Faktagrunnlag> {

    public static final String ID = "SVP_VK 14.4.7";

    public SjekkTreÅrUtland() {
        super(ID);
    }

    @Override
    public Evaluation evaluate(Faktagrunnlag grunnlag) {
        if (! grunnlag.getSøknad().kravTilSoker.skalBoMedBarnetINorgeNesteTolvMaaneder.equals("JA")) {
            return ja();
        }
        return nei();
    }
}
