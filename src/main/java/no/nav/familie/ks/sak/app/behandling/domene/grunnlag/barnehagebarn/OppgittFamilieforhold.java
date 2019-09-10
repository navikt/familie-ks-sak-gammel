package no.nav.familie.ks.sak.app.behandling.domene.grunnlag.barnehagebarn;

import no.nav.familie.ks.sak.app.behandling.domene.typer.BaseEntitet;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "SO_FAMILIEFORHOLD")
public class OppgittFamilieforhold extends BaseEntitet {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "so_familieforhold_seq")
    @SequenceGenerator(name = "so_familieforhold_seq")
    private Long id;

    @OneToMany(mappedBy = "familieforhold", cascade = CascadeType.PERSIST)
    private Set<Barn> barna;

    @Column(name = "bor_begge_sammen_med_barnet")
    private boolean borBeggeForeldreSammen;

    OppgittFamilieforhold() {
        // hibernate
    }

    private OppgittFamilieforhold(Set<Barn> barna, boolean borBeggeForeldreSammen) {
        this.barna = barna.stream().peek(barn -> barn.setOppgittFamilieforhold(this)).collect(Collectors.toSet());
        this.borBeggeForeldreSammen = borBeggeForeldreSammen;
    }

    public Set<Barn> getBarna() {
        return barna;
    }

    public boolean isBorBeggeForeldreSammen() {
        return borBeggeForeldreSammen;
    }

    @Override
    public String toString() {
        return "OppgittFamilieforhold{" +
                "id=" + id +
                ", barna=" + barna +
                ", borBeggeForeldreSammen=" + borBeggeForeldreSammen +
                '}';
    }

    public static class Builder {
        private Set<Barn> barna;
        private boolean borBeggeForeldreSammen;

        public Builder setBarna(Set<Barn> barna) {
            Objects.requireNonNull(barna, "barna");
            this.barna = barna;
            return this;
        }

        public Builder setBorBeggeForeldreSammen(boolean borBeggeForeldreSammen) {
            this.borBeggeForeldreSammen = borBeggeForeldreSammen;
            return this;
        }

        public OppgittFamilieforhold build() {
            return new OppgittFamilieforhold(barna, borBeggeForeldreSammen);
        }
    }
}
