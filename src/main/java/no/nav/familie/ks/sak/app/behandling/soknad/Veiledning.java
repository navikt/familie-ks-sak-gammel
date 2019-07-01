package no.nav.familie.ks.sak.app.behandling.soknad;

public class Veiledning {
    public String bekreftelse;

    public boolean erGyldig() {
        return "JA".equalsIgnoreCase(this.bekreftelse);
    }
}
