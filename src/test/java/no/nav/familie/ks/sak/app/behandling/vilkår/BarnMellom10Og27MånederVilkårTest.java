package no.nav.familie.ks.sak.app.behandling.vilkår;

import java.time.LocalDate;

import no.nav.familie.ks.sak.FaktagrunnlagTestBuilder;
import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.behandling.regel.mvp.barnmellom10og27mnd.BarnMellom10Og27MånederVilkår;
import no.nav.fpsak.nare.evaluation.Resultat;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class BarnMellom10Og27MånederVilkårTest {
    Faktagrunnlag faktagrunnlag = spy(FaktagrunnlagTestBuilder.beggeForeldreBorINorgeOgErNorskeStatsborgere());
    LocalDate fødselsdato = faktagrunnlag.getTpsFakta().getBarn().getPersoninfo().getFødselsdato();
    BarnMellom10Og27MånederVilkår vilkår = new BarnMellom10Og27MånederVilkår();

    @Test
    public void ikke_oppfylt_hvis_barn_er_under_10_måneder() {
        when(faktagrunnlag.getBehandlingstidspunkt()).thenReturn(fødselsdato.plusMonths(10).withDayOfMonth(1).minusDays(1));
        final var evaluering = vilkår.evaluer(faktagrunnlag);
        assertThat(evaluering.result()).isEqualByComparingTo(Resultat.NEI);
    }

    @Test
    public void oppfylt_hvis_barn_er_akkurat_10_måneder() {
        when(faktagrunnlag.getBehandlingstidspunkt()).thenReturn(fødselsdato.plusMonths(10));
        final var evaluering = vilkår.evaluer(faktagrunnlag);
        assertThat(evaluering.result()).isEqualByComparingTo(Resultat.JA);
    }

    @Test
    public void oppfylt_hvis_barn_er_nesten_27_måneder() {
        when(faktagrunnlag.getBehandlingstidspunkt()).thenReturn(fødselsdato.plusMonths(27).withDayOfMonth(1).minusDays(1));
        final var evaluering = vilkår.evaluer(faktagrunnlag);
        assertThat(evaluering.result()).isEqualByComparingTo(Resultat.JA);
    }

    @Test
    public void ikke_oppfylt_hvis_barn_er_akkurat_27_måneder() {
        when(faktagrunnlag.getBehandlingstidspunkt()).thenReturn(fødselsdato.plusMonths(27));
        final var evaluering = vilkår.evaluer(faktagrunnlag);
        assertThat(evaluering.result()).isEqualByComparingTo(Resultat.NEI);
    }
}
