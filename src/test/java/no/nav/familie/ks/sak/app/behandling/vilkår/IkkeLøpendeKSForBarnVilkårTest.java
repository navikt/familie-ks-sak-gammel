package no.nav.familie.ks.sak.app.behandling.vilkår;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import org.junit.Test;

import no.nav.familie.ks.sak.FaktagrunnlagTestBuilder;
import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.behandling.regel.mvp.ikkelopendeksforbarn.IkkeLøpendeKSForBarnVilkår;
import no.nav.familie.ks.sak.app.integrasjon.infotrygd.domene.AktivKontantstøtteInfo;
import no.nav.familie.ks.sak.app.integrasjon.infotrygd.domene.InfotrygdFakta;
import no.nav.fpsak.nare.evaluation.Resultat;

public class IkkeLøpendeKSForBarnVilkårTest {
    Faktagrunnlag faktagrunnlag = spy(FaktagrunnlagTestBuilder.beggeForeldreBorINorgeOgErNorskeStatsborgere());
    IkkeLøpendeKSForBarnVilkår vilkår = new IkkeLøpendeKSForBarnVilkår();

    @Test
    public void ikke_oppfylt_hvis_barnet_mottar_kontantstøtte_eller_er_under_behandling() {
        when(faktagrunnlag.getInfotrygdFakta()).thenReturn(new InfotrygdFakta(new AktivKontantstøtteInfo(true)));
        final var evaluering = vilkår.evaluer(faktagrunnlag);
        assertThat(evaluering.result()).isEqualByComparingTo(Resultat.NEI);
    }

    @Test
    public void oppfylt_hvis_barnet_mottar_kontantstøtte_eller_er_under_behandling() {
        when(faktagrunnlag.getInfotrygdFakta()).thenReturn(new InfotrygdFakta(new AktivKontantstøtteInfo(false)));
        final var evaluering = vilkår.evaluer(faktagrunnlag);
        assertThat(evaluering.result()).isEqualByComparingTo(Resultat.JA);
    }
}
