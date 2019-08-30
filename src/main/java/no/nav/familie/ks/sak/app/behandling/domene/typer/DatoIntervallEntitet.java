package no.nav.familie.ks.sak.app.behandling.domene.typer;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalDate;

@Embeddable
public class DatoIntervallEntitet extends AbstractLocalDateInterval {

    @Column(
        name = "fom"
    )
    private LocalDate fomDato;
    @Column(
        name = "tom"
    )
    private LocalDate tomDato;

    private DatoIntervallEntitet() {
    }

    private DatoIntervallEntitet(LocalDate fomDato, LocalDate tomDato) {
        if (fomDato == null) {
            throw new IllegalArgumentException("Fra og med dato må være satt.");
        } else if (tomDato == null) {
            throw new IllegalArgumentException("Til og med dato må være satt.");
        } else if (tomDato.isBefore(fomDato)) {
            throw new IllegalArgumentException("Til og med dato før fra og med dato.");
        } else {
            this.fomDato = fomDato;
            this.tomDato = tomDato;
        }
    }

    public static DatoIntervallEntitet fraOgMedTilOgMed(LocalDate fomDato, LocalDate tomDato) {
        return new DatoIntervallEntitet(fomDato, tomDato);
    }

    public static DatoIntervallEntitet fraOgMed(LocalDate fomDato) {
        return new DatoIntervallEntitet(fomDato, TIDENES_ENDE);
    }

    public static DatoIntervallEntitet fraOgMedPlusArbeidsdager(LocalDate fom, int antallArbeidsdager) {
        return fraOgMedTilOgMed(fom, finnTomDato(fom, antallArbeidsdager));
    }

    public static DatoIntervallEntitet tilOgMedMinusArbeidsdager(LocalDate tom, int antallArbeidsdager) {
        return fraOgMedTilOgMed(finnFomDato(tom, antallArbeidsdager), tom);
    }

    public LocalDate getFomDato() {
        return this.fomDato;
    }

    public LocalDate getTomDato() {
        return this.tomDato;
    }

    protected DatoIntervallEntitet lagNyPeriode(LocalDate fomDato, LocalDate tomDato) {
        return fraOgMedTilOgMed(fomDato, tomDato);
    }
}
