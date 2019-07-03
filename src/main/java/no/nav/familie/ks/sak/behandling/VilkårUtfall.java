package no.nav.familie.ks.sak.behandling;

import no.nav.familie.ks.sak.behandling.grunnlag.Faktagrunnlag;
import no.nav.familie.ks.sak.resultat.*;
import no.nav.familie.ks.sak.resultat.årsak.VilkårIkkeOppfyltÅrsak;
import no.nav.familie.ks.sak.resultat.årsak.VilkårOppfyltÅrsak;
import no.nav.familie.ks.sak.resultat.årsak.VilkårÅrsak;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.evaluation.RuleReasonRef;
import no.nav.fpsak.nare.evaluation.RuleReasonRefImpl;
import no.nav.fpsak.nare.evaluation.node.SingleEvaluation;
import no.nav.fpsak.nare.specification.LeafSpecification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;

class VilkårUtfall extends LeafSpecification<Faktagrunnlag> {

    public static final String UTFALL = "UTFALL";
    public static final String ÅRSAK = "ÅRSAK";


    private final VilkårÅrsak vurderingÅrsak;
    private final RuleReasonRef ruleReasonRef;
    private final List<BiConsumer<SingleEvaluation, Faktagrunnlag>> utfallSpesifiserere = new ArrayList<>();

    VilkårUtfall(String id, VilkårOppfyltÅrsak vurderingÅrsak) {
        this(id, vurderingÅrsak, UtfallType.OPPFYLT);
    }

    VilkårUtfall(String id, VilkårIkkeOppfyltÅrsak vurderingÅrsak) {
        this(id, vurderingÅrsak, UtfallType.IKKE_OPPFYLT);
    }

    private VilkårUtfall(String id, VilkårÅrsak vurderingÅrsak, UtfallType utfallType) {
        super(id);
        if (vurderingÅrsak == null) {
            throw new IllegalArgumentException("Årsak kan ikke være null.");
        }
        this.vurderingÅrsak = vurderingÅrsak;
        this.ruleReasonRef = new RuleReasonRefImpl(String.valueOf(vurderingÅrsak.getId()), vurderingÅrsak.getBeskrivelse());

        this.utfallSpesifiserere.add((singleEvaluation, grunnlag) -> {
            singleEvaluation.getEvaluationProperties().put(UTFALL, utfallType);
            singleEvaluation.getEvaluationProperties().put(ÅRSAK, vurderingÅrsak);
        });
    }

    @Override
    public Evaluation evaluate(Faktagrunnlag grunnlag) {
        SingleEvaluation utfall = getHovedUtfall();
        spesifiserUtfall(utfall, grunnlag);
        return utfall;
    }

    private void spesifiserUtfall(SingleEvaluation utfall, Faktagrunnlag grunnlag) {
        if (utfallSpesifiserere.isEmpty()) {
            return;
        }
        utfall.setEvaluationProperties(new HashMap<>());
        utfallSpesifiserere.forEach(utfallSpesifiserer -> utfallSpesifiserer.accept(utfall, grunnlag));
    }

    private SingleEvaluation getHovedUtfall() {
        if (vurderingÅrsak instanceof VilkårOppfyltÅrsak) {
            return ja(ruleReasonRef);
        }
        return nei(ruleReasonRef);
    }

}
