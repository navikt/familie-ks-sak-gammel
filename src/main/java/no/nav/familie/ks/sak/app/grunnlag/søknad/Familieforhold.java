package no.nav.familie.ks.sak.app.grunnlag.søknad;

public class Familieforhold {
    private String borForeldreneSammenMedBarnet;
    private String annenForelderNavn;
    private String annenForelderFødselsnummer;

    public Familieforhold(String borForeldreneSammenMedBarnet, String annenForelderNavn, String annenForelderFødselsnummer) {
        this.borForeldreneSammenMedBarnet = borForeldreneSammenMedBarnet;
        this.annenForelderNavn = annenForelderNavn;
        this.annenForelderFødselsnummer = annenForelderFødselsnummer;
    }

    public Familieforhold() {
    }

    public String getBorForeldreneSammenMedBarnet() {
        return borForeldreneSammenMedBarnet;
    }

    public String getAnnenForelderNavn() {
        return annenForelderNavn;
    }

    public String getAnnenForelderFødselsnummer() {
        return annenForelderFødselsnummer;
    }
}
