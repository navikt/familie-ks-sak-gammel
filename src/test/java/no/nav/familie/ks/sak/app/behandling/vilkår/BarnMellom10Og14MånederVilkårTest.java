package no.nav.familie.ks.sak.app.behandling.vilkår;

import java.time.LocalDate;

import no.nav.familie.ks.sak.FaktagrunnlagBuilder;
import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.behandling.regel.mvp.BarnMellom10Og14Måneder;
import no.nav.fpsak.nare.evaluation.Resultat;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class BarnMellom10Og14MånederVilkårTest {
    Faktagrunnlag faktagrunnlag = spy(FaktagrunnlagBuilder.beggeForeldreBorINorgeOgErNorskeStatsborgere());
    LocalDate fødselsdato = faktagrunnlag.getTpsFakta().getBarna().get(0).getPersoninfo().getFødselsdato();
    BarnMellom10Og14Måneder vilkår = new BarnMellom10Og14Måneder();

    @Test
    public void ikke_oppfylt_hvis_barn_er_under_10_måneder() {
        when(faktagrunnlag.getBehandlingstidspunkt()).thenReturn(fødselsdato.plusMonths(10).minusDays(1));
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
    public void oppfylt_hvis_barn_er_nesten_14_måneder() {
        when(faktagrunnlag.getBehandlingstidspunkt()).thenReturn(fødselsdato.plusMonths(14).minusDays(1));
        final var evaluering = vilkår.evaluer(faktagrunnlag);
        assertThat(evaluering.result()).isEqualByComparingTo(Resultat.JA);
    }

    @Test
    public void ikke_oppfylt_hvis_barn_er_akkurat_14_måneder() {
        when(faktagrunnlag.getBehandlingstidspunkt()).thenReturn(fødselsdato.plusMonths(14));
        final var evaluering = vilkår.evaluer(faktagrunnlag);
        assertThat(evaluering.result()).isEqualByComparingTo(Resultat.NEI);
    }
}