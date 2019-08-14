package no.nav.familie.ks.sak.app.grunnlag.søknad;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Barn {
    private String navn;
    private String fodselsdato;
    private String erFlerling;

    public String getNavn() {
        return navn;
    }

    public LocalDate getFodselsdato() {
        if (fodselsdato == null) {
            return null;
        }
        try {
            return LocalDate.parse(fodselsdato, DateTimeFormatter.ISO_DATE);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    public String getErFlerling() {
        return erFlerling;
    }
}
