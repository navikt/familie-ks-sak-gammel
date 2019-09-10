package no.nav.familie.ks.sak.app.behandling.domene.typer;

import java.time.LocalDate;
import java.time.Month;

public class Tid {
    public static final LocalDate TIDENES_BEGYNNELSE;
    public static final LocalDate TIDENES_ENDE;

    private Tid() {
    }

    static {
        TIDENES_BEGYNNELSE = LocalDate.of(-4712, Month.JANUARY, 1);
        TIDENES_ENDE = LocalDate.of(9999, Month.DECEMBER, 31);
    }
}
