package no.nav.familie.ks.sak.app.behandling.domene.kodeverk.årsak;

import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.VilkårType;

public enum VilkårIkkeOppfyltÅrsak implements VilkårUtfallÅrsak {

    // Medlemskap
    IKKE_FEM_ÅR_MEDLEMSKAP(8301, "Må ha vært medlem av den norske folketrygden i minst fem år", VilkårType.MEDLEMSKAP),
    IKKE_BOSATT_I_NORGE_FEM_ÅR(8303, "Ikke vært bosatt i Norge sammenhengende siste fem år", VilkårType.MEDLEMSKAP),
    IKKE_NORSKE_STATSBORGERE(8304, "Foreldre er ikke norsk statsborger nå", VilkårType.MEDLEMSKAP),
    HAR_MEDLEMSKAPSOPPLYSNINGER(8304, "Foreldre har medlemskapsopplysninger", VilkårType.MEDLEMSKAP),


    // §1: Formålet med loven (å bidra til at familiene får mer tid til selv å ta omsorgen for egne barn)
    BARNEHAGEPLASS(8302, "Søknaden inneholder barnehageplass", VilkårType.BARNEHAGE),

    // §2: Vilkår knyttet til barnet
    BARN_IKKE_NORSK_STATSBORGER(8307, "Barn er ikke norsk statsborger", VilkårType.BARN),

    // §3: Vilkår knyttet til støttemottaker
    IKKE_BEGGE_FORELDRE(8307, "Begge foreldre er ikke registrert på barn i TPS", VilkårType.BOSTED),
    IKKE_BOSATT_SAMMEN(8306, "Barn er ikke bosatt sammen med begge foreldre", VilkårType.BOSTED),

    // MVP
    OPPGITT_TILKNYTNING_UTLAND(8309, "Søker har svart ja på spørsmål som indikerer tilknytning til utland", VilkårType.UTLAND),
    BARN_IKKE_MELLOM_10_OG_14_MÅNEDER(8310, "Barnet det søkes for er ikke mellom 10 og 14 måneder", VilkårType.BARN_MELLOM_10_OG_14_MÅNEDER),
    OPPGITT_FLERE_BARN(8311, "Søker har oppgitt flere barn i søknaden", VilkårType.KUN_ET_BARN);

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
