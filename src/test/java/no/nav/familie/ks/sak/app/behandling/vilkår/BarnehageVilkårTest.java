package no.nav.familie.ks.sak.app.behandling.vilkår;

import no.nav.familie.ks.sak.FaktagrunnlagBuilder;
import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.behandling.regel.vilkår.barnehage.BarnehageVilkår;
import no.nav.fpsak.nare.evaluation.Resultat;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BarnehageVilkårTest {

    @Test
    public void søknad_med_barnehage_gir_ikke_oppfylt() {
        final var vilkår = new BarnehageVilkår();
        Faktagrunnlag faktagrunnlag = FaktagrunnlagBuilder.familieNorskStatsborgerskapMedBarnehage();
        final var evaluering = vilkår.evaluer(faktagrunnlag);
        assertThat(evaluering.result()).isEqualByComparingTo(Resultat.NEI);
    }

    @Test
    public void søknad_uten_barnehage_gir_oppfylt() {
        final var vilkår = new BarnehageVilkår();
        Faktagrunnlag faktagrunnlag = FaktagrunnlagBuilder.familieNorskStatsborgerskapUtenBarnehage();
        final var evaluering = vilkår.evaluer(faktagrunnlag);
        assertThat(evaluering.result()).isEqualByComparingTo(Resultat.JA);
    }
}
