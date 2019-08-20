package no.nav.familie.ks.sak.app.behandling.domene.søknad;

import no.nav.familie.ks.sak.app.behandling.domene.BaseEntitet;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "SO_UTLAND")
public class OppgittUtlandsTilknytning extends BaseEntitet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "utlandsTilknytning")
    private Set<AktørArbeidYtelseUtland> aktørerArbeidYtelseIUtlandet;

    @OneToMany(mappedBy = "utlandsTilknytning")
    private Set<AktørTilknytningUtland> aktørerTilknytningTilUtlandet;

    @Override
    public String toString() {
        return "OppgittUtlandsTilknytning{" +
                "id=" + id +
                ", aktørerArbeidYtelseIUtlandet=" + aktørerArbeidYtelseIUtlandet +
                ", aktørerTilknytningTilUtlandet=" + aktørerTilknytningTilUtlandet +
                '}';
    }
}
