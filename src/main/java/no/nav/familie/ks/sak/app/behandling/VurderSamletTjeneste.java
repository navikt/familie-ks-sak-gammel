package no.nav.familie.ks.sak.app.behandling;

import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.behandling.vilkår.InngangsvilkårRegel;
import no.nav.familie.ks.sak.app.behandling.vilkår.VilkårType;
import no.nav.fpsak.nare.evaluation.Evaluation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VurderSamletTjeneste {

    private List<InngangsvilkårRegel> inngangsvilkår;

    @Autowired
    public VurderSamletTjeneste(List<InngangsvilkårRegel> inngangsvilkår) {
        this.inngangsvilkår = inngangsvilkår;
    }

    @SuppressWarnings("unchecked")
    public SamletVilkårsVurdering vurder(Faktagrunnlag grunnlag) {
        final Map<VilkårType, Evaluation> vurderinger = new HashMap<>();
        inngangsvilkår.forEach(vilkår -> {
            final var input = vilkår.konverterInput(grunnlag);
            final var evaluering = vilkår.evaluer(input);
            if (vurderinger.containsKey(vilkår.getVilkårType())) {
                throw new IllegalStateException("Fant flere inngangsvilkår med samme kode");
            }
            vurderinger.put(vilkår.getVilkårType(), evaluering);
        });

        return new SamletVilkårsVurdering(vurderinger, grunnlag);
    }
}
