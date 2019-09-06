package no.nav.familie.ks.sak.app.behandling.vilkår;

import no.nav.familie.ks.sak.FaktagrunnlagBuilder;
import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.behandling.regel.vilkår.bosted.BostedVilkår;
import no.nav.fpsak.nare.evaluation.Resultat;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BostedVilkårTest {

    @Test
    public void kun_en_forelder_gir_ikke_oppfylt() {
        final var vilkår = new BostedVilkår();
        Faktagrunnlag faktagrunnlag = FaktagrunnlagBuilder.aleneForelderNorskStatsborgerskapUtenBarnehage();
        final var evaluering = vilkår.evaluer(faktagrunnlag);
        assertThat(evaluering.result()).isEqualByComparingTo(Resultat.NEI);
    }

    @Test
    public void ikke_samme_bosted_gir_ikke_oppfylt() {
        final var vilkår = new BostedVilkår();
        Faktagrunnlag faktagrunnlag = FaktagrunnlagBuilder.familieMedEnForelderIUtland();
        final var evaluering = vilkår.evaluer(faktagrunnlag);
        assertThat(evaluering.result()).isEqualByComparingTo(Resultat.NEI);
    }

    @Test
    public void to_foreldre_og_samme_bosted_gir_oppfylt() {
        final var vilkår = new BostedVilkår();
        Faktagrunnlag faktagrunnlag = FaktagrunnlagBuilder.familieNorskStatsborgerskapUtenBarnehage();
        final var evaluering = vilkår.evaluer(faktagrunnlag);
        assertThat(evaluering.result()).isEqualByComparingTo(Resultat.JA);
    }
}
