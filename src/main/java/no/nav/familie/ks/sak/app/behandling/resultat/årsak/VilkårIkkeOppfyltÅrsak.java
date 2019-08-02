package no.nav.familie.ks.sak.app.behandling.resultat.årsak;

public enum VilkårIkkeOppfyltÅrsak implements VilkårÅrsak {

    IKKE_FEM_ÅR_MEDLEMSKAP(8304, "Må ha vært medlem av den norske folketrygden i minst fem år"),
    BARNEHAGEPLASS(8310, "Barn det søkes kontantstøtte for har eller har hatt barnehageplass");

    private final int id;
    private final String beskrivelse;

    VilkårIkkeOppfyltÅrsak(int id, String beskrivelse) {
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
