package no.nav.familie.ks.sak.app.behandling.vilkår.bosted;

import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.behandling.resultat.årsak.VilkårIkkeOppfyltÅrsak;
import no.nav.familie.ks.sak.app.behandling.resultat.årsak.VilkårOppfyltÅrsak;
import no.nav.familie.ks.sak.app.behandling.vilkår.InngangsvilkårRegel;
import no.nav.familie.ks.sak.app.behandling.vilkår.Sluttpunkt;
import no.nav.familie.ks.sak.app.behandling.vilkår.VilkårType;
import no.nav.familie.ks.sak.app.behandling.vilkår.bosted.regel.BosattSammen;
import no.nav.fpsak.nare.Ruleset;
import no.nav.fpsak.nare.doc.RuleDocumentation;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.specification.Specification;
import org.springframework.stereotype.Component;

@Component
@RuleDocumentation(VilkårType.Constants.BOSTED_KODE)
public class BostedVilkår implements InngangsvilkårRegel<Faktagrunnlag> {

    @Override
    public VilkårType getVilkårType() {
        return VilkårType.BOSTED;
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
        return rs.hvisRegel(BosattSammen.ID, "Vurder om barnet bor sammen med forelder/foreldre")
                .hvis(new BosattSammen(), Sluttpunkt.oppfylt("BOSTED-INNVILGET-1", VilkårOppfyltÅrsak.VILKÅR_OPPFYLT))
                .ellers(Sluttpunkt.ikkeOppfylt("BOSTED-AVSLAG-1", VilkårIkkeOppfyltÅrsak.IKKE_BOSATT_SAMNEN));
    }
}
