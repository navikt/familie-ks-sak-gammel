package no.nav.familie.ks.sak.app.behandling.domene.kodeverk;

public enum VilkårType {

    MEDLEMSKAP(Constants.MEDLEMSKAP_KODE, "Medlemskap til folketrygden"),
    BARNEHAGE(Constants.BARNEHAGE_KODE, "Retten til basert på plass i barnehage (MVP)"),
    BOSTED(Constants.BOSTED_KODE, "Barn bor sammen med begge foreldre (MVP)"),
    BARN(Constants.BARN_KODE, "Barn er norsk statsborger"),
    UTLAND(Constants.UTLAND_KODE, "Søker har ikke oppgitt noen tilknytning til utland (MVP)");;

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
        public static final String BARNEHAGE_KODE = "BHAG";
        public static final String BOSTED_KODE = "BOST";
        public static final String BARN_KODE = "BARN";
        public static final String UTLAND_KODE = "UTL";
    }
}
