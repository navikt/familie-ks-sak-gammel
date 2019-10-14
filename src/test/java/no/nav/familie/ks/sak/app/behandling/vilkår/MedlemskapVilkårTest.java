package no.nav.familie.ks.sak.app.behandling.vilkår;

import no.nav.familie.ks.sak.FaktagrunnlagTestBuilder;
import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.behandling.regel.vilkår.medlemskap.MedlemskapBostedVilkår;
import no.nav.familie.ks.sak.app.behandling.regel.vilkår.medlemskap.MedlemskapMedlVilkår;
import no.nav.familie.ks.sak.app.behandling.regel.vilkår.medlemskap.MedlemskapStatsborgerskapVilkår;
import no.nav.fpsak.nare.evaluation.Resultat;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MedlemskapVilkårTest {

    @Test
    public void vurderer_begge_delvilkår() {
        final var vilkår = new MedlemskapBostedVilkår();
        Faktagrunnlag faktagrunnlag = FaktagrunnlagTestBuilder.beggeForeldreIkkeNorskStatsborgerMenBorINorge();
        final var evaluering = vilkår.evaluer(faktagrunnlag);

        assertThat(evaluering).isNotNull();
    }

    @Test
    public void utenlandsk_statsborgerskap_gir_ikke_oppfylt() {
        final var vilkår = new MedlemskapStatsborgerskapVilkår();
        Faktagrunnlag faktagrunnlag = FaktagrunnlagTestBuilder.beggeForeldreIkkeNorskStatsborgerMenBorINorge();
        final var evaluering = vilkår.evaluer(faktagrunnlag);
        assertThat(evaluering.result()).isEqualByComparingTo(Resultat.NEI);
    }

    @Test
    public void utenlandsk_bosted_gir_ikke_oppfylt() {
        final var vilkår = new MedlemskapBostedVilkår();
        Faktagrunnlag faktagrunnlag = FaktagrunnlagTestBuilder.beggeForeldreNorskStatsborgerskapMenBoddIUtland();
        final var evaluering = vilkår.evaluer(faktagrunnlag);
        assertThat(evaluering.result()).isEqualByComparingTo(Resultat.NEI);
    }

    @Test
    public void norsk_bosted_mindre_enn_fem_år_gir_ikke_oppfylt() {
        final var vilkår = new MedlemskapBostedVilkår();
        Faktagrunnlag faktagrunnlag = FaktagrunnlagTestBuilder.beggeForeldreNorskStatsborgerskapMenBoddINorgeMindreEnnFemÅr();
        final var evaluering = vilkår.evaluer(faktagrunnlag);
        assertThat(evaluering.result()).isEqualByComparingTo(Resultat.NEI);
    }


    @Test
    public void norsk_statsborgerskap_og_bosted_fem_år_gir_oppfylt() {
        final var vilkår = new MedlemskapBostedVilkår();
        Faktagrunnlag faktagrunnlag = FaktagrunnlagTestBuilder.familieNorskStatsborgerskapUtenBarnehage();
        final var evaluering = vilkår.evaluer(faktagrunnlag);
        assertThat(evaluering.result()).isEqualByComparingTo(Resultat.JA);
    }

    @Test
    public void medlemskapsopplysninger_gir_ikke_oppfylt() {
        final var vilkår = new MedlemskapMedlVilkår();
        Faktagrunnlag faktagrunnlag = FaktagrunnlagTestBuilder.familieNorskStatsborgerskapMedMedlemskapsinfo();
        final var evaluering = vilkår.evaluer(faktagrunnlag);
        assertThat(evaluering.result()).isEqualByComparingTo(Resultat.NEI);
    }

    @Test
    public void ingen_medlemskapsopplysninger_gir_oppfylt() {
        final var vilkår = new MedlemskapMedlVilkår();
        Faktagrunnlag faktagrunnlag = FaktagrunnlagTestBuilder.familieNorskStatsborgerskapUtenBarnehage();
        final var evaluering = vilkår.evaluer(faktagrunnlag);
        assertThat(evaluering.result()).isEqualByComparingTo(Resultat.JA);
    }
}
