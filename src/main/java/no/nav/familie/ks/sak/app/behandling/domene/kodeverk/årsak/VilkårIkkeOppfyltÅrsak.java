package no.nav.familie.ks.sak.app.behandling.domene.kodeverk.årsak;

import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.VilkårType;

public enum VilkårIkkeOppfyltÅrsak implements VilkårUtfallÅrsak {

    // Medlemskap
    IKKE_FEM_ÅR_MEDLEMSKAP(8301, "Må ha vært medlem av den norske folketrygden i minst fem år", VilkårType.MEDLEMSKAP),
    IKKE_BOSATT_I_NORGE_FEM_ÅR(8303, "Ikke vært bosatt i Norge sammenhengende siste fem år", VilkårType.MEDLEMSKAP),
    IKKE_NORSKE_STATSBORGERE_FEM_ÅR(8304, "Foreldre har ikke hatt norsk statsborgerskap sammenhegende siste fem år", VilkårType.MEDLEMSKAP),

    // Bosted?! Er ikke dette også medlemskap?
    IKKE_BEGGE_FORELDRE(8307, "Begge foreldre er ikke registrert på barn i TPS", VilkårType.BOSTED),
    IKKE_BOSATT_SAMMEN(8306, "Barn er ikke bosatt sammen med begge foreldre", VilkårType.BOSTED),

    // Retten til?
    BARNEHAGEPLASS(8302, "Søknaden inneholder barnehageplass", VilkårType.BARNEHAGE),

    // Barn
    BARN_IKKE_NORSK_STATSBORGER(8307, "Barn er ikke norsk statsborger", VilkårType.BARN);

    private final int årsakKode;
    private final String beskrivelse;
    private final VilkårType vilkårType;

    VilkårIkkeOppfyltÅrsak(int årsakKode, String beskrivelse, VilkårType vilkårType) {
        this.årsakKode = årsakKode;
        this.beskrivelse = beskrivelse;
        this.vilkårType = vilkårType;
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
    public VilkårType getVilkårType() {
        return vilkårType;
    }

    @Override
    public String getReasonTextTemplate() {
        return beskrivelse;
    }

    @Override
    public String getReasonCode() {
        return "" + årsakKode;
    }

    @Override
    public String toString() {
        return "VilkårIkkeOppfyltÅrsak{" +
                "ref=" + name() +
                "årsakKode=" + årsakKode +
                ", vilkårType=" + vilkårType +
                '}';
    }
}
