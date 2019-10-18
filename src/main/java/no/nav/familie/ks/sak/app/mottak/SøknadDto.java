package no.nav.familie.ks.sak.app.mottak;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SøknadDto {

    private String søknadJson;

    private String saksnummer;

    private String journalpostID;


    @JsonCreator
    public SøknadDto(@JsonProperty("søknadJson") String søknadJson, @JsonProperty("saksnummer") String saksnummer,
                     @JsonProperty("journalpostID") String journalpostID) {
        this.søknadJson = søknadJson;
        this.saksnummer = saksnummer;
        this.journalpostID = journalpostID;
    }


    public String getSøknadJson() {
        return søknadJson;
    }

    public String getSaksnummer() {
        return saksnummer;
    }

    public String getJournalpostID() {
        return journalpostID;
    }
}
