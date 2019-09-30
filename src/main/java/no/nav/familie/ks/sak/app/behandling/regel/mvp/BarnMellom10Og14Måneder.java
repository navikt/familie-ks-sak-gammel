package no.nav.familie.ks.sak.app.behandling.regel.mvp;

import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.VilkårType;
import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.årsak.VilkårIkkeOppfyltÅrsak;
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.årsak.VilkårOppfyltÅrsak;
import no.nav.familie.ks.sak.app.behandling.vilkår.InngangsvilkårRegel;
import no.nav.fpsak.nare.Ruleset;
import no.nav.fpsak.nare.doc.RuleDocumentation;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.specification.LeafSpecification;
import no.nav.fpsak.nare.specification.Specification;
import org.springframework.stereotype.Component;

@Component
@RuleDocumentation(VilkårType.Constants.BARN_MELLOM_10_OG_14_MÅNEDER)
public class BarnMellom10Og14Måneder implements InngangsvilkårRegel<Faktagrunnlag> {
    
    class Regel extends LeafSpecification<Faktagrunnlag> {
        public Regel() {
            super(VilkårType.BARN_MELLOM_10_OG_14_MÅNEDER.toString());
        }

        public Evaluation evaluate(Faktagrunnlag faktagrunnlag) {
            var behandlingsdato = faktagrunnlag.getBehandlingstidspunkt();
            var fødselsdato = faktagrunnlag.getTpsFakta().getBarna().get(0).getPersoninfo().getFødselsdato();
            var tiMånedersDato = fødselsdato.plusMonths(10);
            var fjortenMånedersDato = fødselsdato.plusMonths(14);
            if ((behandlingsdato.isEqual(tiMånedersDato) || behandlingsdato.isAfter(tiMånedersDato)) && 
                behandlingsdato.isBefore(fjortenMånedersDato)) {
                return ja(VilkårOppfyltÅrsak.VILKÅR_OPPFYLT);
            } else {
                return nei(VilkårIkkeOppfyltÅrsak.BARN_IKKE_MELLOM_10_OG_14_MÅNEDER);
            }
        }
    }
    
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
        var rs = new Ruleset<Faktagrunnlag>();
        
        return rs.regel(VilkårType.BARN_MELLOM_10_OG_14_MÅNEDER.toString(), new Regel());
    }
}