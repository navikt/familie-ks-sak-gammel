package no.nav.familie.ks.sak.app.behandling.vilkår;

import no.nav.familie.ks.sak.FaktagrunnlagBuilder;
import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.behandling.regel.mvp.barn.KunEtBarnVilkår;
import no.nav.fpsak.nare.evaluation.Resultat;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class KunEtBarnVilkårTest {

    @Test
    public void søknad_med_flere_barn_gir_ikke_oppfylt() {
        final var vilkår = new KunEtBarnVilkår();
        Faktagrunnlag faktagrunnlag = FaktagrunnlagBuilder.familieNorskStatsborgerskapUtenBarnehageFlerlinger();
        final var evaluering = vilkår.evaluer(faktagrunnlag);
        assertThat(evaluering.result()).isEqualByComparingTo(Resultat.NEI);
    }

    @Test
    public void søknad_uten_flere_barn_gir_oppfylt() {
        final var vilkår = new KunEtBarnVilkår();
        Faktagrunnlag faktagrunnlag = FaktagrunnlagBuilder.familieNorskStatsborgerskapUtenBarnehage();
        final var evaluering = vilkår.evaluer(faktagrunnlag);
        assertThat(evaluering.result()).isEqualByComparingTo(Resultat.JA);
    }
}
