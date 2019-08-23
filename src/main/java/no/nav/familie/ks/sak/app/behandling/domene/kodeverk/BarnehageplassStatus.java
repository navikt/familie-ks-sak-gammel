package no.nav.familie.ks.sak.app.behandling.domene.kodeverk;

import java.util.Arrays;

public enum BarnehageplassStatus {
    HAR_IKKE("garIkkeIBarnehage", "Går ikke i barnehage"),
    HAR("harBarnehageplass", "Har barnehageplass"),
    HAR_SLUTTET("harSluttetIBarnehage", "Har sluttet i barnehage"),
    SKAL_BEGYNNE("skalBegynneIBarnehage", "Skal begynne i barnehage"),
    SKAL_SLUTTE("skalSlutteIBarnehage", "Skal slutte i barnehage");

    private String kode;
    private String beskrivelse;

    BarnehageplassStatus(String eksternKode, String beskrivelse) {
        this.kode = eksternKode;
        this.beskrivelse = beskrivelse;
    }

    public static BarnehageplassStatus map(String eksternKode) {
        return Arrays.stream(BarnehageplassStatus.values()).filter(it -> it.getKode().equals(eksternKode)).findFirst().orElseThrow(IllegalArgumentException::new);
    }

    public String getBeskrivelse() {
        return beskrivelse;
    }

    public String getKode() {
        return kode;
    }
}
