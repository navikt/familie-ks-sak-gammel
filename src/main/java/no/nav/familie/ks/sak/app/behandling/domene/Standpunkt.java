package no.nav.familie.ks.sak.app.behandling.domene;

import java.util.Arrays;

public enum Standpunkt {

    JA("Ja"), NEI("Nei"), UBESVART("Ubesvart");

    private String beskrivelse;

    Standpunkt(String beskrivelse) {
        this.beskrivelse = beskrivelse;
    }

    public static Standpunkt map(String verdi) {
        return Arrays.stream(Standpunkt.values()).filter(it -> it.name().equalsIgnoreCase(verdi)).findAny().orElse(Standpunkt.UBESVART);
    }

    public String getBeskrivelse() {
        return beskrivelse;
    }
}
