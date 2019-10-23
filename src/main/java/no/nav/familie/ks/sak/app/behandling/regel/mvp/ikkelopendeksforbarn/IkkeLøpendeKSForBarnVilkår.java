package no.nav.familie.ks.sak.app.behandling.regel.mvp.ikkelopendeksforbarn;

import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.VilkårType;
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.årsak.VilkårIkkeOppfyltÅrsak;
import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.behandling.vilkår.InngangsvilkårRegel;
import no.nav.familie.ks.sak.app.behandling.vilkår.Sluttpunkt;
import no.nav.familie.ks.sak.app.behandling.regel.mvp.ikkelopendeksforbarn.regel.IkkeLøpendeKSForBarn;
import no.nav.fpsak.nare.Ruleset;
import no.nav.fpsak.nare.doc.RuleDocumentation;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.specification.Specification;
import org.springframework.stereotype.Component;

@Component
@RuleDocumentation(VilkårType.Constants.IKKE_LØPENDE_KS_FOR_BARN)
public class IkkeLøpendeKSForBarnVilkår implements InngangsvilkårRegel<Faktagrunnlag> {
    
    @Override
    public VilkårType getVilkårType() {
        return VilkårType.IKKE_LØPENDE_KS_FOR_BARN;
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
        return rs.hvisRegel(IkkeLøpendeKSForBarn.ID, "Sjekk om barnet har en løpende kontantstøtte.")
                    .hvis(new IkkeLøpendeKSForBarn(), Sluttpunkt.oppfylt())
                    .ellers(Sluttpunkt.ikkeOppfylt(VilkårIkkeOppfyltÅrsak.BARN_MOTTAR_KONTANTSTØTTE_ELLER_HAR_SAK_UNDER_BEHANDLING));
    }
}