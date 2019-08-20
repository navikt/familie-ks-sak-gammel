package no.nav.familie.ks.sak.util;

import java.time.LocalDate;

public class DateParser {
    public static LocalDate parseSøknadDato(String dato) {
        return LocalDate.parse(dato);
    }
}
