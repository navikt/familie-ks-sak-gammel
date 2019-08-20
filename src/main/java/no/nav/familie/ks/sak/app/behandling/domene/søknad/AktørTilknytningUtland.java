package no.nav.familie.ks.sak.app.behandling.domene.søknad;

import no.nav.familie.ks.sak.app.behandling.domene.BaseEntitet;
import no.nav.familie.ks.sak.app.grunnlag.søknad.TilknytningTilUtland;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "SO_AKTOER_TILKNYTNING_UTLAND")
public class AktørTilknytningUtland extends BaseEntitet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "aktoer", nullable = false, updatable = false)
    private String aktør;

    @ManyToOne
    @JoinColumn(name = "UTLAND_ID")
    private OppgittUtlandsTilknytning utlandsTilknytning;

    @Column(name = "BODD_ELLER_JOBBET")
    private TilknytningTilUtland.TilknytningTilUtlandVerdier tilknytningTilUtland;

    @Column(name = "BODD_ELLER_JOBBET_FORKLARING")
    private String tilknytningTilUtlandForklaring;

    AktørTilknytningUtland() {
    }

    public AktørTilknytningUtland(String aktør, OppgittUtlandsTilknytning utlandsTilknytning, TilknytningTilUtland.TilknytningTilUtlandVerdier tilknytningTilUtland, String tilknytningTilUtlandForklaring) {
        this.aktør = aktør;
        this.utlandsTilknytning = utlandsTilknytning;
        this.tilknytningTilUtland = tilknytningTilUtland;
        this.tilknytningTilUtlandForklaring = tilknytningTilUtlandForklaring;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AktørTilknytningUtland that = (AktørTilknytningUtland) o;
        return aktør.equals(that.aktør);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aktør);
    }

    public class Builder {
        private String aktør;
        private OppgittUtlandsTilknytning utlandsTilknytning;
        private TilknytningTilUtland.TilknytningTilUtlandVerdier tilknytningTilUtland;
        private String tilknytningTilUtlandForklaring;

        public Builder setAktør(String aktør) {
            this.aktør = aktør;
            return this;
        }

        public Builder setUtlandsTilknytning(OppgittUtlandsTilknytning utlandsTilknytning) {
            this.utlandsTilknytning = utlandsTilknytning;
            return this;
        }

        public Builder setTilknytningTilUtland(TilknytningTilUtland.TilknytningTilUtlandVerdier tilknytningTilUtland) {
            this.tilknytningTilUtland = tilknytningTilUtland;
            return this;
        }

        public Builder setTilknytningTilUtlandForklaring(String tilknytningTilUtlandForklaring) {
            this.tilknytningTilUtlandForklaring = tilknytningTilUtlandForklaring;
            return this;
        }

        public AktørTilknytningUtland build() {
            return new AktørTilknytningUtland(aktør, utlandsTilknytning, tilknytningTilUtland, tilknytningTilUtlandForklaring);
        }
    }
}
