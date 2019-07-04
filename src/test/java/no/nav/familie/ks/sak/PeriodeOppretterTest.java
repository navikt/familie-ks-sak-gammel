package no.nav.familie.ks.sak;

import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.behandling.GradertPeriode;
import no.nav.familie.ks.sak.app.grunnlag.Søknad;
import no.nav.familie.ks.sak.app.grunnlag.TpsFakta;
import no.nav.familie.ks.sak.app.behandling.resultat.UtfallType;
import no.nav.familie.ks.sak.app.behandling.resultat.Vedtak;
import no.nav.familie.ks.sak.app.grunnlag.Oppslag;
import no.nav.familie.ks.sak.app.behandling.PeriodeOppretter;
import org.junit.Before;
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

    private static final LocalDate BARNEHAGE_FOM = LocalDate.now().plusMonths(4);
    private static final LocalDate BARNEHAGE_TOM = LocalDate.now().plusYears(3);
    private static final int BARNEHAGE_PROSENT = 65;

    private static final LocalDate FØDSELSDATO_BARN_GYLDIG = LocalDate.now().minusMonths(13);
    private static final LocalDate FØDSELSDATO_BARN_UGYLDIG = LocalDate.now().minusMonths(9);
    private static final String STATSBORGERSKAP_GYLDIG = "NOR";
    private static final Boolean IKKE_UTLAND_TRE_MÅNEDER_GYLDIG = true;

    private final Oppslag oppslagMock = mock(Oppslag.class);
    private final Saksbehandling saksbehandling = new Saksbehandling();

    @Before
    public void setUp() {
        saksbehandling.setOppslag(oppslagMock);
    }

    @Test
    public void at_søknad_med_barnehage_gir_rett_prosent() {
        when(oppslagMock.hentTpsFakta(any())).thenReturn(tpsFaktaGyldig());
        var vedtak = saksbehandling.behandle(søknadMedBarnehage());
        assertThat(vedtak.getStønadperiode().getProsent()).isEqualTo(20);
        assertThat(vedtak.getVilkårvurdering().getUtfallType()).isEqualTo(UtfallType.OPPFYLT);
    }

    @Test
    public void at_søknad_med_barnehage_gir_rett_periode() {
        when(oppslagMock.hentTpsFakta(any())).thenReturn(tpsFaktaGyldig());
        Vedtak vedtak = saksbehandling.behandle(søknadMedBarnehage());
        assertThat(vedtak.getStønadperiode().getTom()).isEqualTo(BARNEHAGE_FOM);
    }

    @Test
    public void at_periode_opprettes_korrekt_med_barnehage_og_gyldig_alder() {
        PeriodeOppretter periodeOppretter = new PeriodeOppretter();
        Faktagrunnlag faktagrunnlag = medBarnehage();
        GradertPeriode stønadPeriode = periodeOppretter.opprettStønadPeriode(faktagrunnlag);

        assertThat(stønadPeriode.getFom()).isEqualTo(LocalDate.now());
        assertThat(stønadPeriode.getTom()).isEqualTo(BARNEHAGE_FOM);
        assertThat(stønadPeriode.getProsent()).isEqualTo(20);
    }

    @Test
    public void at_periode_opprettes_korrekt_med_barnehage_og_ugyldig_alder() {
        PeriodeOppretter periodeOppretter = new PeriodeOppretter();
        Faktagrunnlag faktagrunnlag = medBarnehageUgyldigAlder();
        GradertPeriode stønadPeriode = periodeOppretter.opprettStønadPeriode(faktagrunnlag);

        assertThat(stønadPeriode.getFom()).isEqualTo(FØDSELSDATO_BARN_UGYLDIG.plusMonths(MIN_ALDER_I_MÅNEDER));
        assertThat(stønadPeriode.getTom()).isEqualTo(BARNEHAGE_FOM);
        assertThat(stønadPeriode.getProsent()).isEqualTo(20);
    }

    @Test
    public void at_periode_opprettes_korrekt_uten_barnehage_og_gyldig_alder() {
        PeriodeOppretter periodeOppretter = new PeriodeOppretter();
        Faktagrunnlag faktagrunnlag = utenBarnehage();
        GradertPeriode stønadPeriode = periodeOppretter.opprettStønadPeriode(faktagrunnlag);

        assertThat(stønadPeriode.getFom()).isEqualTo(LocalDate.now());
        assertThat(stønadPeriode.getTom()).isEqualTo(FØDSELSDATO_BARN_GYLDIG.plusMonths(MAKS_ALDER_I_MÅNEDER));
        assertThat(stønadPeriode.getProsent()).isEqualTo(MAKS_UTBETALINGSGRAD);
    }

    @Test
    public void at_periode_opprettes_korrekt_uten_barnehage_og_ugyldig_alder() {
        PeriodeOppretter periodeOppretter = new PeriodeOppretter();
        Faktagrunnlag faktagrunnlag = utenBarnehageUgyldigAlder();
        GradertPeriode stønadPeriode = periodeOppretter.opprettStønadPeriode(faktagrunnlag);

        assertThat(stønadPeriode.getFom()).isEqualTo(FØDSELSDATO_BARN_UGYLDIG.plusMonths(MIN_ALDER_I_MÅNEDER));
        assertThat(stønadPeriode.getTom()).isEqualTo(FØDSELSDATO_BARN_UGYLDIG.plusMonths(MAKS_ALDER_I_MÅNEDER));
        assertThat(stønadPeriode.getProsent()).isEqualTo(MAKS_UTBETALINGSGRAD);
    }

    private Søknad søknadUtenBarnehage() {
        return new Søknad.Builder()
                .medIkkeUtlandTreMåneder(IKKE_UTLAND_TRE_MÅNEDER_GYLDIG)
                .build();
    }

    private Søknad søknadMedBarnehage() {
        return new Søknad.Builder()
                .medBarnehageplassProsent(BARNEHAGE_PROSENT)
                .medBarnehageplassFom(BARNEHAGE_FOM)
                .medBarnehageplassTom(BARNEHAGE_TOM)
                .medIkkeUtlandTreMåneder(IKKE_UTLAND_TRE_MÅNEDER_GYLDIG)
                .build();
    }

    private TpsFakta tpsFaktaGyldig() {
        return new TpsFakta.Builder()
                .medStatsborgerskap(STATSBORGERSKAP_GYLDIG)
                .medBarnetsFødselsdato(FØDSELSDATO_BARN_GYLDIG)
                .build();
    }

    private TpsFakta tpsFaktaUgyldigAlder() {
        return new TpsFakta.Builder()
                .medStatsborgerskap(STATSBORGERSKAP_GYLDIG)
                .medBarnetsFødselsdato(FØDSELSDATO_BARN_UGYLDIG)
                .build();
    }

    private Faktagrunnlag utenBarnehage() {
        return new Faktagrunnlag.Builder()
                .medSøknad(søknadUtenBarnehage())
                .medTpsFakta(tpsFaktaGyldig())
                .build();
    }

    private Faktagrunnlag medBarnehage() {
        return new Faktagrunnlag.Builder()
                .medSøknad(søknadMedBarnehage())
                .medTpsFakta(tpsFaktaGyldig())
                .build();
    }

    private Faktagrunnlag medBarnehageUgyldigAlder() {
        return new Faktagrunnlag.Builder()
                .medSøknad(søknadMedBarnehage())
                .medTpsFakta(tpsFaktaUgyldigAlder())
                .build();
    }

    private Faktagrunnlag utenBarnehageUgyldigAlder() {
        return new Faktagrunnlag.Builder()
                .medSøknad(søknadUtenBarnehage())
                .medTpsFakta(tpsFaktaUgyldigAlder())
                .build();
    }
}
