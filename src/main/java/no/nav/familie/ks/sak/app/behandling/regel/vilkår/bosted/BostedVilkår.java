package no.nav.familie.ks.sak.app.behandling.regel.vilkår.bosted;

import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.VilkårType;
import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.årsak.VilkårIkkeOppfyltÅrsak;
import no.nav.familie.ks.sak.app.behandling.vilkår.InngangsvilkårRegel;
import no.nav.familie.ks.sak.app.behandling.vilkår.Sluttpunkt;
import no.nav.familie.ks.sak.app.behandling.regel.vilkår.bosted.regel.ErBarnBosattMedForeldre;
import no.nav.familie.ks.sak.app.behandling.regel.vilkår.bosted.regel.HarRelasjonTilBeggeForeldre;
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
        return VilkårType.BARN_BOR_MED_FORELDRE;
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
        return rs.hvisRegel(HarRelasjonTilBeggeForeldre.ID, "Vurder om barnet har relasjon til begge foreldre (MVP)")
                    .hvis(new HarRelasjonTilBeggeForeldre(),
                        rs.hvisRegel(ErBarnBosattMedForeldre.ID, "Vurder om barnet bor sammen med foreldre")
                            .hvis(new ErBarnBosattMedForeldre(), Sluttpunkt.oppfylt())
                            .ellers(Sluttpunkt.ikkeOppfylt(VilkårIkkeOppfyltÅrsak.IKKE_BOSATT_SAMMEN)))
                    .ellers(Sluttpunkt.ikkeOppfylt(VilkårIkkeOppfyltÅrsak.IKKE_BEGGE_FORELDRE));
    }
}
