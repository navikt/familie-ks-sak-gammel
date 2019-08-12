package no.nav.familie.ks.sak.app.behandling;

import no.nav.familie.ks.sak.FaktagrunnlagBuilder;
import no.nav.familie.ks.sak.Saksbehandling;
import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.behandling.resultat.UtfallType;
import no.nav.familie.ks.sak.app.behandling.resultat.Vedtak;
import no.nav.familie.ks.sak.app.grunnlag.Oppslag;
import no.nav.familie.ks.sak.config.JacksonJsonConfig;
import org.junit.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PeriodeOppretterTest {

    private static final int MIN_ALDER_I_MÅNEDER = 13;
    private static final int MAKS_ALDER_I_MÅNEDER = 23;
    private static final int MAKS_UTBETALINGSGRAD = 100;
    private static final String PERSONIDENT = "123";

    private final Oppslag oppslagMock = mock(Oppslag.class);
    private final Saksbehandling saksbehandling = new Saksbehandling(oppslagMock, new JacksonJsonConfig().objectMapper());

    @Test
    public void at_søknad_med_gradert_barnehage_gir_feil() {
        when(oppslagMock.hentTpsFakta(any(), any())).thenReturn(FaktagrunnlagBuilder.faktaBeggeForeldreNorskStatsborgerOgBarnHarGyldigAlder());
        Vedtak vedtak = saksbehandling.behandle(getFile("soknadGradertBarnehageplass.json"), PERSONIDENT);
        assertThat(vedtak.getVilkårvurdering().getUtfallType()).isEqualTo(UtfallType.IKKE_OPPFYLT);
    }

    @Test
    public void at_søknad_uten_barnehage_gir_stønadperiode() {
        when(oppslagMock.hentTpsFakta(any(), any())).thenReturn(FaktagrunnlagBuilder.faktaBeggeForeldreNorskStatsborgerOgBarnHarGyldigAlder());
        Vedtak vedtak = saksbehandling.behandle(getFile("soknadUtenBarnehageplass.json"), PERSONIDENT);
        assertThat(vedtak.getVilkårvurdering().getUtfallType()).isEqualTo(UtfallType.OPPFYLT);
        assertThat(vedtak.getStønadperiode().getFom()).isEqualTo(FaktagrunnlagBuilder.faktaBeggeForeldreNorskStatsborgerOgBarnHarGyldigAlder().getBarn().getFødselsdato().plusMonths(MIN_ALDER_I_MÅNEDER).withDayOfMonth(1));
        assertThat(vedtak.getStønadperiode().getTom()).isEqualTo(FaktagrunnlagBuilder.faktaBeggeForeldreNorskStatsborgerOgBarnHarGyldigAlder().getBarn().getFødselsdato().plusMonths(MAKS_ALDER_I_MÅNEDER).withDayOfMonth(1));
        assertThat(vedtak.getStønadperiode().getProsent()).isEqualTo(100);
    }

    @Test
    public void at_søknad_med_full_barnehage_gir_stønadperiode() {
        when(oppslagMock.hentTpsFakta(any(), any())).thenReturn(FaktagrunnlagBuilder.faktaBeggeForeldreNorskStatsborgerOgBarnHarGyldigAlder());
        Vedtak vedtak = saksbehandling.behandle(getFile("soknadFullBarnehageplass.json"), PERSONIDENT);
        assertThat(vedtak.getVilkårvurdering().getUtfallType()).isEqualTo(UtfallType.OPPFYLT);
        assertThat(vedtak.getStønadperiode().getFom()).isEqualTo(FaktagrunnlagBuilder.faktaBeggeForeldreNorskStatsborgerOgBarnHarGyldigAlder().getBarn().getFødselsdato().plusMonths(MIN_ALDER_I_MÅNEDER).withDayOfMonth(1));
        assertThat(vedtak.getStønadperiode().getTom()).isEqualTo(FaktagrunnlagBuilder.faktaBeggeForeldreNorskStatsborgerOgBarnHarGyldigAlder().getBarn().getFødselsdato().plusMonths(MAKS_ALDER_I_MÅNEDER).withDayOfMonth(1));
        assertThat(vedtak.getStønadperiode().getProsent()).isEqualTo(100);
    }

    @Test
    public void at_periode_opprettes_korrekt_nå() {
        PeriodeOppretter periodeOppretter = new PeriodeOppretter();
        Faktagrunnlag faktagrunnlag = FaktagrunnlagBuilder.beggeForeldreNorskStatsborgerOgBarnHarGyldigAlder();
        LocalDate fødselsdatoBarn = faktagrunnlag.getTpsFakta().getBarn().getFødselsdato();
        GradertPeriode stønadPeriode = periodeOppretter.opprettStønadPeriode(faktagrunnlag);
        assertThat(stønadPeriode.getFom()).isEqualTo(LocalDate.now().withDayOfMonth(1));
        assertThat(stønadPeriode.getTom()).isEqualTo(fødselsdatoBarn.plusMonths(MAKS_ALDER_I_MÅNEDER).withDayOfMonth(1));
        assertThat(stønadPeriode.getProsent()).isEqualTo(MAKS_UTBETALINGSGRAD);
    }

    @Test
    public void at_periode_opprettes_korrekt_fremtidig() {
        PeriodeOppretter periodeOppretter = new PeriodeOppretter();
        Faktagrunnlag faktagrunnlag = FaktagrunnlagBuilder.beggeForeldreUtenlandskeStatsborgereOgBarnForGammel();
        LocalDate fødselsdatoBarn = faktagrunnlag.getTpsFakta().getBarn().getFødselsdato();
        GradertPeriode stønadPeriode = periodeOppretter.opprettStønadPeriode(faktagrunnlag);

        assertThat(stønadPeriode.getFom()).isEqualTo(fødselsdatoBarn.plusMonths(MIN_ALDER_I_MÅNEDER).withDayOfMonth(1));
        assertThat(stønadPeriode.getTom()).isEqualTo(fødselsdatoBarn.plusMonths(MAKS_ALDER_I_MÅNEDER).withDayOfMonth(1));
        assertThat(stønadPeriode.getProsent()).isEqualTo(MAKS_UTBETALINGSGRAD);
    }

    private String getFile(String filnavn) {
        return getClass().getClassLoader().getResource(filnavn).getFile();
    }
}
