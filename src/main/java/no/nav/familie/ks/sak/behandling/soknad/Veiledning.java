package no.nav.familie.ks.sak.behandling.soknad;

public class Veiledning {
    public String bekreftelse;

    public boolean erGyldig() {
        return "JA".equalsIgnoreCase(this.bekreftelse);
    }
}
