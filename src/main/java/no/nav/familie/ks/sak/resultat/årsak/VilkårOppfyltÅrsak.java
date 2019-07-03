package no.nav.familie.ks.sak.resultat.årsak;

public enum VilkårOppfyltÅrsak implements VilkårÅrsak {

    VILKÅR_OPPFYLT(8601, "Vilkår for å søke kontantstøtte er godkjenn");

    private final int id;
    private final String beskrivelse;

    VilkårOppfyltÅrsak(int id, String beskrivelse) {
        this.id = id;
        this.beskrivelse = beskrivelse;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getBeskrivelse() {
        return beskrivelse;
    }
}
