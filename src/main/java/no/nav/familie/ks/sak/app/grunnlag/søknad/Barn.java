package no.nav.familie.ks.sak.app.grunnlag.søknad;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Barn {
    private String navn;
    private String fødselsdato;
    private String fødselsnummer;
    private String erFlerling;

    public String getNavn() {
        return navn;
    }

    public LocalDate getFødselsdato() {
        if (fødselsdato == null) {
            return null;
        }
        try {
            return LocalDate.parse(fødselsdato, DateTimeFormatter.ISO_DATE);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    public String getFødselsnummer() {
        return fødselsnummer;
    }

    public String getErFlerling() {
        return erFlerling;
    }
}
