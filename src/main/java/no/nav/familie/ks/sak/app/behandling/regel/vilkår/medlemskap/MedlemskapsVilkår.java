package no.nav.familie.ks.sak.app.behandling.regel.vilkår.medlemskap;

import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.VilkårType;
import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.årsak.VilkårIkkeOppfyltÅrsak;
import no.nav.familie.ks.sak.app.behandling.regel.vilkår.medlemskap.regel.HarIngenMedlemskapsopplysninger;
import no.nav.familie.ks.sak.app.behandling.vilkår.InngangsvilkårRegel;
import no.nav.familie.ks.sak.app.behandling.vilkår.Sluttpunkt;
import no.nav.familie.ks.sak.app.behandling.regel.vilkår.medlemskap.regel.HarNorskStatsborgerskap;
import no.nav.familie.ks.sak.app.behandling.regel.vilkår.medlemskap.regel.HarVærtBosattFemÅrINorge;
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

    private Ruleset<Faktagrunnlag> rs = new Ruleset<Faktagrunnlag>();


    @Override
    @SuppressWarnings("unchecked")
    public Specification<Faktagrunnlag> getSpecification() {
        return rs.hvisRegel(HarVærtBosattFemÅrINorge.ID, "Vurder om søker har vært bosatt i Norge siste fem år")
                .hvis(new HarVærtBosattFemÅrINorge(), erNorskStatsborger())
                .ellers(Sluttpunkt.ikkeOppfylt(VilkårIkkeOppfyltÅrsak.IKKE_BOSATT_I_NORGE_FEM_ÅR));
    }

    @SuppressWarnings("unchecked")
    private Specification<Faktagrunnlag> erNorskStatsborger() {
        return rs.hvisRegel(HarNorskStatsborgerskap.ID, "Vurder om foreldrene er norske statsborgere")
                .hvis(new HarNorskStatsborgerskap(), sjekkOmIngenMedlemskapsInfo())
                .ellers(Sluttpunkt.ikkeOppfylt(VilkårIkkeOppfyltÅrsak.IKKE_NORSKE_STATSBORGERE));
    }

    @SuppressWarnings("unchecked")
    private Specification<Faktagrunnlag> sjekkOmIngenMedlemskapsInfo() {
        return rs.hvisRegel(HarIngenMedlemskapsopplysninger.ID, "Har foreldrene ingen medlemskapsopplysninger?")
            .hvis(new HarIngenMedlemskapsopplysninger(), Sluttpunkt.oppfylt())
            .ellers(Sluttpunkt.ikkeOppfylt(VilkårIkkeOppfyltÅrsak.HAR_MEDLEMSKAPSOPPLYSNINGER));
    }
}
