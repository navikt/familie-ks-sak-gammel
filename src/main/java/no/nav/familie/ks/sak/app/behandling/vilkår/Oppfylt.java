package no.nav.familie.ks.sak.app.behandling.vilkår;

import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.årsak.VilkårOppfyltÅrsak;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.specification.LeafSpecification;

@SuppressWarnings("rawtypes")
public class Oppfylt extends LeafSpecification {

    public Oppfylt() {
        super(VilkårOppfyltÅrsak.VILKÅR_OPPFYLT.getReasonCode());
    }

    @Override
    public Evaluation evaluate(Object grunnlag) {
        return ja();
    }

    @Override
    public String beskrivelse() {
        return VilkårOppfyltÅrsak.VILKÅR_OPPFYLT.getBeskrivelse();
    }
}
