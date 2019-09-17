package no.nav.familie.ks.sak;

import no.nav.familie.ks.sak.app.behandling.BehandlingslagerService;
import no.nav.familie.ks.sak.app.behandling.ResultatService;
import no.nav.familie.ks.sak.app.behandling.VurderSamletTjeneste;
import no.nav.familie.ks.sak.app.behandling.domene.Behandling;
import no.nav.familie.ks.sak.app.behandling.domene.Fagsak;
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.UtfallType;
import no.nav.familie.ks.sak.app.behandling.domene.typer.AktørId;
import no.nav.familie.ks.sak.app.behandling.fastsetting.FastsettingService;
import no.nav.familie.ks.sak.app.behandling.resultat.Vedtak;
import no.nav.familie.ks.sak.app.behandling.vilkår.barn.BarneVilkår;
import no.nav.familie.ks.sak.app.behandling.vilkår.medlemskap.MedlemskapsVilkår;
import no.nav.familie.ks.sak.app.grunnlag.Søknad;
import no.nav.familie.ks.sak.app.integrasjon.RegisterInnhentingException;
import no.nav.familie.ks.sak.config.JacksonJsonConfig;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SaksbehandlingTest {
    private final BehandlingslagerService behandlingslagerMock = mock(BehandlingslagerService.class);
    private final VurderSamletTjeneste vurderSamletTjeneste = new VurderSamletTjeneste(List.of(new BarneVilkår(), new MedlemskapsVilkår()));
    private final FastsettingService fastsettingServiceMock = mock(FastsettingService.class);
    private final Saksbehandling saksbehandling = new Saksbehandling(vurderSamletTjeneste, behandlingslagerMock, fastsettingServiceMock, mock(ResultatService.class), new JacksonJsonConfig().objectMapper());

    @Test
    public void positivt_vedtak_ved_oppfylte_vilkår() throws RegisterInnhentingException {
        when(fastsettingServiceMock.fastsettFakta(any(), any())).thenReturn(FaktagrunnlagBuilder.familieNorskStatsborgerskapUtenBarnehage());
        when(behandlingslagerMock.trekkUtOgPersister(any())).thenReturn(Behandling.forFørstegangssøknad(new Fagsak(new AktørId(0L), "")).build());

        Søknad innsendtSøknad = FaktagrunnlagBuilder.utenBarnehageplass(FaktagrunnlagBuilder.norskPersonIdent.getIdent());
        Vedtak vedtak = saksbehandling.behandle(innsendtSøknad);
        assertThat(vedtak.getVilkårvurdering().getSamletUtfallType()).isEqualTo(UtfallType.OPPFYLT);
    }

    @Test
    public void negativt_vedtak_ved_ikke_oppfylte_vilkår() throws RegisterInnhentingException {
        when(fastsettingServiceMock.fastsettFakta(any(), any())).thenReturn(FaktagrunnlagBuilder.familieUtenlandskStatsborgerskapMedBarnehage());
        when(behandlingslagerMock.trekkUtOgPersister(any())).thenReturn(Behandling.forFørstegangssøknad(new Fagsak(new AktørId(0L), "")).build());

        Vedtak vedtak = saksbehandling.behandle(getFile("soknadGradertBarnehageplass.json"));
        assertThat(vedtak.getVilkårvurdering().getSamletUtfallType()).isEqualTo(UtfallType.IKKE_OPPFYLT);
    }

    private String getFile(String filnavn) {
        return getClass().getClassLoader().getResource(filnavn).getFile();
    }
}
