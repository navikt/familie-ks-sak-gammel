package no.nav.familie.ks.sak.app.behandling.resultat.årsak;

import no.nav.fpsak.nare.evaluation.RuleReasonRef;

public interface VilkårÅrsak extends RuleReasonRef {

    default String getKode() {
        return "" + getÅrsakKode();
    }

    int getÅrsakKode();

    String getBeskrivelse();
}
