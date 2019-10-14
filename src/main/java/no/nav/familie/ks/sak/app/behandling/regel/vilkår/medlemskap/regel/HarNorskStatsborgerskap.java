package no.nav.familie.ks.sak.app.behandling.regel.vilkår.medlemskap.regel;

import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.årsak.VilkårIkkeOppfyltÅrsak;
import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.grunnlag.PersonMedHistorikk;
import no.nav.fpsak.nare.doc.RuleDocumentation;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.specification.LeafSpecification;

@RuleDocumentation(HarNorskStatsborgerskap.ID)
public class HarNorskStatsborgerskap extends LeafSpecification<Faktagrunnlag> {

    public static final String ID = "MEDL-2";

    public HarNorskStatsborgerskap() {
        super(ID);
    }

    @Override
    public Evaluation evaluate(Faktagrunnlag grunnlag) {
        PersonMedHistorikk forelder = grunnlag.getTpsFakta().getForelder();
        PersonMedHistorikk annenForelder = grunnlag.getTpsFakta().getAnnenForelder();
        if (norskStatsborger(forelder) && norskStatsborger(annenForelder)) {
            return ja();
        }
        return nei(VilkårIkkeOppfyltÅrsak.IKKE_NORSKE_STATSBORGERE);
    }

    private boolean norskStatsborger(PersonMedHistorikk forelder) {
        if (forelder == null) {
            return false;
        }
        return forelder.getPersoninfo().getStatsborgerskap().erNorge();

    }
}
