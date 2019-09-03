package no.nav.familie.ks.sak.app.behandling.domene.kodeverk;

public enum VilkårType {

    MEDLEMSKAP(Constants.MEDLEMSKAP_KODE, "Medlemskap til folketrygden", "lovreferanse"),
    BARNEHAGE(Constants.BARNEHAGE_KODE, "Retten til basert på plass i barnehage", "lovreferanse"),
    BOSTED(Constants.BOSTED_KODE, "Barn bor sammen med begge foreldre (MVP)", "lovreferanse"),
    BARN(Constants.BARN_KODE, "Barn er norsk statsborger", "lovreferanse");;

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
    }
}
