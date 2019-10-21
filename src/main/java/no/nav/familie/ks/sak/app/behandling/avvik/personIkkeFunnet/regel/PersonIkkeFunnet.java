package no.nav.familie.ks.sak.app.behandling.avvik.personIkkeFunnet.regel;

import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.specification.LeafSpecification;

public class PersonIkkeFunnet extends LeafSpecification<Faktagrunnlag> {

    public static final String ID = "AVVIK-PERSONIKKEFUNNET";

    public PersonIkkeFunnet() {
        super(ID);
    }

    @Override
    public Evaluation evaluate(Faktagrunnlag faktagrunnlag) {
        return nei();
    }
}
