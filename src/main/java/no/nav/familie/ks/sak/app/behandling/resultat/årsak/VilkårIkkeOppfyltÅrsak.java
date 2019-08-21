package no.nav.familie.ks.sak.app.behandling.resultat.årsak;

public enum VilkårIkkeOppfyltÅrsak implements VilkårÅrsak {

    IKKE_FEM_ÅR_MEDLEMSKAP(8301, "Må ha vært medlem av den norske folketrygden i minst fem år"),
    GRADERT_BARNEHAGEPLASS(8302, "Søknaden inneholder gradert barnehageplass"),
    IKKE_BOSATT_I_NORGE_FEM_ÅR(8303, "Ikke bosatt vært bosatt fem år i norge"),
    BARN_UTENFOR_KONTANTSTØTTE_ALDER(8305, "Barn utenfor kontantstøtte alder"),
    IKKE_NORSK_STATSBORGER(8304, "Ikke norsk statsborger"),
    IKKE_BEGGE_FORELDRE(8307, "Begge foreldre er ikke registrert på barn i TPS"),
    IKKE_BOSATT_SAMMEN(8306, "Barn er ikke bosatt sammen med begge foreldre");

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
