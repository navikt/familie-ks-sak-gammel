package no.nav.familie.ks.sak.app.mottak;

public class SøknadDto {

    private String søknadJson;

    private String saksnummer;

    public SøknadDto(String søknadJson, String saksnummer) {
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
