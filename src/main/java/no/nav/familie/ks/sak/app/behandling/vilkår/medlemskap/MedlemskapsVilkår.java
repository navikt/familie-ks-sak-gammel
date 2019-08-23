package no.nav.familie.ks.sak.app.behandling.vilkår.medlemskap;

import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.VilkårType;
import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.behandling.resultat.årsak.VilkårIkkeOppfyltÅrsak;
import no.nav.familie.ks.sak.app.behandling.vilkår.InngangsvilkårRegel;
import no.nav.familie.ks.sak.app.behandling.vilkår.Sluttpunkt;
import no.nav.familie.ks.sak.app.behandling.vilkår.medlemskap.regel.HattNorskStatsborgerskapFemÅr;
import no.nav.familie.ks.sak.app.behandling.vilkår.medlemskap.regel.HarVærtBosattFemÅrINorge;
import no.nav.fpsak.nare.Ruleset;
import no.nav.fpsak.nare.doc.RuleDocumentation;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.specification.Specification;
import org.springframework.stereotype.Component;

@Component
@RuleDocumentation(VilkårType.Constants.MEDLEMSKAP_KODE)
public class MedlemskapsVilkår implements InngangsvilkårRegel<Faktagrunnlag> {

    @Override
    public VilkårType getVilkårType() {
        return VilkårType.MEDLEMSKAP;
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
        return rs.hvisRegel(HarVærtBosattFemÅrINorge.ID, "Vurder om søker har vært bosatt i Norge siste fem år")
                .hvis(new HarVærtBosattFemÅrINorge(),
                        rs.hvisRegel(HattNorskStatsborgerskapFemÅr.ID, "Vurder om foreldre har vært norske statsborgere siste fem år")
                                .hvis(new HattNorskStatsborgerskapFemÅr(), Sluttpunkt.oppfylt())
                                .ellers(Sluttpunkt.ikkeOppfylt(VilkårIkkeOppfyltÅrsak.IKKE_NORSKE_STATSBORGERE_FEM_ÅR)))
                .ellers(Sluttpunkt.ikkeOppfylt(VilkårIkkeOppfyltÅrsak.IKKE_BOSATT_I_NORGE_FEM_ÅR));

    }
}
