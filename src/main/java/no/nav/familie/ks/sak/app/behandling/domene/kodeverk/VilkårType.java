package no.nav.familie.ks.sak.app.behandling.domene.kodeverk;

public enum VilkårType {

    MEDLEMSKAP(Constants.MEDLEMSKAP_KODE, "Medlemskap til folketrygden", "§2 og §3"),
    BARNEHAGE(Constants.BARNEHAGE_KODE, "Retten til basert på plass i barnehage", "§1"),
    BOSTED(Constants.BOSTED_KODE, "Kontantstøtte ytes til den som barnet bor fast hos.", "§3"),
    BARN(Constants.BARN_KODE, "Barn er norsk statsborger", "§2"),
    KUN_ET_BARN(Constants.BARN_KODE, "Kun et barn er oppgitt i søknaden (MVP)", "MVP"),
    UTLAND(Constants.UTLAND_KODE, "Søker har ikke oppgitt noen tilknytning til utland (MVP)", "lovreferanse");

    private final String kode;
    private final String beskrivelse;
    private final String lovreferanse;

    VilkårType(String kode, String beskrivelse, String lovreferanse) {
        this.kode = kode;
        this.beskrivelse = beskrivelse;
        this.lovreferanse = lovreferanse;
    }

    /**
     * Benyttes i skriv til brukeren etc.
     *
     * @return
     */
    public String getLovReferanse() {
        return lovreferanse;
    }

    public String getKode() {
        return kode;
    }

    public String getBeskrivelse() {
        return beskrivelse;
    }

    @Override
    public String toString() {
        return "VilkårType{" +
                "ref='" + name() + '\'' +
                "kode='" + kode + '\'' +
                ", lovreferanse='" + lovreferanse + '\'' +
                '}';
    }

    public static class Constants {
        public static final String MEDLEMSKAP_KODE = "MEDL";
        public static final String BARNEHAGE_KODE = "BHAG";
        public static final String BOSTED_KODE = "BOST";
        public static final String BARN_KODE = "BARN";
        public static final String UTLAND_KODE = "UTL";
    }
}
