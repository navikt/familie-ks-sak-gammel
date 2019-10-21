package no.nav.familie.ks.sak.app.behandling.vilkår;

import no.nav.familie.ks.sak.FaktagrunnlagTestBuilder;
import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.behandling.regel.mvp.annenPart.AnnenPartErOppgittVilkår;
import no.nav.familie.ks.sak.app.behandling.regel.mvp.annenPart.AnnenPartStemmerVilkår;
import no.nav.fpsak.nare.evaluation.Resultat;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AnnenPartVilkårTest {

    @Test
    public void søker_og_annen_part_gir_oppfylt() {
        final var vilkår = new AnnenPartErOppgittVilkår();
        Faktagrunnlag faktagrunnlag = FaktagrunnlagTestBuilder.familieNorskStatsborgerskapUtenBarnehage();
        final var evaluering = vilkår.evaluer(faktagrunnlag);
        assertThat(evaluering.result()).isEqualByComparingTo(Resultat.JA);
    }

    @Test
    public void søker_og_uten_annen_part_gir_ikke_oppfylt() {
        final var vilkår = new AnnenPartErOppgittVilkår();
        Faktagrunnlag faktagrunnlag = FaktagrunnlagTestBuilder.aleneForelderNorskStatsborgerskapUtenBarnehage();
        final var evaluering = vilkår.evaluer(faktagrunnlag);
        assertThat(evaluering.result()).isEqualByComparingTo(Resultat.NEI);
    }

    @Test
    public void søker_og_annen_part_stemmer_gir_oppfylt() {
        final var vilkår = new AnnenPartStemmerVilkår();
        Faktagrunnlag faktagrunnlag = FaktagrunnlagTestBuilder.familieNorskStatsborgerskapUtenBarnehage();
        final var evaluering = vilkår.evaluer(faktagrunnlag);
        assertThat(evaluering.result()).isEqualByComparingTo(Resultat.JA);
    }

    @Test
    public void søker_og_annen_part_stemmer_delvis_gir_oppfylt() {
        final var vilkår = new AnnenPartStemmerVilkår();
        Faktagrunnlag faktagrunnlag = FaktagrunnlagTestBuilder.familieNorskStatsborgerskapUtenBarnehageAnnenPartStemmerDelvis();
        final var evaluering = vilkår.evaluer(faktagrunnlag);
        assertThat(evaluering.result()).isEqualByComparingTo(Resultat.JA);
    }

    @Test
    public void søker_og_annen_part_stemmer_ikke_gir_ikke_oppfylt() {
        final var vilkår = new AnnenPartStemmerVilkår();
        Faktagrunnlag faktagrunnlag = FaktagrunnlagTestBuilder.familieNorskStatsborgerskapUtenBarnehageAnnenPartStemmerIkke();
        final var evaluering = vilkår.evaluer(faktagrunnlag);
        assertThat(evaluering.result()).isEqualByComparingTo(Resultat.NEI);
    }
}
