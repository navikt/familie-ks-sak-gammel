package no.nav.familie.ks.sak.app.behandling.regel.vilkår.medlemskapBostedNå.regel;

import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.Landkode;
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.årsak.VilkårIkkeOppfyltÅrsak;
import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.grunnlag.PersonMedHistorikk;
import no.nav.fpsak.nare.doc.RuleDocumentation;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.specification.LeafSpecification;

@RuleDocumentation(ErBosattINorgeNå.ID)
public class ErBosattINorgeNå extends LeafSpecification<Faktagrunnlag> {

    public static final String ID = "KS-MEDL-4";

    public ErBosattINorgeNå() {
        super(ID);
    }

    @Override
    public Evaluation evaluate(Faktagrunnlag faktagrunnlag) {
        PersonMedHistorikk søker = faktagrunnlag.getTpsFakta().getForelder();
        PersonMedHistorikk annenForelder = faktagrunnlag.getTpsFakta().getAnnenForelder();

            if (annenForelder == null) {
                if (erBosattINorge(søker)) {
                    return ja();
                }
            } else {
                if (erBosattINorge(søker) && erBosattINorge(annenForelder)) {
                    return ja();
                }
            }
            return nei(VilkårIkkeOppfyltÅrsak.IKKE_BOSATT_I_NORGE_NÅ);
    }

    private boolean erBosattINorge(PersonMedHistorikk forelder) {
        return Landkode.NORGE.getKode().matches(forelder.getPersoninfo().getBostedsadresse().getLand());
    }

}
