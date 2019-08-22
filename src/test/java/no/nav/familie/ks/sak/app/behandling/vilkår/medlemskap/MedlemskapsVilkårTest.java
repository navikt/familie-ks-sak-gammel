package no.nav.familie.ks.sak.app.behandling.vilkår.medlemskap;

import no.nav.familie.ks.sak.FaktagrunnlagBuilder;
import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.fpsak.nare.evaluation.Resultat;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MedlemskapsVilkårTest {

    @Test
    public void skal_vurdere_begge_delvilkår() {
        final var vilkår = new MedlemskapsVilkår();
        Faktagrunnlag faktagrunnlag = FaktagrunnlagBuilder.beggeForeldreIkkeNorskStatsborgerMenBorINorge();
        final var evaluering = vilkår.evaluer(faktagrunnlag);

        assertThat(evaluering).isNotNull();
    }

    @Test
    public void utenlandsk_statsborgerskap_skal_gi_ikke_oppfylt() {
        final var vilkår = new MedlemskapsVilkår();
        Faktagrunnlag faktagrunnlag = FaktagrunnlagBuilder.beggeForeldreIkkeNorskStatsborgerMenBorINorge();
        final var evaluering = vilkår.evaluer(faktagrunnlag);
        assertThat(evaluering.result()).isEqualByComparingTo(Resultat.NEI);
    }

    @Test
    public void utenlandsk_bosted_skal_gi_ikke_oppfylt() {
        final var vilkår = new MedlemskapsVilkår();
        Faktagrunnlag faktagrunnlag = FaktagrunnlagBuilder.beggeForeldreNorskStatsborgerskapMenBoddIUtland();
        final var evaluering = vilkår.evaluer(faktagrunnlag);
        assertThat(evaluering.result()).isEqualByComparingTo(Resultat.NEI);
    }


    @Test
    public void norsk_statsborgerskap_og_bosted_fem_år_skal_gi_oppfylt() {
        final var vilkår = new MedlemskapsVilkår();
        Faktagrunnlag faktagrunnlag = FaktagrunnlagBuilder.familieNorskStatsborgerskap();
        final var evaluering = vilkår.evaluer(faktagrunnlag);
        assertThat(evaluering.result()).isEqualByComparingTo(Resultat.JA);
    }
}
