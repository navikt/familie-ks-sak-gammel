package no.nav.familie.ks.sak.app.behandling.domene.resultat.vilkår;

import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.UtfallType;
import no.nav.familie.ks.sak.app.behandling.domene.typer.BaseEntitet;

import javax.persistence.*;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "samlet_vilkar_resultat")
public class SamletVilkårResultat extends BaseEntitet {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vilkars_resultat_seq")
    @SequenceGenerator(name = "vilkars_resultat_seq")
    private Long id;

    @OneToMany(mappedBy = "samletVilkårResultat", cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE})
    private Set<VilkårResultat> samletVilkårResultat;

    SamletVilkårResultat() {
    }

    public SamletVilkårResultat(Set<VilkårResultat> samletVilkårResultat) {
        this.samletVilkårResultat = samletVilkårResultat.stream().peek(it -> it.setSamletVilkårResultat(this)).collect(Collectors.toSet());
    }

    UtfallType getSamletUtfall() {
        if (samletVilkårResultat == null || samletVilkårResultat.isEmpty()) {
            return UtfallType.IKKE_VURDERT;
        }
        final var utfall = samletVilkårResultat.stream().map(VilkårResultat::getUtfall).collect(Collectors.toSet());
        if (utfall.size() == 1) {
            return utfall.iterator().next();
        }
        if (utfall.contains(UtfallType.IKKE_VURDERT)) {
            return UtfallType.UAVKLART;
        }
        if (utfall.contains(UtfallType.MANUELL_BEHANDLING)) {
            return UtfallType.MANUELL_BEHANDLING;
        }
        if (utfall.contains(UtfallType.IKKE_OPPFYLT)) {
            return UtfallType.IKKE_OPPFYLT;
        }
        throw new IllegalStateException("Ukjent utfall :" + utfall);
    }

    public Set<VilkårResultat> getSamletVilkårResultat() {
        return samletVilkårResultat;
    }

    @Override
    public String toString() {
        return "SamletVilkårResultat{" +
                "id=" + id +
                "samletVurdering=" + getSamletUtfall() +
                ", samletVilkårResultat=" + samletVilkårResultat +
                '}';
    }
}
