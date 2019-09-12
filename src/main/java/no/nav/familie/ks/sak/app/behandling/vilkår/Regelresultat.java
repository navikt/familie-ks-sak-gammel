package no.nav.familie.ks.sak.app.behandling.vilkår;

import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.UtfallType;
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.VilkårType;
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.årsak.VilkårUtfallÅrsak;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.evaluation.Resultat;
import no.nav.fpsak.nare.evaluation.summary.EvaluationSerializer;
import no.nav.fpsak.nare.evaluation.summary.EvaluationSummary;

import java.util.Collection;

public class Regelresultat {

    private final VilkårType vilkårType;
    private final Evaluation evaluation;
    private final EvaluationSummary evaluationSummary;


    public Regelresultat(VilkårType vilkårType, Evaluation evaluation) {
        this.vilkårType = vilkårType;
        this.evaluation = evaluation;
        this.evaluationSummary = new EvaluationSummary(this.evaluation);
    }

    public UtfallType getUtfallType() {
        Collection<Evaluation> leafEvaluations = evaluationSummary.leafEvaluations();
        for (Evaluation ev : leafEvaluations) {
            if (ev.getOutcome() != null) {
                Resultat res = ev.result();
                switch (res) {
                    case JA:
                        return UtfallType.OPPFYLT;
                    case NEI:
                        return UtfallType.IKKE_OPPFYLT;
                    case IKKE_VURDERT:
                        return UtfallType.IKKE_VURDERT;
                    default:
                        throw new IllegalArgumentException("Ukjent Resultat:" + res + " ved evaluering av:" + ev);
                }
            } else {
                return UtfallType.OPPFYLT;
            }
        }

        throw new IllegalArgumentException("leafEvaluations.isEmpty():" + leafEvaluations);
    }

    public String getRegelSporingJson() {
        return EvaluationSerializer.asJson(evaluation);
    }

    public VilkårUtfallÅrsak getUtfallÅrsak() {
        Collection<Evaluation> leafEvaluations = evaluationSummary.leafEvaluations();
        for (Evaluation ev : leafEvaluations) {
            if (ev.getOutcome() != null) {
                return (VilkårUtfallÅrsak) ev.getOutcome();
            }
        }

        throw new IllegalStateException("Utfall mangler årsak. LeafSpesification er ikke riktig definert.");
    }

    public VilkårType getVilkårType() {
        return vilkårType;
    }

    @Override
    public String toString() {
        return getUtfallÅrsak().getKode();
    }
}
