package no.nav.familie.ks.sak.app.grunnlag.søknad;

public class Veiledning {
    public String bekreftelse;

    public boolean erGyldig() {
        return "JA".equalsIgnoreCase(this.bekreftelse);
    }
}
