package no.nav.familie.ks.sak;

import no.nav.familie.ks.kontrakter.søknad.Søknad;
import no.nav.familie.ks.kontrakter.søknad.testdata.SøknadTestdata;
import no.nav.familie.ks.sak.app.behandling.BehandlingslagerService;
import no.nav.familie.ks.sak.app.behandling.ResultatService;
import no.nav.familie.ks.sak.app.behandling.Saksbehandling;
import no.nav.familie.ks.sak.app.behandling.VurderSamletTjeneste;
import no.nav.familie.ks.sak.app.behandling.domene.Behandling;
import no.nav.familie.ks.sak.app.behandling.domene.Fagsak;
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.UtfallType;
import no.nav.familie.ks.sak.app.behandling.domene.typer.AktørId;
import no.nav.familie.ks.sak.app.behandling.fastsetting.FastsettingService;
import no.nav.familie.ks.sak.app.behandling.regel.vilkår.barn.BarneVilkår;
import no.nav.familie.ks.sak.app.behandling.regel.vilkår.medlemskap.MedlemskapsVilkår;
import no.nav.familie.ks.sak.app.behandling.resultat.Vedtak;
import no.nav.familie.ks.sak.app.integrasjon.RegisterInnhentingService;
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
    private final RegisterInnhentingService registerInnhentingServiceMock = mock(RegisterInnhentingService.class);
    private final Saksbehandling saksbehandling = new Saksbehandling(vurderSamletTjeneste, behandlingslagerMock, registerInnhentingServiceMock, fastsettingServiceMock, mock(ResultatService.class));

    @Test
    public void positivt_vedtak_ved_oppfylte_vilkår() {
        when(fastsettingServiceMock.fastsettFakta(any(), any(), any())).thenReturn(FaktagrunnlagTestBuilder.familieNorskStatsborgerskapUtenBarnehage());
        when(behandlingslagerMock.nyBehandling(any())).thenReturn(Behandling.forFørstegangssøknad(new Fagsak(new AktørId(0L), "")).build());

        Søknad innsendtSøknad = SøknadTestdata.norskFamilieUtenBarnehageplass();
        Vedtak vedtak = saksbehandling.behandle(innsendtSøknad);
        assertThat(vedtak.getVilkårvurdering().getSamletUtfallType()).isEqualTo(UtfallType.OPPFYLT);
    }

    @Test
    public void negativt_vedtak_ved_ikke_oppfylte_vilkår() {
        when(fastsettingServiceMock.fastsettFakta(any(), any(), any())).thenReturn(FaktagrunnlagTestBuilder.familieUtenlandskStatsborgerskapMedBarnehage());
        when(behandlingslagerMock.nyBehandling(any())).thenReturn(Behandling.forFørstegangssøknad(new Fagsak(new AktørId(0L), "")).build());

        Søknad innsendtSøknad = SøknadTestdata.norskFamilieGradertBarnehageplass();
        Vedtak vedtak = saksbehandling.behandle(innsendtSøknad);
        assertThat(vedtak.getVilkårvurdering().getSamletUtfallType()).isEqualTo(UtfallType.MANUELL_BEHANDLING);
    }
}
