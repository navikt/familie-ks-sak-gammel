package no.nav.familie.ks.sak.app.behandling.vilkår.medlemskap;

import no.nav.familie.ks.sak.FaktagrunnlagBuilder;
import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
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
}