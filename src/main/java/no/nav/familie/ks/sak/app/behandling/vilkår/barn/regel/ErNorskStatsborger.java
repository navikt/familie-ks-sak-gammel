package no.nav.familie.ks.sak.app.behandling.vilkår.barn.regel;

import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.behandling.resultat.årsak.VilkårIkkeOppfyltÅrsak;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.tilhørighet.Landkode;
import no.nav.fpsak.nare.doc.RuleDocumentation;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.specification.LeafSpecification;

@RuleDocumentation(ErNorskStatsborger.ID)
public class ErNorskStatsborger extends LeafSpecification<Faktagrunnlag> {

    public static final String ID = "BARN-2";

    public ErNorskStatsborger() {
        super(ID);
    }

    @Override
    public Evaluation evaluate(Faktagrunnlag grunnlag) {
        Landkode statsborgerskap = grunnlag.getTpsFakta().getBarn().getStatsborgerskap();
        return statsborgerskap.erNorge() ? ja() : nei(VilkårIkkeOppfyltÅrsak.BARN_IKKE_NORSK_STATSBORGER);
    }
}
