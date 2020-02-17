package no.nav.familie.ks.sak.app.behandling.domene.grunnlag.søknad;

import com.fasterxml.jackson.annotation.JsonIgnore;
import no.nav.familie.kontrakter.ks.søknad.TilknytningTilUtlandVerdier;
import no.nav.familie.ks.sak.app.behandling.domene.typer.AktørId;
import no.nav.familie.ks.sak.app.behandling.domene.typer.BaseEntitet;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "SO_AKTOER_TILKNYTNING_UTLAND")
public class AktørTilknytningUtland extends BaseEntitet {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "so_aktoer_tilknytning_utland_seq")
    @SequenceGenerator(name = "so_aktoer_tilknytning_utland_seq")
    private Long id;

    @Column(name = "FNR")
    private String fødselsnummer;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "UTLAND_ID")
    private OppgittUtlandsTilknytning utlandsTilknytning;

    @Enumerated(EnumType.STRING)
    @Column(name = "BODD_ELLER_JOBBET")
    private TilknytningTilUtlandVerdier tilknytningTilUtland;

    @Column(name = "BODD_ELLER_JOBBET_FORKLARING")
    private String tilknytningTilUtlandForklaring;

    AktørTilknytningUtland() {
    }

    public AktørTilknytningUtland(String fødselsnummer, TilknytningTilUtlandVerdier tilknytningTilUtland, String tilknytningTilUtlandForklaring) {
        this.fødselsnummer = fødselsnummer;
        this.tilknytningTilUtland = tilknytningTilUtland;
        this.tilknytningTilUtlandForklaring = tilknytningTilUtlandForklaring;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AktørTilknytningUtland that = (AktørTilknytningUtland) o;
        return fødselsnummer.equals(that.fødselsnummer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fødselsnummer);
    }

    AktørTilknytningUtland setUtlandsTilknytning(OppgittUtlandsTilknytning utlandsTilknytning) {
        this.utlandsTilknytning = utlandsTilknytning;
        return this;
    }

    public String getFødselsnummer() {
        return fødselsnummer;
    }

    public void setFødselsnummer(String fnr) {
        this.fødselsnummer = fnr;
    }

    public TilknytningTilUtlandVerdier getTilknytningTilUtland() {
        return tilknytningTilUtland;
    }

    public String getTilknytningTilUtlandForklaring() {
        return tilknytningTilUtlandForklaring;
    }
}
