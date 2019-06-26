package no.nav.familie.ks.sak.behandling.soknad;

public class Oppsummering {
    public String bekreftelse;

    public boolean erGyldig() {
        return "JA".equalsIgnoreCase(this.bekreftelse);
    }
}
