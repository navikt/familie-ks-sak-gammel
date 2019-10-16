package no.nav.familie.ks.sak.app.behandling.domene.resultat.vilkår;


import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.UtfallType;
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.VilkårType;
import org.junit.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class VilkårsResultatTest {

    @Test
    public void skal_utlede_rett_utfall() {
        final var vilkårsResultat = new VilkårsResultat(Set.of(new VilkårResultat(VilkårType.MEDLEMSKAP_BOSTED, UtfallType.OPPFYLT, null, null)));

        assertThat(vilkårsResultat.getSamletUtfall()).isEqualTo(UtfallType.OPPFYLT);
    }

    @Test
    public void skal_utlede_rett_utfall_2() {
        final var vilkårsResultat = new VilkårsResultat(Set.of(new VilkårResultat(VilkårType.MEDLEMSKAP_BOSTED, UtfallType.OPPFYLT, null, null),
                new VilkårResultat(VilkårType.BARNEHAGE, UtfallType.OPPFYLT, null, null)));

        assertThat(vilkårsResultat.getSamletUtfall()).isEqualTo(UtfallType.OPPFYLT);
    }

    @Test
    public void skal_utlede_rett_utfall_3() {
        final var vilkårsResultat = new VilkårsResultat(Set.of(new VilkårResultat(VilkårType.MEDLEMSKAP_BOSTED, UtfallType.OPPFYLT, null, null),
                new VilkårResultat(VilkårType.BARNEHAGE, UtfallType.MANUELL_BEHANDLING, null, null)));

        assertThat(vilkårsResultat.getSamletUtfall()).isEqualTo(UtfallType.MANUELL_BEHANDLING);
    }

    @Test
    public void skal_utlede_rett_utfall_4() {
        final var vilkårsResultat = new VilkårsResultat(Set.of(new VilkårResultat(VilkårType.MEDLEMSKAP_BOSTED, UtfallType.OPPFYLT, null, null),
                new VilkårResultat(VilkårType.BARNEHAGE, UtfallType.IKKE_OPPFYLT, null, null)));

        assertThat(vilkårsResultat.getSamletUtfall()).isEqualTo(UtfallType.IKKE_OPPFYLT);
    }

    @Test
    public void skal_utlede_rett_utfall_5() {
        final var vilkårsResultat = new VilkårsResultat(Set.of(new VilkårResultat(VilkårType.MEDLEMSKAP_BOSTED, UtfallType.OPPFYLT, null, null),
                new VilkårResultat(VilkårType.BARNEHAGE, UtfallType.IKKE_VURDERT, null, null)));

        assertThat(vilkårsResultat.getSamletUtfall()).isEqualTo(UtfallType.UAVKLART);
    }
}
