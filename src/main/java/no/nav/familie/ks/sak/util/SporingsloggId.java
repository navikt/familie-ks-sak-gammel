package no.nav.familie.ks.sak.util;

public enum SporingsloggId {

    SAKSNUMMER("saksnummer"),
    ANSVALIG_SAKSBEHANDLER("ansvarlig_saksbehandler"),
    FNR("fnr"),
    AKTOR_ID("aktorId");

    private String kode;

    SporingsloggId(String kode) {
        this.kode = kode;
    }

    public String getSporingsloggKode() {
        return this.kode;
    }

}
