package no.nav.familie.ks.sak.app.behandling;

import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.behandling.resultat.UtfallType;
import no.nav.familie.ks.sak.app.behandling.resultat.årsak.VilkårÅrsak;
import no.nav.familie.ks.sak.app.behandling.vilkår.Regelresultat;
import no.nav.fpsak.nare.evaluation.Evaluation;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SamletVilkårsVurdering {

    private final List<Evaluation> vurderinger;
    private final Faktagrunnlag faktagrunnlag;

    SamletVilkårsVurdering(List<Evaluation> vurderinger, Faktagrunnlag faktagrunnlag) {
        this.vurderinger = vurderinger;
        this.faktagrunnlag = faktagrunnlag;
    }

    public List<Regelresultat> getResultater() {
        return vurderinger.stream()
                .map(evaluation -> new Regelresultat(faktagrunnlag, evaluation))
                .collect(Collectors.toList());
    }

    public UtfallType getUtfallType() {
        final var utfall = vurderinger.stream()
                .map(evaluation -> new Regelresultat(faktagrunnlag, evaluation))
                .map(Regelresultat::getUtfallType)
                .distinct()
                .collect(Collectors.toList());
        if (utfall.size() == 1) {
            return utfall.get(0);
        }
        return UtfallType.IKKE_OPPFYLT;
    }

    public Set<VilkårÅrsak> getÅrsakType() {
        return vurderinger.stream()
                .map(evaluation -> new Regelresultat(faktagrunnlag, evaluation))
                .map(Regelresultat::getUtfallÅrsak)
                .collect(Collectors.toSet());
    }
}
