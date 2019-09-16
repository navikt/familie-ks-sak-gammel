package no.nav.familie.ks.sak.app.behandling.domene.typer;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

public abstract class AbstractLocalDateInterval implements Comparable<AbstractLocalDateInterval>, Serializable {
    public static final LocalDate TIDENES_ENDE;
    static final DateTimeFormatter FORMATTER;
    private static final LocalDate TIDENES_BEGYNNELSE;

    static {
        TIDENES_BEGYNNELSE = Tid.TIDENES_BEGYNNELSE;
        TIDENES_ENDE = Tid.TIDENES_ENDE;
        FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    }

    public AbstractLocalDateInterval() {
    }

    protected static LocalDate finnTomDato(LocalDate fom, int antallArbeidsdager) {
        if (antallArbeidsdager < 1) {
            throw new IllegalArgumentException("Antall arbeidsdager må være 1 eller større.");
        } else {
            LocalDate tom = fom;
            int antallArbeidsdagerTmp = antallArbeidsdager;

            while (antallArbeidsdagerTmp > 0) {
                if (antallArbeidsdagerTmp > antallArbeidsdager) {
                    throw new IllegalArgumentException("Antall arbeidsdager beregnes feil.");
                }

                if (erArbeidsdag(tom)) {
                    --antallArbeidsdagerTmp;
                }

                if (antallArbeidsdagerTmp > 0) {
                    tom = tom.plusDays(1L);
                }
            }

            return tom;
        }
    }

    protected static LocalDate finnFomDato(LocalDate tom, int antallArbeidsdager) {
        if (antallArbeidsdager < 1) {
            throw new IllegalArgumentException("Antall arbeidsdager må være 1 eller større.");
        } else {
            LocalDate fom = tom;
            int antallArbeidsdagerTmp = antallArbeidsdager;

            while (antallArbeidsdagerTmp > 0) {
                if (antallArbeidsdagerTmp > antallArbeidsdager) {
                    throw new IllegalArgumentException("Antall arbeidsdager beregnes feil.");
                }

                if (erArbeidsdag(fom)) {
                    --antallArbeidsdagerTmp;
                }

                if (antallArbeidsdagerTmp > 0) {
                    fom = fom.minusDays(1L);
                }
            }

            return fom;
        }
    }

    public static LocalDate forrigeArbeidsdag(LocalDate dato) {
        if (dato != TIDENES_BEGYNNELSE && dato != TIDENES_ENDE) {
            switch (dato.getDayOfWeek()) {
                case SATURDAY:
                    return dato.minusDays(1L);
                case SUNDAY:
                    return dato.minusDays(2L);
            }
        }

        return dato;
    }

    public static LocalDate nesteArbeidsdag(LocalDate dato) {
        if (dato != TIDENES_BEGYNNELSE && dato != TIDENES_ENDE) {
            switch (dato.getDayOfWeek()) {
                case SATURDAY:
                    return dato.plusDays(2L);
                case SUNDAY:
                    return dato.plusDays(1L);
            }
        }

        return dato;
    }

    private static List<LocalDate> listArbeidsdager(LocalDate fomDato, LocalDate tomDato) {
        List<LocalDate> arbeidsdager = new ArrayList();

        for (LocalDate dato = fomDato; !dato.isAfter(tomDato); dato = dato.plusDays(1L)) {
            if (erArbeidsdag(dato)) {
                arbeidsdager.add(dato);
            }
        }

        return arbeidsdager;
    }

    protected static boolean erArbeidsdag(LocalDate dato) {
        return !dato.getDayOfWeek().equals(DayOfWeek.SATURDAY) && !dato.getDayOfWeek().equals(DayOfWeek.SUNDAY);
    }

    public abstract LocalDate getFomDato();

    public abstract LocalDate getTomDato();

    protected abstract AbstractLocalDateInterval lagNyPeriode(LocalDate var1, LocalDate var2);

