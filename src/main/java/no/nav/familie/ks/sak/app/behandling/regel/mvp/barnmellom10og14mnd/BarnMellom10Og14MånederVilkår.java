package no.nav.familie.ks.sak.app.behandling.regel.mvp.barnmellom10og14mnd;

import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.VilkårType;
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.årsak.VilkårIkkeOppfyltÅrsak;
import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.behandling.vilkår.InngangsvilkårRegel;
import no.nav.familie.ks.sak.app.behandling.vilkår.Sluttpunkt;
import no.nav.familie.ks.sak.app.behandling.regel.mvp.barnmellom10og14mnd.regel.BarnMellom10Og14Måneder;
import no.nav.fpsak.nare.Ruleset;
import no.nav.fpsak.nare.doc.RuleDocumentation;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.specification.Specification;
import org.springframework.stereotype.Component;

@Component
@RuleDocumentation(VilkårType.Constants.BARN_MELLOM_10_OG_14_MÅNEDER_KODE)
public class BarnMellom10Og14MånederVilkår implements InngangsvilkårRegel<Faktagrunnlag> {
    
    @Override
    public VilkårType getVilkårType() {
        return VilkårType.BARN_MELLOM_10_OG_14_MÅNEDER;
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
        return rs.hvisRegel(BarnMellom10Og14Måneder.ID, "Vurder om søker ikke har tilknytning til utland (MVP)")
                    .hvis(new BarnMellom10Og14Måneder(), Sluttpunkt.oppfylt())
                    .ellers(Sluttpunkt.ikkeOppfylt(VilkårIkkeOppfyltÅrsak.BARN_IKKE_MELLOM_10_OG_14_MÅNEDER));
    }
}