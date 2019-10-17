package no.nav.familie.ks.sak.app.behandling.regel.vilkår.medlemskapMedl;

import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.VilkårType;
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.årsak.VilkårIkkeOppfyltÅrsak;
import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.behandling.regel.vilkår.medlemskapMedl.regel.HarIngenMedlemskapsopplysninger;
import no.nav.familie.ks.sak.app.behandling.vilkår.InngangsvilkårRegel;
import no.nav.familie.ks.sak.app.behandling.vilkår.Sluttpunkt;
import no.nav.fpsak.nare.Ruleset;
import no.nav.fpsak.nare.doc.RuleDocumentation;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.specification.Specification;
import org.springframework.stereotype.Component;

@Component
@RuleDocumentation(VilkårType.Constants.MEDLEMSKAP_MEDL_KODE)
public class MedlemskapMedlVilkår implements InngangsvilkårRegel<Faktagrunnlag> {

    @Override
    public VilkårType getVilkårType() {
        return VilkårType.MEDLEMSKAP_MEDL;
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
    public <V> Specification<V> getSpecification() {
        final var rs = new Ruleset<Faktagrunnlag>();
        return rs.hvisRegel(HarIngenMedlemskapsopplysninger.ID, "Har foreldrene ingen medlemskapsopplysninger?")
            .hvis(new HarIngenMedlemskapsopplysninger(), Sluttpunkt.oppfylt())
            .ellers(Sluttpunkt.ikkeOppfylt(VilkårIkkeOppfyltÅrsak.HAR_MEDLEMSKAPSOPPLYSNINGER));    }
}
