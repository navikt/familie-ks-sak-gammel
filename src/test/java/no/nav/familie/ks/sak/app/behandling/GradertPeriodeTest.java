package no.nav.familie.ks.sak.app.behandling;

import org.junit.Test;

import java.time.LocalDate;
import java.time.Month;

public class GradertPeriodeTest {

    @Test(expected = NullPointerException.class)
    public void manglene_fom_skal_kaste_exception() {
        new GradertPeriode(null, LocalDate.of(2019, Month.FEBRUARY, 1), 50);
    }

    @Test(expected = NullPointerException.class)
    public void maglende_tom_skal_kaste_exception() {
        new GradertPeriode(LocalDate.of(2019, Month.JANUARY, 1), null, 50);
    }

    @Test(expected = IllegalArgumentException.class)
    public void fom_etter_tom_skal_kaste_exception() {
        new GradertPeriode(LocalDate.of(2019, Month.FEBRUARY, 1), LocalDate.of(2019, Month.JANUARY, 1), 50);
    }
}
