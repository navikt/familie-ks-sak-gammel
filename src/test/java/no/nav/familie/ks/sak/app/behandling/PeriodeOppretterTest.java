package no.nav.familie.ks.sak.app.behandling;

import no.nav.familie.ks.kontrakter.søknad.testdata.SøknadTestdata;
import no.nav.familie.ks.sak.FaktagrunnlagBuilder;
import no.nav.familie.ks.sak.app.behandling.domene.Behandling;
import no.nav.familie.ks.sak.app.behandling.domene.Fagsak;
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.UtfallType;
import no.nav.familie.ks.sak.app.behandling.domene.typer.AktørId;
import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.behandling.fastsetting.FastsettingService;
import no.nav.familie.ks.sak.app.behandling.regel.vilkår.barn.BarneVilkår;
import no.nav.familie.ks.sak.app.behandling.regel.vilkår.barnehage.BarnehageVilkår;
import no.nav.familie.ks.sak.app.behandling.regel.vilkår.bosted.BostedVilkår;
import no.nav.familie.ks.sak.app.behandling.regel.vilkår.medlemskap.MedlemskapsVilkår;
import no.nav.familie.ks.sak.app.behandling.resultat.Vedtak;
import no.nav.familie.ks.sak.app.integrasjon.RegisterInnhentingService;
import no.nav.familie.ks.sak.config.JacksonJsonConfig;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PeriodeOppretterTest {
    private static final int MIN_ALDER_I_MÅNEDER = 13;
    private static final int MAKS_ALDER_I_MÅNEDER = 23;
    private static final int MAKS_UTBETALINGSGRAD = 100;

    private final BehandlingslagerService behandlingslagerMock = mock(BehandlingslagerService.class);
    private final RegisterInnhentingService registerInnhentingServiceMock = mock(RegisterInnhentingService.class);
    private final VurderSamletTjeneste vurderSamletTjeneste = new VurderSamletTjeneste(List.of(new BarneVilkår(), new MedlemskapsVilkår(), new BarnehageVilkår(), new BostedVilkår()));
    private final FastsettingService fastsettingServiceMock = mock(FastsettingService.class);
    private final Saksbehandling saksbehandling = new Saksbehandling(vurderSamletTjeneste, behandlingslagerMock, registerInnhentingServiceMock, fastsettingServiceMock, mock(ResultatService.class), new JacksonJsonConfig().objectMapper());

    @Test
    public void søknad_med_barnehage_gir_feil() {
        when(fastsettingServiceMock.fastsettFakta(any(), any())).thenReturn(FaktagrunnlagBuilder.familieNorskStatsborgerskapMedGradertBarnehage());
        when(behandlingslagerMock.nyBehandling(any())).thenReturn(Behandling.forFørstegangssøknad(new Fagsak(new AktørId(0L), "")).build());
        Vedtak vedtak = saksbehandling.behandle(SøknadTestdata.norskFamilieGradertBarnehageplass());
        assertThat(vedtak.getVilkårvurdering().getSamletUtfallType()).isEqualTo(UtfallType.MANUELL_BEHANDLING);
    }

    @Test
    public void at_søknad_uten_barnehage_gir_stønadperiode() {
        when(fastsettingServiceMock.fastsettFakta(any(), any())).thenReturn(FaktagrunnlagBuilder.familieNorskStatsborgerskapUtenBarnehage());
        when(behandlingslagerMock.nyBehandling(any())).thenReturn(Behandling.forFørstegangssøknad(new Fagsak(new AktørId(0L), "")).build());
        Vedtak vedtak = saksbehandling.behandle(SøknadTestdata.norskFamilieUtenBarnehageplass());
        assertThat(vedtak.getVilkårvurdering().getSamletUtfallType()).isEqualTo(UtfallType.OPPFYLT);
        assertThat(vedtak.getStønadperiode().getFom()).isEqualTo(FaktagrunnlagBuilder.faktaBeggeForeldreOgBarnNorskStatsborger().getBarn().getPersoninfo().getFødselsdato().plusMonths(MIN_ALDER_I_MÅNEDER).withDayOfMonth(1));
        assertThat(vedtak.getStønadperiode().getTom()).isEqualTo(FaktagrunnlagBuilder.faktaBeggeForeldreOgBarnNorskStatsborger().getBarn().getPersoninfo().getFødselsdato().plusMonths(MAKS_ALDER_I_MÅNEDER).withDayOfMonth(1));
        assertThat(vedtak.getStønadperiode().getProsent()).isEqualTo(100);
    }

    @Test
    public void periode_opprettes_korrekt_nå() {
        PeriodeOppretter periodeOppretter = new PeriodeOppretter();
        Faktagrunnlag faktagrunnlag = FaktagrunnlagBuilder.familieNorskStatsborgerskapUtenBarnehage();
        LocalDate fødselsdatoBarn = faktagrunnlag.getTpsFakta().getBarn().getPersoninfo().getFødselsdato();
        GradertPeriode stønadPeriode = periodeOppretter.opprettStønadPeriode(faktagrunnlag);
        assertThat(stønadPeriode.getFom()).isEqualTo(LocalDate.now().withDayOfMonth(1));
        assertThat(stønadPeriode.getTom()).isEqualTo(fødselsdatoBarn.plusMonths(MAKS_ALDER_I_MÅNEDER).withDayOfMonth(1));
        assertThat(stønadPeriode.getProsent()).isEqualTo(MAKS_UTBETALINGSGRAD);
    }

    @Test
    public void periode_opprettes_korrekt_fremtidig() {
        PeriodeOppretter periodeOppretter = new PeriodeOppretter();
        Faktagrunnlag faktagrunnlag = FaktagrunnlagBuilder.familieUtenlandskStatsborgerskapMedBarnehage();
        LocalDate fødselsdatoBarn = faktagrunnlag.getTpsFakta().getBarn().getPersoninfo().getFødselsdato();
        GradertPeriode stønadPeriode = periodeOppretter.opprettStønadPeriode(faktagrunnlag);

        assertThat(stønadPeriode.getFom()).isEqualTo(fødselsdatoBarn.plusMonths(MIN_ALDER_I_MÅNEDER).withDayOfMonth(1));
        assertThat(stønadPeriode.getTom()).isEqualTo(fødselsdatoBarn.plusMonths(MAKS_ALDER_I_MÅNEDER).withDayOfMonth(1));
        assertThat(stønadPeriode.getProsent()).isEqualTo(MAKS_UTBETALINGSGRAD);
    }
}
