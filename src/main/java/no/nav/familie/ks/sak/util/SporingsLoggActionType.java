package no.nav.familie.ks.sak.util;

public enum SporingsLoggActionType {

    READ("read"),
    UPDATE("update"),
    CREATE("create"),
    DELETE("delete");

    private String kode;

    SporingsLoggActionType(String kode) {
        this.kode = kode;
    }

    public String getKode() {
        return kode;
    }
}
