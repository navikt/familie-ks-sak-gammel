package no.nav.familie.ks.sak;


import no.nav.familie.ks.sak.app.behandling.Regelresultat;
import no.nav.familie.ks.sak.app.behandling.VilkårRegel;
import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.behandling.regler.*;
import no.nav.familie.ks.sak.app.behandling.resultat.UtfallType;
import no.nav.familie.ks.sak.app.grunnlag.Søknad;
import no.nav.familie.ks.sak.app.grunnlag.TpsFakta;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.evaluation.Resultat;
import org.junit.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class RegelTest {

    private static final LocalDate FØDSELSDATO_BARN_GYLDIG = LocalDate.now().minusMonths(13);
    private static final LocalDate FØDSELSDATO_BARN_UGYLDIG = LocalDate.now().minusMonths(9);
    private static final String STATSBORGERSKAP_GYLDIG = "NOR";
    private static final String STATSBORGERSKAP_UGYLDIG = "NZ";
    private static final Boolean IKKE_UTLAND_TRE_MÅNEDER_GYLDIG = true;
    private static final Boolean IKKE_UTLAND_TRE_MÅNEDER_UGYLDIG = false;

    private final VilkårRegel vilkårRegel = new VilkårRegel();


    @Test
    public void at_sjekkBarnetsAlder_returnerer_korrekt() {
        SjekkBarnetsAlder sjekkBarnetsAlder = new SjekkBarnetsAlder();

        Faktagrunnlag faktagrunnlag = medGyldigSøknadOgTpsFakta();
        Evaluation evaluation = sjekkBarnetsAlder.evaluate(faktagrunnlag);
        assertThat(evaluation.result()).isEqualByComparingTo(Resultat.NEI);

        faktagrunnlag = medUgyldigSøknadOgTpsFakta();
        evaluation = sjekkBarnetsAlder.evaluate(faktagrunnlag);
        assertThat(evaluation.result()).isEqualByComparingTo(Resultat.JA);
    }

    @Test
    public void at_sjekkBarnSammeAdresse_returnerer_korrekt() {
        SjekkBarnSammeAdresse sjekkBarnSammeAdresse = new SjekkBarnSammeAdresse();

        Faktagrunnlag faktagrunnlag = medGyldigSøknadOgTpsFakta();
        Evaluation evaluation = sjekkBarnSammeAdresse.evaluate(faktagrunnlag);
        assertThat(evaluation.result()).isEqualByComparingTo(Resultat.NEI);

        faktagrunnlag = medUgyldigSøknadOgTpsFakta();
        evaluation = sjekkBarnSammeAdresse.evaluate(faktagrunnlag);
        assertThat(evaluation.result()).isEqualByComparingTo(Resultat.JA);
    }

    @Test
    public void at_sjekkDeltBosted_returnerer_korrekt() {
        SjekkDeltBosted sjekkDeltBosted = new SjekkDeltBosted();

        Faktagrunnlag faktagrunnlag = medGyldigSøknadOgTpsFakta();
        Evaluation evaluation = sjekkDeltBosted.evaluate(faktagrunnlag);
        assertThat(evaluation.result()).isEqualByComparingTo(Resultat.NEI);

        faktagrunnlag = medUgyldigSøknadOgTpsFakta();
        evaluation = sjekkDeltBosted.evaluate(faktagrunnlag);
        assertThat(evaluation.result()).isEqualByComparingTo(Resultat.JA);
    }

    @Test
    public void at_sjekkMedlemsskap_returnerer_korrekt() {
        SjekkMedlemsskap sjekkMedlemsskap = new SjekkMedlemsskap();

        Faktagrunnlag faktagrunnlag = medGyldigSøknadOgTpsFakta();
        Evaluation evaluation = sjekkMedlemsskap.evaluate(faktagrunnlag);
        assertThat(evaluation.result()).isEqualByComparingTo(Resultat.NEI);

        faktagrunnlag = medUgyldigSøknadOgTpsFakta();
        evaluation = sjekkMedlemsskap.evaluate(faktagrunnlag);
        assertThat(evaluation.result()).isEqualByComparingTo(Resultat.JA);
    }

    @Test
    public void at_sjekkTreÅrUtland_returnerer_korrekt() {
        SjekkTreÅrUtland sjekkTreÅrUtland = new SjekkTreÅrUtland();

        Faktagrunnlag faktagrunnlag = medGyldigSøknadOgTpsFakta();
        Evaluation evaluation = sjekkTreÅrUtland.evaluate(faktagrunnlag);
        assertThat(evaluation.result()).isEqualByComparingTo(Resultat.NEI);

        faktagrunnlag = medUgyldigSøknadOgTpsFakta();
        evaluation = sjekkTreÅrUtland.evaluate(faktagrunnlag);
        assertThat(evaluation.result()).isEqualByComparingTo(Resultat.JA);
    }

    @Test
    public void at_vilkår_regel_blir_oppfylt() {
        Faktagrunnlag faktagrunnlag = medGyldigSøknadOgTpsFakta();
        Evaluation evaluation = vilkårRegel.evaluer(faktagrunnlag);
        Regelresultat regelresultat = new Regelresultat(evaluation);
        assertThat(regelresultat.getUtfallType()).isEqualTo(UtfallType.OPPFYLT);
    }


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

    private TpsFakta tpsFaktaGyldig() {
        return new TpsFakta.Builder()
                .medStatsborgerskap(STATSBORGERSKAP_GYLDIG)
                .medBarnetsFødselsdato(FØDSELSDATO_BARN_GYLDIG)
                .build();
    }

    private TpsFakta tpsFaktaUgyldig() {
        return new TpsFakta.Builder()
                .medStatsborgerskap(STATSBORGERSKAP_UGYLDIG)
                .medBarnetsFødselsdato(FØDSELSDATO_BARN_UGYLDIG)
                .build();
    }

    private Faktagrunnlag medUgyldigSøknadOgTpsFakta() {
        return new Faktagrunnlag.Builder()
                .medTpsFakta(tpsFaktaUgyldig())
                .medSøknad(søknadUgyldig())
                .build();
    }

    private Faktagrunnlag medGyldigSøknadOgTpsFakta() {
        return new Faktagrunnlag.Builder()
                .medTpsFakta(tpsFaktaGyldig())
                .medSøknad(søknadGyldig())
                .build();
    }

}
