package no.nav.familie.ks.sak.app.behandling.resultat.årsak;

public enum VilkårIkkeOppfyltÅrsak implements VilkårÅrsak {

    IKKE_FEM_ÅR_MEDLEMSKAP(8301, "Må ha vært medlem av den norske folketrygden i minst fem år"),
    BARNEHAGEPLASS(8302, "Barn det søkes kontantstøtte for har eller har hatt barnehageplass");

    private final int årsakKode;
    private final String beskrivelse;

    VilkårIkkeOppfyltÅrsak(int årsakKode, String beskrivelse) {
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
}
