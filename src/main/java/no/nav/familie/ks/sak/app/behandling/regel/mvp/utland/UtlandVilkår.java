package no.nav.familie.ks.sak.app.behandling.regel.mvp.utland;

import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.VilkårType;
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.årsak.VilkårIkkeOppfyltÅrsak;
import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.behandling.vilkår.InngangsvilkårRegel;
import no.nav.familie.ks.sak.app.behandling.vilkår.Sluttpunkt;
import no.nav.familie.ks.sak.app.behandling.regel.mvp.utland.regel.IkkeOppgittTilknytningUtland;
import no.nav.fpsak.nare.Ruleset;
import no.nav.fpsak.nare.doc.RuleDocumentation;
import no.nav.fpsak.nare.evaluation.Evaluation;
import no.nav.fpsak.nare.specification.Specification;
import org.springframework.stereotype.Component;

@Component
@RuleDocumentation(VilkårType.Constants.UTLAND_KODE)
public class UtlandVilkår implements InngangsvilkårRegel<Faktagrunnlag> {

    @Override
    public VilkårType getVilkårType() {
        return VilkårType.OPPGITT_TILKNYTNING_TIL_UTLAND;
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
        return rs.hvisRegel(IkkeOppgittTilknytningUtland.ID, "Vurder om søker ikke har tilknytning til utland (MVP)")
                    .hvis(new IkkeOppgittTilknytningUtland(), Sluttpunkt.oppfylt())
                    .ellers(Sluttpunkt.ikkeOppfylt(VilkårIkkeOppfyltÅrsak.OPPGITT_TILKNYTNING_UTLAND));
    }
}
