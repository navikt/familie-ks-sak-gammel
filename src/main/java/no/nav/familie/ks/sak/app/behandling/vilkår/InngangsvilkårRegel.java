package no.nav.familie.ks.sak.app.behandling.vilkår;

import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.fpsak.nare.evaluation.Evaluation;

public interface InngangsvilkårRegel {

    Evaluation evaluer(Faktagrunnlag input);

}
