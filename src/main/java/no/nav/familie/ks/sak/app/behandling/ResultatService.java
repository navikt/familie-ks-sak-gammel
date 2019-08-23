package no.nav.familie.ks.sak.app.behandling;

import no.nav.familie.ks.sak.app.behandling.domene.Behandling;
import no.nav.familie.ks.sak.app.behandling.domene.resultat.BehandlingResultat;
import no.nav.familie.ks.sak.app.behandling.domene.resultat.BehandlingresultatRepository;
import no.nav.familie.ks.sak.app.behandling.domene.resultat.vilkår.VilkårResultat;
import no.nav.familie.ks.sak.app.behandling.domene.resultat.vilkår.VilkårsResultat;
import no.nav.familie.ks.sak.app.behandling.domene.resultat.vilkår.VilkårsResultatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class ResultatService {

    private BehandlingresultatRepository behandlingresultatRepository;
    private VilkårsResultatRepository vilkårsResultatRepository;

    @Autowired
    public ResultatService(BehandlingresultatRepository behandlingresultatRepository, VilkårsResultatRepository vilkårsResultatRepository) {
        this.behandlingresultatRepository = behandlingresultatRepository;
        this.vilkårsResultatRepository = vilkårsResultatRepository;
    }

    public void persisterResultat(Behandling behandling, SamletVilkårsVurdering samletVilkårsVurdering) {
        final var vilkårsSet = new HashSet<VilkårResultat>();
        samletVilkårsVurdering.getResultater()
                .forEach(vurdering -> vilkårsSet.add(new VilkårResultat(vurdering.getVilkårType(), vurdering.getUtfallType(), vurdering.getInputJson(), vurdering.getRegelSporingJson())));
        final var vilkårsResultat = new VilkårsResultat(vilkårsSet);

        vilkårsResultatRepository.save(vilkårsResultat);
        behandlingresultatRepository.saveAndFlush(new BehandlingResultat(behandling, vilkårsResultat));
    }
}