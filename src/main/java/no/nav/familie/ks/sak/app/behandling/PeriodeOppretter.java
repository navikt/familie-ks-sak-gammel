package no.nav.familie.ks.sak.app.behandling;


import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;

import java.time.LocalDate;

public class PeriodeOppretter {

    private static final int FULL_UTBETALINGSGRAD = 100;

    public GradertPeriode opprettStønadPeriode(Faktagrunnlag grunnlag) {
        LocalDate søknadsDato = LocalDate.now(); //TODO: Hent fra fastsetting
        LocalDate gyldigAlderFom = grunnlag.getTpsFakta().getBarn().getFødselsdato().plusMonths(13).withDayOfMonth(1);
        LocalDate gyldigAlderTom = grunnlag.getTpsFakta().getBarn().getFødselsdato().plusMonths(23).withDayOfMonth(1);
        LocalDate startDato = søknadsDato.isBefore(gyldigAlderFom) ? gyldigAlderFom : søknadsDato.withDayOfMonth(1); //Tillater ikke tilbakevirkende
        return new GradertPeriode(startDato, gyldigAlderTom, FULL_UTBETALINGSGRAD);
        //TODO: Lag GradertPeriode fra barnehageinformasjon og regn ut prosent og periode for stønad ved barnehage
    }
}
