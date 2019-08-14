package no.nav.familie.ks.sak.app.behandling;

import no.nav.familie.ks.sak.app.behandling.fastsetting.Faktagrunnlag;
import no.nav.familie.ks.sak.app.behandling.vilkår.InngangsvilkårRegel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VurderSamletTjeneste {

    private List<InngangsvilkårRegel> inngangsvilkår;

    @Autowired
    public VurderSamletTjeneste(List<InngangsvilkårRegel> inngangsvilkår) {
        this.inngangsvilkår = inngangsvilkår;
    }

    public SamletVilkårsVurdering vurder(Faktagrunnlag grunnlag) {
        final var vurderinger = inngangsvilkår.stream()
                .map(vilkår -> vilkår.evaluer(grunnlag))
                .collect(Collectors.toList());

        return new SamletVilkårsVurdering(vurderinger, grunnlag);
    }
}
