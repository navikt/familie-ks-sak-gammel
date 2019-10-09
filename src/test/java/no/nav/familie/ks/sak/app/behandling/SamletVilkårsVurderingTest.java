package no.nav.familie.ks.sak.app.behandling;

import no.nav.familie.ks.sak.FaktagrunnlagTestBuilder;
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.UtfallType;
import no.nav.familie.ks.sak.app.behandling.vilkår.InngangsvilkårRegel;
import no.nav.familie.ks.sak.app.behandling.vilkår.Regelresultat;
import no.nav.familie.ks.sak.app.behandling.regel.vilkår.barn.BarneVilkår;
import no.nav.familie.ks.sak.app.behandling.regel.vilkår.barnehage.BarnehageVilkår;
import no.nav.familie.ks.sak.app.behandling.regel.vilkår.medlemskap.MedlemskapsVilkår;
import no.nav.familie.ks.sak.app.behandling.regel.vilkår.bosted.BostedVilkår;
import no.nav.familie.ks.sak.app.behandling.regel.mvp.utland.UtlandVilkår;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class SamletVilkårsVurderingTest {

    private final List<InngangsvilkårRegel> inngangsvilkår = List.of(new BarnehageVilkår(), new MedlemskapsVilkår(), new BostedVilkår(), new BarneVilkår(), new UtlandVilkår());
    private final VurderSamletTjeneste vurderSamletTjeneste = new VurderSamletTjeneste(inngangsvilkår);

    @Test
    public void gir_oppfylt_når_alle_er_oppfylt() {
        final var faktagrunnlag = FaktagrunnlagTestBuilder.familieNorskStatsborgerskapUtenBarnehage();
        final var vurder = vurderSamletTjeneste.vurder(faktagrunnlag);
        final var alleUtfall = vurder.getResultater().stream().map(Regelresultat::getUtfallType).collect(Collectors.toList());
        assertThat(alleUtfall).hasSize(inngangsvilkår.size());
        assertThat(alleUtfall).containsExactlyInAnyOrder(UtfallType.OPPFYLT, UtfallType.OPPFYLT, UtfallType.OPPFYLT, UtfallType.OPPFYLT, UtfallType.OPPFYLT);
        assertThat(vurder.getSamletUtfallType()).isEqualTo(UtfallType.OPPFYLT);
    }

    @Test
    public void gir_ikke_oppfylt_når_ikke_alle_er_oppfylt() {
        final var faktagrunnlag = FaktagrunnlagTestBuilder.familieNorskStatsborgerskapMedBarnehage();

        final var vurder = vurderSamletTjeneste.vurder(faktagrunnlag);

        final var alleUtfall = vurder.getResultater().stream().map(Regelresultat::getUtfallType).collect(Collectors.toList());
        assertThat(alleUtfall).hasSize(inngangsvilkår.size());
        assertThat(alleUtfall).containsExactlyInAnyOrder(UtfallType.MANUELL_BEHANDLING, UtfallType.OPPFYLT, UtfallType.OPPFYLT, UtfallType.OPPFYLT,  UtfallType.OPPFYLT);
        assertThat(vurder.getSamletUtfallType()).isEqualTo(UtfallType.MANUELL_BEHANDLING);
    }

    @Test
    public void har_utfall_årsak_på_alle_regel_resultater() {
        final var faktagrunnlag = FaktagrunnlagTestBuilder.familieNorskStatsborgerskapMedBarnehage();

        final var vurder = vurderSamletTjeneste.vurder(faktagrunnlag);
        final var resultater = vurder.getResultater();

        for (Regelresultat resultat : resultater) {
            assertThat(resultat.getUtfallÅrsak()).isNotNull();
        }
    }
}
