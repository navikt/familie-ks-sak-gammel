package no.nav.familie.ks.sak.app.behandling.vilkår.medlemskap;

import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.behandling.resultat.årsak.VilkårIkkeOppfyltÅrsak;
import no.nav.familie.ks.sak.app.behandling.resultat.årsak.VilkårOppfyltÅrsak;
import no.nav.familie.ks.sak.app.behandling.vilkår.InngangsvilkårRegel;
import no.nav.familie.ks.sak.app.behandling.vilkår.Sluttpunkt;
import no.nav.familie.ks.sak.app.behandling.vilkår.medlemskap.regel.HarVærtBosattFemÅrINorge;
import no.nav.familie.ks.sak.app.behandling.vilkår.medlemskap.regel.MinstEnErNorskStatsborger;
import no.nav.fpsak.nare.RuleService;
import no.nav.fpsak.nare.Ruleset;
import no.nav.fpsak.nare.doc.RuleDocumentation;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.specification.Specification;
import org.springframework.stereotype.Component;

@Component
@RuleDocumentation(MedlemskapsVilkår.ID)
public class MedlemskapsVilkår implements RuleService<Faktagrunnlag>, InngangsvilkårRegel {

    public static final String ID = "KS-MEDL";

    @Override
    public Evaluation evaluer(Faktagrunnlag input) {
        return getSpecification().evaluate(input);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Specification<Faktagrunnlag> getSpecification() {
        final var rs = new Ruleset<Faktagrunnlag>();
        return rs.hvisRegel(HarVærtBosattFemÅrINorge.ID, "Vurder om søker har vært bosatt i fem år")
                .hvis(new HarVærtBosattFemÅrINorge(),
                        rs.hvisRegel(MinstEnErNorskStatsborger.ID, "Vurder om minst en av søker eller annen part er norsk statsborger")
                                .hvis(new MinstEnErNorskStatsborger(), Sluttpunkt.oppfylt("MEDL-INNVILGET-1", VilkårOppfyltÅrsak.VILKÅR_OPPFYLT))
                                .ellers(Sluttpunkt.ikkeOppfylt("MEDL-AVSLAG-1", VilkårIkkeOppfyltÅrsak.IKKE_BOSATT_I_NORGE_FEM_ÅR)))
                .ellers(Sluttpunkt.ikkeOppfylt("MEDL-AVSLAG-2", VilkårIkkeOppfyltÅrsak.IKKE_BOSATT_I_NORGE_FEM_ÅR));
    }
}
