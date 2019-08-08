package no.nav.familie.ks.sak.app.behandling.resultat;

public enum UtfallType {
    IKKE_OPPFYLT("Ikke oppfylt"),
    OPPFYLT("Oppfylt"),
    UAVKLART("Uavklart"),
    MANUELL_BEHANDLING("Manuell behandling");

    private String beskrivelse;

    UtfallType(String beskrivelse) {
        this.beskrivelse = beskrivelse;
    }

    public String getBeskrivelse() {
        return beskrivelse;
    }
}
