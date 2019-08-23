package no.nav.familie.ks.sak.app.behandling.vilkår;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.familie.ks.sak.app.behandling.VilkårRegelFeil;
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.VilkårType;
import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.UtfallType;
import no.nav.familie.ks.sak.app.behandling.resultat.årsak.VilkårÅrsak;
import no.nav.familie.ks.sak.config.JacksonJsonConfig;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.evaluation.summary.EvaluationSerializer;
import no.nav.fpsak.nare.evaluation.summary.EvaluationSummary;

import java.util.Optional;

public class Regelresultat {

    private final VilkårType vilkårType;
    private final Faktagrunnlag faktagrunnlag;
    private final Evaluation evaluation;
    private final EvaluationSummary evaluationSummary;
    private final ObjectMapper objectMapper = new JacksonJsonConfig().objectMapper();

    public Regelresultat(VilkårType vilkårType, Faktagrunnlag faktagrunnlag, Evaluation evaluation) {
        this.vilkårType = vilkårType;
        this.faktagrunnlag = faktagrunnlag;
        this.evaluation = evaluation;
        this.evaluationSummary = new EvaluationSummary(this.evaluation);
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

    public String getInputJson() {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(faktagrunnlag);
        } catch (JsonProcessingException e) {
            throw new VilkårRegelFeil("Kunne ikke serialisere regelinput for avklaring av uttaksperioder.", e);
        }
    }

    public String getRegelSporingJson() {
        return EvaluationSerializer.asJson(evaluation);
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

    public VilkårType getVilkårType() {
        return vilkårType;
    }
}
