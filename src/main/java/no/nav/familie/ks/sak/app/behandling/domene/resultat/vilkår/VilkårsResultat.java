package no.nav.familie.ks.sak.app.behandling.domene.resultat.vilkår;

import no.nav.familie.ks.sak.app.behandling.domene.BaseEntitet;
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.UtfallType;

import javax.persistence.*;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "vilkars_resultat")
public class VilkårsResultat extends BaseEntitet {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vilkars_resultat_seq")
    @SequenceGenerator(name = "vilkars_resultat_seq")
    private Long id;

    @OneToMany(mappedBy = "vilkårsResultat", cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE})
    private Set<VilkårResultat> vilkårsResultat;

    VilkårsResultat() {
    }

    public VilkårsResultat(Set<VilkårResultat> vilkårsResultat) {
        this.vilkårsResultat = vilkårsResultat.stream().peek(it -> it.setVilkårsResultat(this)).collect(Collectors.toSet());
    }

    public UtfallType getSamletUtfall() {
        if (vilkårsResultat == null || vilkårsResultat.isEmpty()) {
            return UtfallType.IKKE_VURDERT;
        }
        final var utfall = vilkårsResultat.stream().map(VilkårResultat::getUtfall).collect(Collectors.toSet());
        if (utfall.size() == 1) {
            return utfall.iterator().next();
        }
        if (utfall.contains(UtfallType.IKKE_OPPFYLT)) {
            return UtfallType.IKKE_OPPFYLT;
        }
        if (utfall.contains(UtfallType.MANUELL_BEHANDLING)) {
            return UtfallType.MANUELL_BEHANDLING;
        }
        if (utfall.contains(UtfallType.IKKE_VURDERT)) {
            return UtfallType.UAVKLART;
        }
        throw new IllegalStateException("Ukjent utfall :" + utfall);
    }

    public Set<VilkårResultat> getVilkårsResultat() {
        return vilkårsResultat;
    }

    @Override
    public String toString() {
        return "VilkårsResultat{" +
                "id=" + id +
                "samletVurdering=" + getSamletUtfall() +
                ", vilkårsResultat=" + vilkårsResultat +
                '}';
    }
}
