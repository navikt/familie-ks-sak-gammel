package no.nav.familie.ks.sak.app.behandling.domene.grunnlag.søknad;

import no.nav.familie.ks.sak.app.behandling.domene.BaseEntitet;

import javax.persistence.*;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "SO_UTLAND")
public class OppgittUtlandsTilknytning extends BaseEntitet {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "so_utland_seq")
    @SequenceGenerator(name = "so_utland_seq")
    private Long id;

    @OneToMany(mappedBy = "utlandsTilknytning", cascade = CascadeType.PERSIST)
    private Set<AktørArbeidYtelseUtland> aktørerArbeidYtelseIUtlandet;

    @OneToMany(mappedBy = "utlandsTilknytning", cascade = CascadeType.PERSIST)
    private Set<AktørTilknytningUtland> aktørerTilknytningTilUtlandet;

    OppgittUtlandsTilknytning() {
    }

    public OppgittUtlandsTilknytning(Set<AktørArbeidYtelseUtland> aktørerArbeidYtelseIUtlandet, Set<AktørTilknytningUtland> aktørerTilknytningTilUtlandet) {
        this.aktørerArbeidYtelseIUtlandet = aktørerArbeidYtelseIUtlandet.stream().peek(it -> it.setUtlandsTilknytning(this)).collect(Collectors.toSet());
        this.aktørerTilknytningTilUtlandet = aktørerTilknytningTilUtlandet.stream().peek(it -> it.setUtlandsTilknytning(this)).collect(Collectors.toSet());;
    }

    public Set<AktørArbeidYtelseUtland> getAktørerArbeidYtelseIUtlandet() {
        return aktørerArbeidYtelseIUtlandet;
    }

    public Set<AktørTilknytningUtland> getAktørerTilknytningTilUtlandet() {
        return aktørerTilknytningTilUtlandet;
    }

    @Override
    public String toString() {
        return "OppgittUtlandsTilknytning{" +
                "id=" + id +
                ", aktørerArbeidYtelseIUtlandet=" + aktørerArbeidYtelseIUtlandet +
                ", aktørerTilknytningTilUtlandet=" + aktørerTilknytningTilUtlandet +
                '}';
    }
}
