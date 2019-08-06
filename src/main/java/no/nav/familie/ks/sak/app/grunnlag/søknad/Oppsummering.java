package no.nav.familie.ks.sak.app.grunnlag.søknad;

public class Oppsummering {
    public String bekreftelse;

    public boolean erGyldig() {
        return "JA".equalsIgnoreCase(this.bekreftelse);
    }
}
