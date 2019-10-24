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
import no.nav.familie.ks.sak.app.behandling.regel.vilkår.medlemskapBosted.MedlemskapBostedVilkår;
import no.nav.familie.ks.sak.app.behandling.resultat.Vedtak;
import no.nav.familie.ks.sak.app.integrasjon.OppslagTjeneste;
import no.nav.familie.ks.sak.app.integrasjon.RegisterInnhentingService;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.PersonIdent;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.FDATException;
import no.nav.familie.ks.sak.config.toggle.UnleashProvider;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SaksbehandlingTest {
    private static final String SAKSNUMMER = "TEST123";
    private static final String JOURNALPOSTID = "1234567";

    private final BehandlingslagerService behandlingslagerMock = mock(BehandlingslagerService.class);
    private final VurderSamletTjeneste vurderSamletTjeneste = new VurderSamletTjeneste(List.of(new BarneVilkår(), new MedlemskapBostedVilkår()));
    private final FastsettingService fastsettingServiceMock = mock(FastsettingService.class);
    private final RegisterInnhentingService registerInnhentingServiceMock = mock(RegisterInnhentingService.class);
    private final UnleashProvider unleashProviderMock = mock(UnleashProvider.class);
    private final UnleashProvider.Toggle toggleMock = mock(UnleashProvider.Toggle.class);
    private final Saksbehandling saksbehandling = new Saksbehandling(vurderSamletTjeneste, behandlingslagerMock, registerInnhentingServiceMock, fastsettingServiceMock, mock(OppslagTjeneste.class), unleashProviderMock, mock(ResultatService.class));

    @Before
    public void setUp() {
        when(unleashProviderMock.toggle(any())).thenReturn(toggleMock);
        when(toggleMock.isEnabled()).thenReturn(false);
    }

    @Test
    public void positivt_vedtak_ved_oppfylte_vilkår() {
        when(fastsettingServiceMock.fastsettFakta(any(), any(), any(), any())).thenReturn(FaktagrunnlagTestBuilder.familieNorskStatsborgerskapUtenBarnehage());
        when(behandlingslagerMock.nyBehandling(any(), any(), any())).thenReturn(Behandling.forFørstegangssøknad(new Fagsak(new AktørId(0L), new PersonIdent(""),""), "").build());

        Søknad innsendtSøknad = SøknadTestdata.norskFamilieUtenBarnehageplass();
        Vedtak vedtak = saksbehandling.behandle(innsendtSøknad, SAKSNUMMER, JOURNALPOSTID);
        assertThat(vedtak.getVilkårvurdering().getSamletUtfallType()).isEqualTo(UtfallType.OPPFYLT);
    }

    @Test
    public void negativt_vedtak_ved_ikke_oppfylte_vilkår() {
        when(fastsettingServiceMock.fastsettFakta(any(), any(), any(), any())).thenReturn(FaktagrunnlagTestBuilder.familieUtenlandskStatsborgerskapMedBarnehage());
        when(behandlingslagerMock.nyBehandling(any(), any(), any())).thenReturn(Behandling.forFørstegangssøknad(new Fagsak(new AktørId(0L), new PersonIdent(""), ""), "").build());

        Søknad innsendtSøknad = SøknadTestdata.norskFamilieGradertBarnehageplass();
        Vedtak vedtak = saksbehandling.behandle(innsendtSøknad, SAKSNUMMER, JOURNALPOSTID);
        assertThat(vedtak.getVilkårvurdering().getSamletUtfallType()).isEqualTo(UtfallType.MANUELL_BEHANDLING);
    }

    @Test
    public void negativt_vedtak_ved_person_ikke_funnet_avvik() throws FDATException {
        when(fastsettingServiceMock.fastsettFakta(any(), any(), any(), any())).thenReturn(FaktagrunnlagTestBuilder.familieUtenlandskStatsborgerskapMedBarnehage());
        when(behandlingslagerMock.nyBehandling(any(), any(), any())).thenReturn(Behandling.forFørstegangssøknad(new Fagsak(new AktørId(0L), new PersonIdent("123"), ""), "").build());
        when(registerInnhentingServiceMock.innhentPersonopplysninger(any(), any())).thenThrow(new FDATException());

        Søknad innsendtSøknad = SøknadTestdata.utenlandskFamilieMedBarnehageplass();
        Vedtak vedtak = saksbehandling.behandle(innsendtSøknad, SAKSNUMMER, JOURNALPOSTID);

        assertThat(vedtak.getVilkårvurdering().getSamletUtfallType()).isEqualByComparingTo(UtfallType.MANUELL_BEHANDLING);
    }
}
