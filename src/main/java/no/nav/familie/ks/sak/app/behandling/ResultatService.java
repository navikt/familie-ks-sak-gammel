package no.nav.familie.ks.sak.app.behandling;

import no.nav.familie.ks.sak.app.behandling.domene.Behandling;
import no.nav.familie.ks.sak.app.behandling.domene.resultat.BehandlingResultat;
import no.nav.familie.ks.sak.app.behandling.domene.resultat.BehandlingresultatRepository;
import no.nav.familie.ks.sak.app.behandling.domene.resultat.vilkår.SamletVilkårResultat;
import no.nav.familie.ks.sak.app.behandling.domene.resultat.vilkår.VilkårResultat;
import no.nav.familie.ks.sak.app.behandling.domene.resultat.vilkår.SamletVilkårResultatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class ResultatService {

    private BehandlingresultatRepository behandlingresultatRepository;
    private SamletVilkårResultatRepository samletVilkårResultatRepository;

    @Autowired
    public ResultatService(BehandlingresultatRepository behandlingresultatRepository, SamletVilkårResultatRepository samletVilkårResultatRepository) {
        this.behandlingresultatRepository = behandlingresultatRepository;
        this.samletVilkårResultatRepository = samletVilkårResultatRepository;
    }

    public void persisterResultat(Behandling behandling, SamletVilkårsVurdering samletVilkårsVurdering) {
        final var vilkårsSet = new HashSet<VilkårResultat>();
        samletVilkårsVurdering.getResultater()
                .forEach(vurdering -> vilkårsSet.add(new VilkårResultat(vurdering.getVilkårType(), vurdering.getUtfallType(), vurdering.getInputJson(), vurdering.getRegelSporingJson())));
        final var vilkårsResultat = new SamletVilkårResultat(vilkårsSet);

        samletVilkårResultatRepository.save(vilkårsResultat);
        behandlingresultatRepository.save(new BehandlingResultat(behandling, vilkårsResultat));
    }
}
