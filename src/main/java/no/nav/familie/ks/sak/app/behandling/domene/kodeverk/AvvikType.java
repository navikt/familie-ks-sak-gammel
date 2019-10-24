package no.nav.familie.ks.sak.app.behandling.domene.kodeverk;

public enum AvvikType implements VilkårEllerAvvikType {


    AVVIK_PERSON_IKKE_FUNNET(Constants.PERSON_IKKE_FUNNET_KODE, "Avvik: Klarte ikke innhente personopplysninger for søker, medforelder og/eller barn");

    private final String kode;
    private final String beskrivelse;

    AvvikType(String kode, String beskrivelse) {
        this.kode = kode;
        this.beskrivelse = beskrivelse;
    }

    @Override
    public String getKode() {
        return kode;
    }

    @Override
    public String getBeskrivelse() {
        return beskrivelse;
    }

    @Override
    public String getLovReferanse() {
        return null;
    }

    public static class Constants {
        public static final String PERSON_IKKE_FUNNET_KODE = "AVVIK_PERSONIKKEFUNNET";
    }
}