    public boolean erFørEllerLikPeriodeslutt(ChronoLocalDate dato) {
        return this.getTomDato() == null || this.getTomDato().isAfter(dato) || this.getTomDato().isEqual(dato);
    }

    public boolean erEtterEllerLikPeriodestart(ChronoLocalDate dato) {
        return this.getFomDato().isBefore(dato) || this.getFomDato().isEqual(dato);
    }

    public boolean inkluderer(ChronoLocalDate dato) {
        return this.erEtterEllerLikPeriodestart(dato) && this.erFørEllerLikPeriodeslutt(dato);
    }

    public boolean inkludererArbeidsdag(LocalDate dato) {
        return this.erEtterEllerLikPeriodestart(nesteArbeidsdag(dato)) && this.erFørEllerLikPeriodeslutt(forrigeArbeidsdag(dato));
    }

    public long antallDager() {
        return ChronoUnit.DAYS.between(this.getFomDato(), this.getTomDato());
    }

    public int antallArbeidsdager() {
        if (this.getTomDato().isEqual(TIDENES_ENDE)) {
            throw new IllegalStateException("Både fra og med og til og med dato må være satt for å regne ut arbeidsdager.");
        } else {
            return this.arbeidsdager().size();
        }
    }

    public int maksAntallArbeidsdager() {
        if (this.getTomDato().isEqual(TIDENES_ENDE)) {
            throw new IllegalStateException("Både fra og med og til og med dato må være satt for å regne ut arbeidsdager.");
        } else {
            LocalDate månedsstart = this.getFomDato().minusDays((long) this.getFomDato().getDayOfMonth() - 1L);
            LocalDate månedsslutt = this.getTomDato().minusDays((long) this.getTomDato().getDayOfMonth() - 1L).plusDays((long) this.getTomDato().lengthOfMonth() - 1L);
            return listArbeidsdager(månedsstart, månedsslutt).size();
        }
    }

    public List<LocalDate> arbeidsdager() {
        return listArbeidsdager(this.getFomDato(), this.getTomDato());
    }

    public boolean grenserTil(AbstractLocalDateInterval periode2) {
        return this.getTomDato().equals(periode2.getFomDato().minusDays(1L)) || periode2.getTomDato().equals(this.getFomDato().minusDays(1L));
    }

    public List<AbstractLocalDateInterval> splittVedMånedsgrenser() {
        List<AbstractLocalDateInterval> perioder = new ArrayList();
        LocalDate dato = this.getFomDato().minusDays((long) this.getFomDato().getDayOfMonth() - 1L);
        LocalDate periodeFomDato = this.getFomDato();

        while (dato.isBefore(this.getTomDato())) {
            int dagerIMåned = dato.lengthOfMonth();
            LocalDate sisteDagIMåneden = dato.plusDays((long) dagerIMåned - 1L);
            boolean harMånedsslutt = this.inkluderer(sisteDagIMåneden);
            if (harMånedsslutt) {
                perioder.add(this.lagNyPeriode(periodeFomDato, sisteDagIMåneden));
                dato = sisteDagIMåneden.plusDays(1L);
                periodeFomDato = dato;
            } else {
                perioder.add(this.lagNyPeriode(periodeFomDato, this.getTomDato()));
                dato = this.getTomDato();
            }
        }

        return perioder;
    }

    public double finnMånedeskvantum() {
        Collection<AbstractLocalDateInterval> perioder = this.splittVedMånedsgrenser();
        double kvantum = 0.0D;
        Iterator var4 = perioder.iterator();

        while (var4.hasNext()) {
            AbstractLocalDateInterval periode = (AbstractLocalDateInterval) var4.next();
            int antallArbeidsdager = periode.antallArbeidsdager();
            if (antallArbeidsdager != 0) {
                int diff = periode.maksAntallArbeidsdager() - antallArbeidsdager;
                kvantum += diff == 0 ? 1.0D : (double) diff / (double) periode.maksAntallArbeidsdager();
            }
        }

        return kvantum;
    }

