package no.nav.familie.ks.sak.app.behandling.avvik;

import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.AvvikType;
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.UtfallType;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AvviksVurderingTest {

    @Test
    public void skal_gi_ett_avvik_med_manuell_behandling() {
        AvviksVurdering avviksVurdering = new AvviksVurdering();

        assertThat(avviksVurdering.getSamletUtfallType()).isEqualByComparingTo(UtfallType.MANUELL_BEHANDLING);
        assertThat(avviksVurdering.getAvvik().size()).isEqualTo(1);
        assertThat(avviksVurdering.getResultater().get(0).getAvvikType()).isEqualByComparingTo(AvvikType.AVVIK_PERSON_IKKE_FUNNET);
        assertThat(avviksVurdering.getResultater().get(0).getUtfallType()).isEqualByComparingTo(UtfallType.MANUELL_BEHANDLING);
    }
}
