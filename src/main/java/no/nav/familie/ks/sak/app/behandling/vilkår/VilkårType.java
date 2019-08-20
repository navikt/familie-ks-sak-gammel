package no.nav.familie.ks.sak.app.behandling.vilkår;

public enum VilkårType {

    MEDLEMSKAP(Constants.MEDLEMSKAP_KODE, "Medlemskap til folketrygden"),
    BARNEHAGE(Constants.BARNEHAGE_KODE, "Retten til basert på plass i barnehage"),
    BOSTED(Constants.BOSTED_KODE, "Forelder/foreldre bor sammen med barn (MVP)");

    private final String kode;
    private final String beskrivelse;

    VilkårType(String kode, String beskrivelse) {
        this.kode = kode;
        this.beskrivelse = beskrivelse;
    }

    public String getKode() {
        return kode;
    }

    public String getBeskrivelse() {
        return beskrivelse;
    }

    public static class Constants {
        public static final String MEDLEMSKAP_KODE = "MEDL";
        public static final String BARNEHAGE_KODE = "BARN";
        public static final String BOSTED_KODE = "BOSTED";
    }
}
