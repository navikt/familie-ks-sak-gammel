package no.nav.familie.ks.sak.app.behandling.vilkår.medlemskap.regel;

import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.behandling.resultat.årsak.VilkårIkkeOppfyltÅrsak;
import no.nav.familie.ks.sak.app.grunnlag.TpsFakta;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.Personinfo;
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
        TpsFakta tpsFakta = grunnlag.getTpsFakta();
        Personinfo forelder = tpsFakta.getForelder().getPersoninfo();
        Personinfo barn = tpsFakta.getBarn();
        Personinfo annenForelder = tpsFakta.getAnnenForelder() != null
                ? tpsFakta.getAnnenForelder().getPersoninfo()
                : null;
        if (norskStatsborger(forelder) && norskStatsborger(barn) && norskStatsborger(annenForelder)) {
            return ja();
        }
        return nei(VilkårIkkeOppfyltÅrsak.IKKE_NORSKE_STATSBORGERE_FEM_ÅR);
    }

    private boolean norskStatsborger(Personinfo person) {
        if (person == null) {
            return false;
        }
        return person.getStatsborgerskap().erNorge();
    }
}
