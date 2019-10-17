package no.nav.familie.ks.sak.app.behandling.regel.vilkår.medlemskapMedl.regel;

import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.årsak.VilkårIkkeOppfyltÅrsak;
import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.grunnlag.MedlFakta;
import no.nav.familie.ks.sak.app.grunnlag.PersonMedHistorikk;
import no.nav.fpsak.nare.doc.RuleDocumentation;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.specification.LeafSpecification;

@RuleDocumentation(HarIngenMedlemskapsopplysninger.ID)
public class HarIngenMedlemskapsopplysninger extends LeafSpecification<Faktagrunnlag> {

    public static final String ID = "KS-MEDL-3";

    public HarIngenMedlemskapsopplysninger() {
        super(ID);
    }

    @Override
    public Evaluation evaluate(Faktagrunnlag faktagrunnlag) {
        MedlFakta medlFakta = faktagrunnlag.getMedlFakta();
        PersonMedHistorikk annenForelder = faktagrunnlag.getTpsFakta().getAnnenForelder();

        if (annenForelder == null) {
            if (medlFakta.getSøker().isEmpty()) {
                return ja();
            }
        } else {
            if (medlFakta.getSøker().isEmpty() && medlFakta.getAnnenForelder().isEmpty()) {
                return ja();
            }
        }

        return nei(VilkårIkkeOppfyltÅrsak.HAR_MEDLEMSKAPSOPPLYSNINGER);
    }

}
