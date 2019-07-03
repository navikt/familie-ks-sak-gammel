package no.nav.familie.ks.sak;

import no.nav.familie.ks.sak.resultat.UtfallType;
import no.nav.familie.ks.sak.grunnlag.Søknad;
import no.nav.familie.ks.sak.grunnlag.TpsFakta;
import no.nav.familie.ks.sak.resultat.Vedtak;
import no.nav.familie.ks.sak.behandling.Oppslag;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BehandlingTest {

    private static final LocalDate FØDSELSDATO_BARN_GYLDIG = LocalDate.now().minusMonths(13);
    private static final String STATSBORGERSKAP_GYLDIG = "NOR";
    private static final Boolean IKKE_UTLAND_TRE_MÅNEDER_GYLDIG = true;
    private static final Boolean IKKE_UTLAND_TRE_MÅNEDER_UGYLDIG = false;

    private final Oppslag oppslagMock = mock(Oppslag.class);
    private final Saksbehandling saksbehandling = new Saksbehandling();

    @Before
    public void setUp() {
        saksbehandling.setOppslag(oppslagMock);
    }

    @Test
    public void positivt_vedtak_ved_oppfylte_vilkår() {
        when(oppslagMock.hentTpsFakta(any())).thenReturn(tpsFakta());
        Vedtak vedtak = saksbehandling.behandle(søknadGyldig());

        assertThat(vedtak.getVilkårvurdering().getUtfallType()).isEqualTo(UtfallType.OPPFYLT);
    }

    @Test
    public void negativt_vedtak_ved_ikke_oppfylte_vilkår() {
        when(oppslagMock.hentTpsFakta(any())).thenReturn(tpsFakta());
        Vedtak vedtak = saksbehandling.behandle(søknadUgyldig());

        assertThat(vedtak.getVilkårvurdering().getUtfallType()).isEqualTo(UtfallType.IKKE_OPPFYLT);
    }
/*
    @Test
    public void manuelt_vedtak_ved_ikke_oppfylte_vilkår() {
        when(oppslagMock.hentTpsFakta(any())).thenReturn(tpsFakta());
        Vedtak vedtak = saksbehandling.behandle(u);

        assertThat(vedtak.getVilkårvurdering().getUtfallType()).isEqualTo(UtfallType.UAVKLART);
    }
*/
    private Søknad søknadGyldig() {
        return new Søknad.Builder()
                .medIkkeUtlandTreMåneder(IKKE_UTLAND_TRE_MÅNEDER_GYLDIG)
                .build();
    }

    private Søknad søknadUgyldig() {
        return new Søknad.Builder()
                .medIkkeUtlandTreMåneder(IKKE_UTLAND_TRE_MÅNEDER_UGYLDIG)
                .build();
    }

    private TpsFakta tpsFakta() {
        return new TpsFakta.Builder()
                .medStatsborgerskap(STATSBORGERSKAP_GYLDIG)
                .medBarnetsFødselsdato(FØDSELSDATO_BARN_GYLDIG)
                .build();
    }
}
