package no.nav.familie.ks.sak;

import no.nav.familie.ks.sak.app.behandling.BehandlingslagerService;
import no.nav.familie.ks.sak.app.behandling.ResultatService;
import no.nav.familie.ks.sak.app.behandling.VurderSamletTjeneste;
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.UtfallType;
import no.nav.familie.ks.sak.app.behandling.resultat.Vedtak;
import no.nav.familie.ks.sak.app.behandling.vilkår.medlemskap.MedlemskapsVilkår;
import no.nav.familie.ks.sak.app.behandling.vilkår.barn.BarneVilkår;
import no.nav.familie.ks.sak.app.grunnlag.OppslagTjeneste;
import no.nav.familie.ks.sak.config.JacksonJsonConfig;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SaksbehandlingTest {

    private final OppslagTjeneste oppslagMock = mock(OppslagTjeneste.class);
    private final VurderSamletTjeneste vurderSamletTjeneste = new VurderSamletTjeneste(List.of(new BarneVilkår(), new MedlemskapsVilkår()));
    private final Saksbehandling saksbehandling = new Saksbehandling(oppslagMock, vurderSamletTjeneste, mock(BehandlingslagerService.class), mock(ResultatService.class), new JacksonJsonConfig().objectMapper());

    @Test
    public void positivt_vedtak_ved_oppfylte_vilkår() {
        when(oppslagMock.hentTpsFakta(any())).thenReturn(FaktagrunnlagBuilder.faktaBeggeForeldreOgBarnNorskStatsborger());
        Vedtak vedtak = saksbehandling.behandle(getFile("soknadUtenBarnehageplass.json"));

        assertThat(vedtak.getVilkårvurdering().getSamletUtfallType()).isEqualTo(UtfallType.OPPFYLT);
    }

    @Test
    public void negativt_vedtak_ved_ikke_oppfylte_vilkår() {
        when(oppslagMock.hentTpsFakta(any())).thenReturn(FaktagrunnlagBuilder.faktaBeggeForeldreOgBarnUtenlandskeStatsborgere());
        Vedtak vedtak = saksbehandling.behandle(getFile("soknadGradertBarnehageplass.json"));

        assertThat(vedtak.getVilkårvurdering().getSamletUtfallType()).isEqualTo(UtfallType.IKKE_OPPFYLT);
    }

    private String getFile(String filnavn) {
        return getClass().getClassLoader().getResource(filnavn).getFile();
    }
}
