package no.nav.familie.ks.sak.behandling;

import no.nav.familie.ks.sak.resultat.årsak.VilkårÅrsak;
import no.nav.familie.ks.sak.resultat.UtfallType;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.evaluation.summary.EvaluationSummary;

import java.util.Optional;

public class Regelresultat {

    private final EvaluationSummary evaluationSummary;

    public Regelresultat(Evaluation evaluation) {
        this.evaluationSummary = new EvaluationSummary(evaluation);
    }

    private <T> T getProperty(String tag, Class<T> clazz) {
        Object obj = getProperty(tag);
        if (obj != null && !clazz.isAssignableFrom(obj.getClass())) {
            throw new IllegalArgumentException("Kan ikke hente property " + tag + ". Forventet " + clazz.getSimpleName() + " men fant " + obj.getClass());
        }
        return (T) obj;
    }

    public UtfallType getUtfallType() {
        return getProperty(VilkårUtfall.UTFALL, UtfallType.class);
    }

    public VilkårÅrsak getUtfallÅrsak() {
        return getProperty(VilkårUtfall.ÅRSAK, VilkårÅrsak.class);
    }

    private Object getProperty(String tag) {
        Optional<Evaluation> first = evaluationSummary.leafEvaluations().stream()
            .filter(e -> e.getEvaluationProperties() != null)
            .findFirst();

        return first.map(evaluation -> evaluation.getEvaluationProperties().get(tag)).orElse(null);
    }

}
