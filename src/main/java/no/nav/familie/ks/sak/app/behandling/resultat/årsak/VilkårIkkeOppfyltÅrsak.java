package no.nav.familie.ks.sak.app.behandling.resultat.årsak;

public enum VilkårIkkeOppfyltÅrsak implements VilkårÅrsak {

    IKKE_FEM_ÅR_MEDLEMSKAP(8301, "Må ha vært medlem av den norske folketrygden i minst fem år"),
    BARNEHAGEPLASS(8302, "Søknaden inneholder barnehageplass"),
    IKKE_BOSATT_I_NORGE_FEM_ÅR(8303, "Ikke bosatt vært bosatt i Norge sammenhengende siste fem år"),
    BARN_UTENFOR_KONTANTSTØTTE_ALDER(8305, "Barn utenfor kontantstøtte alder"),
    IKKE_NORSKE_STATSBORGERE_FEM_ÅR(8304, "Foreldre har ikke hatt norsk statsborgerskap sammenhegende siste fem år"),
    IKKE_BEGGE_FORELDRE(8307, "Begge foreldre er ikke registrert på barn i TPS"),
    IKKE_BOSATT_SAMMEN(8306, "Barn er ikke bosatt sammen med begge foreldre"),
    BARN_IKKE_NORSK_STATSBORGER(8307, "Barn er ikke norsk statsborger");

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

    @Override
    public String getReasonTextTemplate() {
        return beskrivelse;
    }

    @Override
    public String getReasonCode() {
        return "" + årsakKode;
    }
}
