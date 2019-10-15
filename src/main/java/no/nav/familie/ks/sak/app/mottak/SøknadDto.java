package no.nav.familie.ks.sak.app.mottak;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SøknadDto {

    private String søknadJson;

    private String saksnummer;

    @JsonCreator
    public SøknadDto(@JsonProperty("søknadJson") String søknadJson, @JsonProperty("saksnummer") String saksnummer) {
        this.søknadJson = søknadJson;
        this.saksnummer = saksnummer;
    }


    public String getSøknadJson() {
        return søknadJson;
    }

    public String getSaksnummer() {
        return saksnummer;
    }
}
