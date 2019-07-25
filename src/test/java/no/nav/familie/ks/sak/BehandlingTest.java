package no.nav.familie.ks.sak;

import no.nav.familie.ks.sak.app.behandling.resultat.UtfallType;
import no.nav.familie.ks.sak.app.behandling.resultat.Vedtak;
import no.nav.familie.ks.sak.app.grunnlag.Oppslag;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BehandlingTest {

    private final Oppslag oppslagMock = mock(Oppslag.class);
    private final Saksbehandling saksbehandling = new Saksbehandling();
    private final FaktagrunnlagBuilder faktagrunnlagBuilder = new FaktagrunnlagBuilder();

    @Before
    public void setUp() {
        saksbehandling.setOppslag(oppslagMock);
    }

    @Test
    public void positivt_vedtak_ved_oppfylte_vilkår() {
        when(oppslagMock.hentTpsFakta(any())).thenReturn(faktagrunnlagBuilder.tpsFaktaGyldig);
        Vedtak vedtak = saksbehandling.behandle(getFile("soknadUtenBarnehageplass.json"));

        assertThat(vedtak.getVilkårvurdering().getUtfallType()).isEqualTo(UtfallType.OPPFYLT);
    }

    @Test
    public void negativt_vedtak_ved_ikke_oppfylte_vilkår() {
        when(oppslagMock.hentTpsFakta(any())).thenReturn(faktagrunnlagBuilder.tpsFaktaUgyldig);
        Vedtak vedtak = saksbehandling.behandle(getFile("soknadMedBarnehageplass.json"));

        assertThat(vedtak.getVilkårvurdering().getUtfallType()).isEqualTo(UtfallType.IKKE_OPPFYLT);
    }

    private String getFile(String filnavn) {
        return getClass().getClassLoader().getResource(filnavn).getFile();
    }
}
