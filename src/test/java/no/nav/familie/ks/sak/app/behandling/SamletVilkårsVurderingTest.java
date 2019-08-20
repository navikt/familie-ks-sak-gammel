package no.nav.familie.ks.sak.app.behandling;

import no.nav.familie.ks.sak.FaktagrunnlagBuilder;
import no.nav.familie.ks.sak.app.behandling.resultat.UtfallType;
import no.nav.familie.ks.sak.app.behandling.vilkår.InngangsvilkårRegel;
import no.nav.familie.ks.sak.app.behandling.vilkår.Regelresultat;
import no.nav.familie.ks.sak.app.behandling.vilkår.barn.BarneVilkår;
import no.nav.familie.ks.sak.app.behandling.vilkår.medlemskap.MedlemskapsVilkår;
import no.nav.familie.ks.sak.app.behandling.vilkår.bosted.BostedVilkår;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class SamletVilkårsVurderingTest {

    private final List<InngangsvilkårRegel> inngangsvilkår = List.of(new BarneVilkår(), new MedlemskapsVilkår(), new BostedVilkår());
    private final VurderSamletTjeneste vurderSamletTjeneste = new VurderSamletTjeneste(inngangsvilkår);

    @Test
    public void skal_gi_oppfylt_når_alle_er_oppfylt() {
        final var faktagrunnlag = FaktagrunnlagBuilder.beggeForeldreNorskStatsborgerOgBarnHarGyldigAlder();
        final var vurder = vurderSamletTjeneste.vurder(faktagrunnlag);
        final var alleUtfall = vurder.getResultater().stream().map(Regelresultat::getUtfallType).collect(Collectors.toList());
        assertThat(alleUtfall).hasSize(inngangsvilkår.size());
        assertThat(alleUtfall).containsExactlyInAnyOrder(UtfallType.OPPFYLT, UtfallType.OPPFYLT, UtfallType.OPPFYLT);
        assertThat(vurder.getUtfallType()).isEqualTo(UtfallType.OPPFYLT);
    }

    @Test
    public void skal_gi_rett_utfall_når_ulike() {
        final var faktagrunnlag = FaktagrunnlagBuilder.beggeForeldreUtenlandskeStatsborgereOgBarnForGammel();

        final var vurder = vurderSamletTjeneste.vurder(faktagrunnlag);

        final var alleUtfall = vurder.getResultater().stream().map(Regelresultat::getUtfallType).collect(Collectors.toList());
        assertThat(alleUtfall).hasSize(inngangsvilkår.size());
        assertThat(alleUtfall).containsExactlyInAnyOrder(UtfallType.OPPFYLT, UtfallType.IKKE_OPPFYLT, UtfallType.IKKE_OPPFYLT);
        assertThat(vurder.getUtfallType()).isEqualTo(UtfallType.IKKE_OPPFYLT);

    }
}
