package no.nav.familie.ks.sak.app.behandling.resultat.årsak;

public enum VilkårOppfyltÅrsak implements VilkårÅrsak {

    VILKÅR_OPPFYLT(8601, "Vilkår for å søke kontantstøtte er godkjent");

    private final int årsakKode;
    private final String beskrivelse;

    VilkårOppfyltÅrsak(int årsakKode, String beskrivelse) {
        this.årsakKode = årsakKode;
        this.beskrivelse = beskrivelse;
    }

    @Override
    public int getÅrsakKode() {
        return årsakKode;
    }

    @Override
    public String getBeskrivelse() {
        return beskrivelse;
    }

    @Override
    public String getReasonTextTemplate() {
        return beskrivelse;
    }

    @Override
    public String getReasonCode() {
        return "" + årsakKode;
    }
}
