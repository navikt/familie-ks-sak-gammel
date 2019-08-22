package no.nav.familie.ks.sak.app.behandling.vilkår.medlemskap;

import no.nav.familie.ks.sak.FaktagrunnlagBuilder;
import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.behandling.vilkår.barn.BarneVilkår;
import no.nav.fpsak.nare.evaluation.Resultat;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BarneVilkårTest {

    @Test
    public void at_norsk_statsborgerskap_på_barn_gir_oppfylt() {
        final var vilkår = new BarneVilkår();
        Faktagrunnlag faktagrunnlag = FaktagrunnlagBuilder.familieNorskStatsborgerskap();
        final var evaluering = vilkår.evaluer(faktagrunnlag);
        assertThat(evaluering.result()).isEqualByComparingTo(Resultat.JA);
    }

    @Test
    public void at_utenlandsk_statsborgerskap_på_barn_gir_oppfylt() {
        final var vilkår = new BarneVilkår();
        Faktagrunnlag faktagrunnlag = FaktagrunnlagBuilder.familieUtenlandskStatsborgerskap();
        final var evaluering = vilkår.evaluer(faktagrunnlag);
        assertThat(evaluering.result()).isEqualByComparingTo(Resultat.NEI);
    }
}
