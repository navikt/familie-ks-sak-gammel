package no.nav.familie.ks.sak.app.behandling;

import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.VilkårType;
import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.UtfallType;
import no.nav.familie.ks.sak.app.behandling.resultat.årsak.VilkårÅrsak;
import no.nav.familie.ks.sak.app.behandling.vilkår.Regelresultat;
import no.nav.fpsak.nare.evaluation.Evaluation;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SamletVilkårsVurdering {

    private final Map<VilkårType, Evaluation> vurderinger;
    private final Faktagrunnlag faktagrunnlag;

    SamletVilkårsVurdering(Map<VilkårType, Evaluation> vurderinger, Faktagrunnlag faktagrunnlag) {
        this.vurderinger = vurderinger;
        this.faktagrunnlag = faktagrunnlag;
    }

    public List<Regelresultat> getResultater() {
        return vurderinger.entrySet().stream()
                .map(entry -> new Regelresultat(entry.getKey(), faktagrunnlag, entry.getValue()))
                .collect(Collectors.toList());
    }

    public UtfallType getUtfallType() {
        final var utfall = getResultater()
                .stream()
                .map(Regelresultat::getUtfallType)
                .distinct()
                .collect(Collectors.toList());
        if (utfall.size() == 1) {
            return utfall.get(0);
        }
        return UtfallType.IKKE_OPPFYLT;
    }

    public Set<VilkårÅrsak> getÅrsakType() {
        return getResultater()
                .stream()
                .filter(it -> it.getUtfallType().equals(getUtfallType()))
                .map(Regelresultat::getUtfallÅrsak)
                .collect(Collectors.toSet());
    }
}
