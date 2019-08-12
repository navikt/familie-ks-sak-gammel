package no.nav.familie.ks.sak.app.behandling;

import no.nav.familie.ks.sak.FaktagrunnlagBuilder;
import no.nav.familie.ks.sak.app.behandling.Regelresultat;
import no.nav.familie.ks.sak.app.behandling.AutomatiskBehandleKontantstøtteRegel;
import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.behandling.regler.*;
import no.nav.familie.ks.sak.app.behandling.resultat.UtfallType;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.evaluation.Resultat;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AutomatiskBehandleKontantstøtteRegelTest {

    private final no.nav.familie.ks.sak.app.behandling.AutomatiskBehandleKontantstøtteRegel vilkårRegel = new no.nav.familie.ks.sak.app.behandling.AutomatiskBehandleKontantstøtteRegel();

    @Test
    public void at_sjekkMedlemsskap_returnerer_korrekt() {
        SjekkMedlemsskap sjekkMedlemsskap = new SjekkMedlemsskap();

        Faktagrunnlag faktagrunnlag = FaktagrunnlagBuilder.beggeForeldreNorskStatsborgerOgBarnHarGyldigAlder();
        Evaluation evaluation = sjekkMedlemsskap.evaluate(faktagrunnlag);
        assertThat(evaluation.result()).isEqualByComparingTo(Resultat.JA);

        faktagrunnlag = FaktagrunnlagBuilder.beggeForeldreUtenlandskeStatsborgereOgBarnForGammel();
        evaluation = sjekkMedlemsskap.evaluate(faktagrunnlag);
        assertThat(evaluation.result()).isEqualByComparingTo(Resultat.NEI);
    }

    @Test
    public void at_sjekkBarnehage_returnerer_korrekt() {
        SjekkBarnehage sjekkBarnehage = new SjekkBarnehage();

        Faktagrunnlag faktagrunnlag = FaktagrunnlagBuilder.beggeForeldreNorskStatsborgerOgBarnHarGyldigAlder();
        Evaluation evaluation = sjekkBarnehage.evaluate(faktagrunnlag);
        assertThat(evaluation.result()).isEqualByComparingTo(Resultat.JA);

        faktagrunnlag = FaktagrunnlagBuilder.beggeForeldreUtenlandskeStatsborgereOgBarnForGammel();
        evaluation = sjekkBarnehage.evaluate(faktagrunnlag);
        assertThat(evaluation.result()).isEqualByComparingTo(Resultat.NEI);
    }

    @Test
    public void at_vilkår_regel_blir_oppfylt() {
        Faktagrunnlag faktagrunnlag = FaktagrunnlagBuilder.beggeForeldreNorskStatsborgerOgBarnHarGyldigAlder();
        Evaluation evaluation = vilkårRegel.evaluer(faktagrunnlag);
        Regelresultat regelresultat = new Regelresultat(evaluation);
        assertThat(regelresultat.getUtfallType()).isEqualTo(UtfallType.OPPFYLT);
    }
}