    public List<AbstractLocalDateInterval> splittPeriodePåDatoer(LocalDate... datoer) {
        List<LocalDate> datoListe = Arrays.asList(datoer);
        Collections.sort(datoListe);
        List<AbstractLocalDateInterval> perioder = new ArrayList();
        AbstractLocalDateInterval periode = this;
        Iterator var5 = datoListe.iterator();

        while (var5.hasNext()) {
            LocalDate dato = (LocalDate) var5.next();
            if (periode.inkluderer(dato) && dato.isAfter(periode.getFomDato())) {
                perioder.add(this.lagNyPeriode(periode.getFomDato(), dato.minusDays(1L)));
                periode = this.lagNyPeriode(dato, periode.getTomDato());
            }
        }

        perioder.add(periode);
        return perioder;
    }

    public List<AbstractLocalDateInterval> splittPeriodePåDatoerAvgrensTilArbeidsdager(LocalDate... datoer) {
        List<LocalDate> datoListe = Arrays.asList(datoer);
        Collections.sort(datoListe);
        List<AbstractLocalDateInterval> perioder = new ArrayList();
        AbstractLocalDateInterval periode = this.avgrensTilArbeidsdager();
        Iterator var5 = datoListe.iterator();

        while (var5.hasNext()) {
            LocalDate dato = (LocalDate) var5.next();
            if (periode.inkluderer(dato) && dato.isAfter(periode.getFomDato())) {
                perioder.add(this.lagNyPeriode(periode.getFomDato(), dato.minusDays(1L)).avgrensTilArbeidsdager());
                periode = this.lagNyPeriode(dato, periode.getTomDato()).avgrensTilArbeidsdager();
            }
        }

        perioder.add(periode);
        return perioder;
    }

    public AbstractLocalDateInterval avgrensTilArbeidsdager() {
        LocalDate nyFomDato = nesteArbeidsdag(this.getFomDato());
        LocalDate nyTomDato = forrigeArbeidsdag(this.getTomDato());
        return nyFomDato.equals(this.getFomDato()) && nyTomDato.equals(this.getTomDato()) ? this : this.lagNyPeriode(nyFomDato, nyTomDato);
    }

    public AbstractLocalDateInterval kuttPeriodePåGrenseneTil(AbstractLocalDateInterval periode) {
        LocalDate nyFomDato = this.getFomDato().isBefore(periode.getFomDato()) ? periode.getFomDato() : this.getFomDato();
        LocalDate nyTomDato = this.getTomDato().isAfter(periode.getTomDato()) ? periode.getTomDato() : this.getTomDato();
        return this.lagNyPeriode(nyFomDato, nyTomDato);
    }

    public int compareTo(AbstractLocalDateInterval periode) {
        return this.getFomDato().compareTo(periode.getFomDato());
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        } else if (!(object instanceof AbstractLocalDateInterval)) {
            return false;
        } else {
            AbstractLocalDateInterval annen = (AbstractLocalDateInterval) object;
            return this.likFom(annen) && this.likTom(annen);
        }
    }

    private boolean likFom(AbstractLocalDateInterval annen) {
        boolean likFom = Objects.equals(this.getFomDato(), annen.getFomDato());
        if (this.getFomDato() != null && annen.getFomDato() != null) {
            return likFom || Objects.equals(nesteArbeidsdag(this.getFomDato()), nesteArbeidsdag(annen.getFomDato()));
        } else {
            return likFom;
        }
    }

    private boolean likTom(AbstractLocalDateInterval annen) {
        boolean likTom = Objects.equals(this.getTomDato(), annen.getTomDato());
        if (this.getTomDato() != null && annen.getTomDato() != null) {
            return likTom || Objects.equals(forrigeArbeidsdag(this.getTomDato()), forrigeArbeidsdag(annen.getTomDato()));
        } else {
            return likTom;
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.getFomDato(), this.getTomDato()});
    }

    public String toString() {
        return String.format("Periode: %s - %s", this.getFomDato().format(FORMATTER), this.getTomDato().format(FORMATTER));
    }
}
