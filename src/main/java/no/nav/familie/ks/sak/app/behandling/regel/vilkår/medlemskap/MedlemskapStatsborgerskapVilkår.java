package no.nav.familie.ks.sak.app.behandling.regel.vilkår.medlemskap;

import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.VilkårType;
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.årsak.VilkårIkkeOppfyltÅrsak;
import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.behandling.regel.vilkår.medlemskap.regel.HarNorskStatsborgerskap;
import no.nav.familie.ks.sak.app.behandling.vilkår.InngangsvilkårRegel;
import no.nav.familie.ks.sak.app.behandling.vilkår.Sluttpunkt;
import no.nav.fpsak.nare.Ruleset;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.specification.Specification;

public class MedlemskapStatsborgerskapVilkår implements InngangsvilkårRegel<Faktagrunnlag> {


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
    public <V> Specification<V> getSpecification() {
        final var rs = new Ruleset<Faktagrunnlag>();
        return rs.hvisRegel(HarNorskStatsborgerskap.ID, "Vurder om foreldrene er norske statsborgere")
            .hvis(new HarNorskStatsborgerskap(), Sluttpunkt.oppfylt())
            .ellers(Sluttpunkt.ikkeOppfylt(VilkårIkkeOppfyltÅrsak.IKKE_NORSKE_STATSBORGERE));    }
}
