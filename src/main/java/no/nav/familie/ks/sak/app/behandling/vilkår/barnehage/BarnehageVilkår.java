package no.nav.familie.ks.sak.app.behandling.vilkår.barnehage;

import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.behandling.resultat.årsak.VilkårIkkeOppfyltÅrsak;
import no.nav.familie.ks.sak.app.behandling.resultat.årsak.VilkårOppfyltÅrsak;
import no.nav.familie.ks.sak.app.behandling.vilkår.InngangsvilkårRegel;
import no.nav.familie.ks.sak.app.behandling.vilkår.Sluttpunkt;
import no.nav.familie.ks.sak.app.behandling.vilkår.VilkårType;
import no.nav.familie.ks.sak.app.behandling.vilkår.barnehage.regel.GarkkeIBarnehage;
import no.nav.fpsak.nare.Ruleset;
import no.nav.fpsak.nare.doc.RuleDocumentation;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.specification.Specification;
import org.springframework.stereotype.Component;

@Component
@RuleDocumentation(VilkårType.Constants.BARNEHAGE_KODE)
public class BarnehageVilkår implements InngangsvilkårRegel<Faktagrunnlag> {

    @Override
    public VilkårType getVilkårType() {
        return VilkårType.BARNEHAGE;
    }

    @Override
    public Faktagrunnlag konverterInput(Faktagrunnlag faktagrunnlag) {
        return faktagrunnlag;
    }

    @Override
    public Evaluation evaluer(Faktagrunnlag input) {
        return getSpecification().evaluate(input);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Specification<Faktagrunnlag> getSpecification() {
        final var rs = new Ruleset<Faktagrunnlag>();
        return rs.hvisRegel(GarkkeIBarnehage.ID, "Vurder om barnet har barnehageplass")
                    .hvis(new GarkkeIBarnehage(), Sluttpunkt.oppfylt(getVilkårType() + "-INNVILGET-1", VilkårOppfyltÅrsak.VILKÅR_OPPFYLT))
                    .ellers(Sluttpunkt.ikkeOppfylt(getVilkårType() + "-AVSLAG-1", VilkårIkkeOppfyltÅrsak.BARNEHAGEPLASS));
    }
}
