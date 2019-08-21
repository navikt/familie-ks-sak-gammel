package no.nav.familie.ks.sak.app.behandling.domene.kodeverk;

import java.util.Arrays;

public enum Standpunkt {

    JA("JA", "Ja"), NEI("NEI", "Nei"), UBESVART("UBESVART", "Ubesvart");

    private String kode;
    private String beskrivelse;

    Standpunkt(String kode, String beskrivelse) {
        this.kode = kode;
        this.beskrivelse = beskrivelse;
    }

    public static Standpunkt map(String verdi) {
        return Arrays.stream(Standpunkt.values()).filter(it -> it.getKode().equals(verdi)).findFirst().orElseThrow(IllegalArgumentException::new);
    }

    public static Standpunkt map(String verdi, Standpunkt defaultValue) {
        return Arrays.stream(Standpunkt.values()).filter(it -> it.getKode().equals(verdi)).findFirst().orElse(defaultValue);
    }

    public String getBeskrivelse() {
        return beskrivelse;
    }

    public String getKode() {
        return kode;
    }
}
