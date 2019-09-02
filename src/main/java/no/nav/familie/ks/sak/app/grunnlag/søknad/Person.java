package no.nav.familie.ks.sak.app.grunnlag.søknad;

public class Person {
    private String fnr;
    private String navn;
    private String statsborgerskap;

    public Person() {
    }

    public Person(String fnr, String navn, String statsborgerskap) {
        this.fnr = fnr;
        this.navn = navn;
        this.statsborgerskap = statsborgerskap;
    }

    public String getFnr() {
        return fnr;
    }

    public String getNavn() {
        return navn;
    }

    public String getStatsborgerskap() {
        return statsborgerskap;
    }
}
