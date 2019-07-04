package no.nav.familie.ks.sak.app.behandling.soknad;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VedleggMetadata {

    private final String filreferanse;

    private final String filnavn;

    public VedleggMetadata(
            @JsonProperty("filreferanse") String filreferanse,
            @JsonProperty("filnavn") String filnavn
    ) {
        this.filreferanse = filreferanse;
        this.filnavn = filnavn;
    }

    public String getFilreferanse() {
        return filreferanse;
    }

    public String getFilnavn() {
        return filnavn;
    }
}
