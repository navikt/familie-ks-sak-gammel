package no.nav.familie.ks.sak.app.behandling.domene.grunnlag.søknad;

import no.nav.familie.ks.sak.app.behandling.domene.typer.AktørId;
import no.nav.familie.ks.sak.app.behandling.domene.typer.BaseEntitet;
import no.nav.familie.ks.sak.app.grunnlag.søknad.TilknytningTilUtland;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "SO_AKTOER_TILKNYTNING_UTLAND")
public class AktørTilknytningUtland extends BaseEntitet {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "so_aktoer_tilknytning_utland_seq")
    @SequenceGenerator(name = "so_aktoer_tilknytning_utland_seq")
    private Long id;

    @Embedded
    @AttributeOverrides(@AttributeOverride(name = "aktørId", column = @Column(name = "aktoer", updatable = false, nullable = false)))
    private AktørId aktørId;

    @ManyToOne
    @JoinColumn(name = "UTLAND_ID")
    private OppgittUtlandsTilknytning utlandsTilknytning;

    @Column(name = "BODD_ELLER_JOBBET")
    private TilknytningTilUtland.TilknytningTilUtlandVerdier tilknytningTilUtland;

    @Column(name = "BODD_ELLER_JOBBET_FORKLARING")
    private String tilknytningTilUtlandForklaring;

    AktørTilknytningUtland() {
    }

    public AktørTilknytningUtland(AktørId aktør, TilknytningTilUtland.TilknytningTilUtlandVerdier tilknytningTilUtland, String tilknytningTilUtlandForklaring) {
        this.aktørId = aktør;
        this.tilknytningTilUtland = tilknytningTilUtland;
        this.tilknytningTilUtlandForklaring = tilknytningTilUtlandForklaring;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AktørTilknytningUtland that = (AktørTilknytningUtland) o;
        return aktørId.equals(that.aktørId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aktørId);
    }

    AktørTilknytningUtland setUtlandsTilknytning(OppgittUtlandsTilknytning utlandsTilknytning) {
        this.utlandsTilknytning = utlandsTilknytning;
        return this;
    }

    public String getAktør() {
        return aktør;
    }

    public TilknytningTilUtland.TilknytningTilUtlandVerdier getTilknytningTilUtland() {
        return tilknytningTilUtland;
    }

    public String getTilknytningTilUtlandForklaring() {
        return tilknytningTilUtlandForklaring;
    }
}
