package no.nav.familie.ks.sak.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateParser {
    public static LocalDate parseSøknadDato(String dato) {
        return LocalDate.parse(dato, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }
}
