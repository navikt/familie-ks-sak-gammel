package no.nav.familie.ks.sak.app.behandling.resultat.årsak;

public enum VilkårIkkeOppfyltÅrsak implements VilkårÅrsak {

    IKKE_FEM_ÅR_MEDLEMSKAP(8304, "Må ha vært medlem av den norske folketrygden i minst fem år"),
    HAR_SVART_TRE_MÅNEDER_UTLAND(8309, "Kan ikke oppholde seg tre måneder eller mer i utlandet i løpet av de neste tolv månedene "),
    FORELDRE_HAR_AVTALT_DELT_BOSTED(8310, "Foreldre kan ikke ha avtalt delt bosted for barnet"),
    FORELDER_BOR_IKKE_MED_BARN(8310, "Søker må bo sammen med barnet det søkes kontantstøtte for"),
    IKKE_GYLDIG_ALDER(8310, "Barn det søkes kontantstøtte for er ikke i gyldig kontantstøttealder");

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
