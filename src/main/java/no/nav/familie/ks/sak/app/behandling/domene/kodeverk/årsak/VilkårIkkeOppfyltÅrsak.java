package no.nav.familie.ks.sak.app.behandling.domene.kodeverk.årsak;

import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.VilkårType;

public enum VilkårIkkeOppfyltÅrsak implements VilkårUtfallÅrsak {

    // Medlemskap
    IKKE_BOSATT_I_NORGE_FEM_ÅR(8303, "Sammenlagt botid i Norge er under 5 år", VilkårType.MEDLEMSKAP_BOSTED),
    IKKE_BOSATT_I_NORGE_NÅ(8308, "Ikke bosatt i Norge nå", VilkårType.MEDLEMSKAP_BOSTED_NÅ),
    IKKE_NORSKE_STATSBORGERE(8304, "Foreldre er ikke norsk statsborger nå", VilkårType.MEDLEMSKAP_STATSBORGERSKAP),
    HAR_MEDLEMSKAPSOPPLYSNINGER(8305, "Foreldre har medlemskapsopplysninger", VilkårType.MEDLEMSKAP_MEDL),


    // §1: Formålet med loven (å bidra til at familiene får mer tid til selv å ta omsorgen for egne barn)
    BARNEHAGEPLASS(8302, "Søknaden inneholder barnehageplass", VilkårType.BARNEHAGE),

    // §2: Vilkår knyttet til barnet
    BARN_IKKE_NORSK_STATSBORGER(8307, "Barn er ikke norsk statsborger", VilkårType.BARN_NORSK_STATSBORGER),

    // §3: Vilkår knyttet til støttemottaker
    IKKE_BEGGE_FORELDRE(8314, "Begge foreldre er ikke registrert på barn i TPS", VilkårType.BARN_BOR_MED_FORELDRE),
    IKKE_BOSATT_SAMMEN(8306, "Barn er ikke bosatt sammen med begge foreldre", VilkårType.BARN_BOR_MED_FORELDRE),

    // MVP
    OPPGITT_TILKNYTNING_UTLAND(8309, "Søker har svart ja på spørsmål som indikerer tilknytning til utland", VilkårType.OPPGITT_TILKNYTNING_TIL_UTLAND),
    BARN_IKKE_MELLOM_10_OG_27_MÅNEDER(8310, "Barnet det søkes for er ikke mellom 10 og 14 måneder", VilkårType.BARN_MELLOM_10_OG_27_MÅNEDER),
    OPPGITT_FLERE_BARN(8311, "Søker har oppgitt flere barn i søknaden", VilkårType.KUN_ET_BARN),
    ANNEN_PART_STEMMER_IKKE(8312, "Oppgitt annen part fra søknaden og annen part fra TPS stemmer ikke", VilkårType.ANNEN_PART_STEMMER),
    ANNEN_PART_ER_IKKE_OPPGITT(8313, "Annen part er ikke tilstedet i grunnlaget fra TPS", VilkårType.ANNEN_PART_ER_OPPGITT),
    BARN_MOTTAR_KONTANTSTØTTE_ELLER_HAR_SAK_UNDER_BEHANDLING(8315, "Barnet er mottaker av kontantstøtte eller har en sak for kontantstøtte under behandling", VilkårType.IKKE_LØPENDE_KS_FOR_BARN);

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
