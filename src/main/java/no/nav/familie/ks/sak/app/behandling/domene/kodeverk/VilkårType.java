package no.nav.familie.ks.sak.app.behandling.domene.kodeverk;

public enum VilkårType {

    MEDLEMSKAP_BOSTED(Constants.MEDLEMSKAP_BOSTED_KODE, "Medlemskap til folketrygden", "§2 og §3"),
    MEDLEMSKAP_MEDL(Constants.MEDLEMSKAP_MEDL_KODE, "Medlemskap til folketrygden", "§2 og §3"),
    MEDLEMSKAP_STATSBORGERSKAP(Constants.MEDLEMSKAP_STATSBORGERSKAP_KODE, "Medlemskap til folketrygden", "§2 og §3"),
    BARNEHAGE(Constants.BARNEHAGE_KODE, "Retten til basert på plass i barnehage", "§1"),
    BOSTED(Constants.BOSTED_KODE, "Kontantstøtte ytes til den som barnet bor fast hos.", "§3"),
    BARN(Constants.BARN_KODE, "Barn er norsk statsborger", "§2"),

    KUN_ET_BARN(Constants.BARN_KODE, "Kun et barn er oppgitt i søknaden (MVP)", "MVP"),
    UTLAND(Constants.UTLAND_KODE, "Søker har ikke oppgitt noen tilknytning til utland (MVP)", "lovreferanse"),
    BARN_MELLOM_10_OG_14_MÅNEDER(Constants.BARN_MELLOM_10_OG_14_MÅNEDER_KODE, "Barn er mellom 10 og 14 måneder", "MVP: Se regel 1.7 i Confluence"),
    ANNEN_PART_STEMMER(Constants.ANNEN_PART_KODE, "Oppgitt annen part fra søknaden og annen part fra TPS stemme", "MVP"),
    ANNEN_PART_ER_OPPGITT(Constants.ANNEN_PART_KODE, "Annen part er tilstedet i grunnlaget fra TPS ", "MVP"),
    IKKE_LØPENDE_KS_FOR_BARN(Constants.IKKE_LØPENDE_KS_FOR_BARN, "Barnet skal ikke motta kontantstøtte eller være under behandling for kontantstøtte.", "MVP");

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
        public static final String ANNEN_PART_KODE = "ANNEN_PART";
        public static final String MEDLEMSKAP_BOSTED_KODE = "MEDL";
        public static final String MEDLEMSKAP_MEDL_KODE = "MEDL";
        public static final String MEDLEMSKAP_STATSBORGERSKAP_KODE = "MEDL";
        public static final String BARNEHAGE_KODE = "BHAG";
        public static final String BOSTED_KODE = "BOST";
        public static final String BARN_KODE = "BARN";
        public static final String UTLAND_KODE = "UTL";
        public static final String BARN_MELLOM_10_OG_14_MÅNEDER_KODE = "BARN_MELLOM_10_OG_14_MÅNEDER";
        public static final String IKKE_LØPENDE_KS_FOR_BARN = "IKKE_LØPENDE_KS_FOR_BARN";
    }
}
