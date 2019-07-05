package no.nav.familie.ks.sak.app.behandling;


import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;

import java.time.LocalDate;

public class PeriodeOppretter {

    private static final int FULL_UTBETALINGSGRAD = 100;

    public GradertPeriode opprettStønadPeriode(Faktagrunnlag grunnlag) {
        LocalDate søknadsDato = LocalDate.now(); //TODO: Hent fra fastsetting
        LocalDate gyldigAlderFom = grunnlag.getTpsFakta().getBarn().getFødselsdato().plusMonths(13);
        LocalDate gyldigAlderTom = grunnlag.getTpsFakta().getBarn().getFødselsdato().plusMonths(23);
        LocalDate startDato = søknadsDato.isBefore(gyldigAlderFom) ? gyldigAlderFom : søknadsDato; //Tillater ikke tilbakevirkende
        /*
        LocalDate opphørDato;
        try {
            GradertPeriode barnehagePeriode = opprettBarnehagePeriode(grunnlag);
            opphørDato = gyldigAlderTom.isAfter(barnehagePeriode.getTom()) ? gyldigAlderTom : barnehagePeriode.getFom();
            int utbetalingsgrad = finnUtbetalingsgrad(barnehagePeriode.getProsent());
            return new GradertPeriode(startDato, opphørDato, utbetalingsgrad);
        }
        catch (NullPointerException ingenBarnehagePlass) {
            return new GradertPeriode(startDato, gyldigAlderTom, FULL_UTBETALINGSGRAD);
        }
        */
        return new GradertPeriode(startDato, gyldigAlderTom, 100);
    }

    private GradertPeriode opprettBarnehagePeriode(Faktagrunnlag faktagrunnlag) {
        //TODO: Hent fra søknad
        return new GradertPeriode(
                LocalDate.now(),
                LocalDate.now(),
                100
        );
    }

    private int finnUtbetalingsgrad(int barnehageProsent) {
        if (barnehageProsent > 80) {
            return 0;
        } else if (barnehageProsent > 60) {
            return 20;
        } else if (barnehageProsent > 40) {
            return 40;
        } else if (barnehageProsent > 20) {
            return 60;
        } else {
            return 80;
        }
    }

}
