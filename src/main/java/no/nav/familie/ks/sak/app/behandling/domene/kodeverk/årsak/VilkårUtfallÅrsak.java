package no.nav.familie.ks.sak.app.behandling.domene.kodeverk.årsak;

import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.VilkårType;
import no.nav.fpsak.nare.evaluation.RuleReasonRef;

public interface VilkårUtfallÅrsak extends RuleReasonRef {

    default String getKode() {
        return "" + getÅrsakKode();
    }

    int getÅrsakKode();

    String getBeskrivelse();

    /**
     * Vilkåret årsaken gjelder for
     *
     * @return
     */
    VilkårType getVilkårType();

    /**
     * Lov referansen for utfallet
     * @return
     */
    default String getLovReferanse() {
        return getVilkårType().getLovReferanse();
    }
}
