package no.nav.familie.ks.sak.app.behandling.domene.grunnlag.søknad;

import no.nav.familie.ks.sak.app.behandling.domene.BaseEntitet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "SO_UTLAND")
public class OppgittUtlandsTilknytning extends BaseEntitet<Long> {

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
                "id=" + getId() +
                ", aktørerArbeidYtelseIUtlandet=" + aktørerArbeidYtelseIUtlandet +
                ", aktørerTilknytningTilUtlandet=" + aktørerTilknytningTilUtlandet +
                '}';
    }
}
