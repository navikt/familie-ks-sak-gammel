package no.nav.familie.ks.sak.app.behandling.regel.mvp.annenPart.regel;

import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.fpsak.nare.doc.RuleDocumentation;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.specification.LeafSpecification;

@RuleDocumentation(AnnenPartErOppgitt.ID)
public class AnnenPartErOppgitt extends LeafSpecification<Faktagrunnlag> {

    public static final String ID = "KS-ANNENPART-1";

    public AnnenPartErOppgitt() {
        super(ID);
    }

    @Override
    public Evaluation evaluate(Faktagrunnlag grunnlag) {
        return grunnlag.getTpsFakta().getAnnenForelder() != null ? ja() : nei();
    }
}
