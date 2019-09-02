package no.nav.familie.ks.sak.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateParser {
    public static LocalDate parseInputDatoFraSøknad(String dato) {
        try {
            return LocalDate.parse(dato);
        } catch (DateTimeParseException e) {
            return LocalDate.parse(dato, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        }
    }
}
