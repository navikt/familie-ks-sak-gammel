package no.nav.familie.ks.sak;

import no.nav.familie.ks.sak.app.behandling.VurderSamletTjeneste;
import no.nav.familie.ks.sak.app.behandling.resultat.UtfallType;
import no.nav.familie.ks.sak.app.behandling.resultat.Vedtak;
import no.nav.familie.ks.sak.app.behandling.vilkår.barnehage.BarnehageVilkår;
import no.nav.familie.ks.sak.app.behandling.vilkår.MedlemskapsVilkår;
import no.nav.familie.ks.sak.app.grunnlag.Oppslag;
import no.nav.familie.ks.sak.config.JacksonJsonConfig;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SaksbehandlingTest {

    private final Oppslag oppslagMock = mock(Oppslag.class);
    private final VurderSamletTjeneste vurderSamletTjeneste = new VurderSamletTjeneste(List.of(new BarnehageVilkår(), new MedlemskapsVilkår()));
    private final Saksbehandling saksbehandling = new Saksbehandling(oppslagMock, vurderSamletTjeneste, new JacksonJsonConfig().objectMapper());
    private final String PERSONIDENT = "123";

    @Test
    public void positivt_vedtak_ved_oppfylte_vilkår() {
        when(oppslagMock.hentTpsFakta(any(), any())).thenReturn(FaktagrunnlagBuilder.faktaBeggeForeldreOgBarnNorskStatsborger());
        Vedtak vedtak = saksbehandling.behandle(getFile("soknadUtenBarnehageplass.json"), PERSONIDENT);

        assertThat(vedtak.getVilkårvurdering().getUtfallType()).isEqualTo(UtfallType.OPPFYLT);
    }

    @Test
    public void negativt_vedtak_ved_ikke_oppfylte_vilkår() {
        when(oppslagMock.hentTpsFakta(any(), any())).thenReturn(FaktagrunnlagBuilder.faktaBeggeForeldreOgBarnUtenlandskeStatsborgere());
        Vedtak vedtak = saksbehandling.behandle(getFile("soknadGradertBarnehageplass.json"), PERSONIDENT);

        assertThat(vedtak.getVilkårvurdering().getUtfallType()).isEqualTo(UtfallType.IKKE_OPPFYLT);
    }

    private String getFile(String filnavn) {
        return getClass().getClassLoader().getResource(filnavn).getFile();
    }
}
