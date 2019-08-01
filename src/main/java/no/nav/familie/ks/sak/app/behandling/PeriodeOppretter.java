package no.nav.familie.ks.sak.app.behandling;


import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;

import java.time.LocalDate;

public class PeriodeOppretter {

    private static final int FULL_UTBETALINGSGRAD = 100;

    /*
    Vil foreløpig kun returnere full utbetalingsgrad (behandler ikke søknader med barnehagesvar).
    Starter når barnet fyller kontantstøtte-alder eller søknadsdato dersom barnet allerede har fylt eldre (bør hentes).
    Tillater altså ikke tilbakevirkende foreløpig.
    Avsluttes måneden barnet fyller 23.måneder, da det ikke tas hensyn til barnehage enda.
     */

    public GradertPeriode opprettStønadPeriode(Faktagrunnlag grunnlag) {
        LocalDate søknadsDato = LocalDate.now();
        LocalDate gyldigAlderFom = grunnlag.getTpsFakta().getBarn().getFødselsdato().plusMonths(13).withDayOfMonth(1);
        LocalDate gyldigAlderTom = grunnlag.getTpsFakta().getBarn().getFødselsdato().plusMonths(23).withDayOfMonth(1);
        LocalDate startDato = søknadsDato.isBefore(gyldigAlderFom) ? gyldigAlderFom : søknadsDato.withDayOfMonth(1);
        return new GradertPeriode(startDato, gyldigAlderTom, FULL_UTBETALINGSGRAD);
    }
}
