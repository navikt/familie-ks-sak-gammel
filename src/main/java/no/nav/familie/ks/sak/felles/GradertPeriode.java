package no.nav.familie.ks.sak.felles;

import java.time.LocalDate;
import java.util.Objects;

/**
 * En periode som har definert prosent, start- og slutt-tidpunkt
 */
public class GradertPeriode {

    private final LocalDate fom;
    private final LocalDate tom;
    private final int prosent;

    public GradertPeriode(LocalDate fom, LocalDate tom, int prosent) {
        Objects.requireNonNull(tom);
        Objects.requireNonNull(fom);
        Objects.requireNonNull(prosent);
        if (tom.isBefore(fom)) {
            throw new IllegalArgumentException("Til og med dato før fra og med dato: " + fom + ">" + tom);
        }
        if (prosent > 100 || prosent < 0) {
            throw new IllegalArgumentException("Prosent må være mellom 0 og 100, men er " + prosent);
        }
        this.fom = fom;
        this.tom = tom;
        this.prosent = prosent;
    }

    public LocalDate getFom() {
        return fom;
    }

    public LocalDate getTom() {
        return tom;
    }

    public int getProsent() {
        return prosent;
    }

}
