package no.nav.familie.ks.sak.app.behandling;

import no.nav.familie.ks.sak.FaktagrunnlagBuilder;
import no.nav.familie.ks.sak.Saksbehandling;
import no.nav.familie.ks.sak.app.behandling.domene.Behandling;
import no.nav.familie.ks.sak.app.behandling.domene.Fagsak;
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.UtfallType;
import no.nav.familie.ks.sak.app.behandling.domene.typer.AktørId;
import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.behandling.resultat.Vedtak;
import no.nav.familie.ks.sak.app.behandling.vilkår.barn.BarneVilkår;
import no.nav.familie.ks.sak.app.behandling.vilkår.barnehage.BarnehageVilkår;
import no.nav.familie.ks.sak.app.behandling.vilkår.bosted.BostedVilkår;
import no.nav.familie.ks.sak.app.behandling.vilkår.medlemskap.MedlemskapsVilkår;
import no.nav.familie.ks.sak.app.integrasjon.OppslagTjeneste;
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

    private final OppslagTjeneste oppslagMock = mock(OppslagTjeneste.class);
    private final BehandlingslagerService behandlingslagerMock = mock(BehandlingslagerService.class);

    private final VurderSamletTjeneste vurderSamletTjeneste = new VurderSamletTjeneste(List.of(new BarneVilkår(), new MedlemskapsVilkår(), new BarnehageVilkår(), new BostedVilkår()));

    private final Saksbehandling saksbehandling = new Saksbehandling(oppslagMock, vurderSamletTjeneste, behandlingslagerMock, mock(RegisterInnhentingService.class) , mock(ResultatService.class), new JacksonJsonConfig().objectMapper());

    @Test
    public void søknad_med_barnehage_gir_feil() {
        when(oppslagMock.hentTpsFakta(any(), any(), any())).thenReturn(FaktagrunnlagBuilder.faktaBeggeForeldreOgBarnNorskStatsborger());
        when(behandlingslagerMock.trekkUtOgPersister(any())).thenReturn(Behandling.forFørstegangssøknad(new Fagsak(new AktørId(0L), "")).build());
        Vedtak vedtak = saksbehandling.behandle(getFile("soknadGradertBarnehageplass.json"));
        assertThat(vedtak.getVilkårvurdering().getSamletUtfallType()).isEqualTo(UtfallType.IKKE_OPPFYLT);
    }

    @Test
    public void at_søknad_uten_barnehage_gir_stønadperiode() {
        when(oppslagMock.hentTpsFakta(any(), any(), any())).thenReturn(FaktagrunnlagBuilder.faktaBeggeForeldreOgBarnNorskStatsborger());
        when(behandlingslagerMock.trekkUtOgPersister(any())).thenReturn(Behandling.forFørstegangssøknad(new Fagsak(new AktørId(0L), "")).build());
        Vedtak vedtak = saksbehandling.behandle(getFile("soknadUtenBarnehageplass.json"));
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

    private String getFile(String filnavn) {
        return getClass().getClassLoader().getResource(filnavn).getFile();
    }
}
