package no.nav.familie.ks.sak.app.grunnlag.søknad;

public class Person {
    public String fnr;
    public String navn;
    public String statsborgerskap;

    public Person() {
    }

    public Person(String fnr, String navn, String statsborgerskap) {
        this.fnr = fnr;
        this.navn = navn;
        this.statsborgerskap = statsborgerskap;
    }
}
