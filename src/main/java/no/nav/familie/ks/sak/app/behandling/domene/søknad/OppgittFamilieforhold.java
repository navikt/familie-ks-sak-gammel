package no.nav.familie.ks.sak.app.behandling.domene.søknad;

import no.nav.familie.ks.sak.app.behandling.domene.BaseEntitet;

import javax.persistence.*;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "SO_FAMILIEFORHOLD")
public class OppgittFamilieforhold extends BaseEntitet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "familieforhold")
    private Set<SøknadBarn> barna;

    @Column(name = "bor_begge_sammen_med_barnet")
    private boolean borBeggeForeldreSammen;

    OppgittFamilieforhold() {
        // hibernate
    }

    private OppgittFamilieforhold(Set<SøknadBarn> barna, boolean borBeggeForeldreSammen) {
        this.barna = barna.stream().peek(barn -> barn.setOppgittFamilieforhold(this)).collect(Collectors.toSet());
        this.borBeggeForeldreSammen = borBeggeForeldreSammen;
    }

    public Set<SøknadBarn> getBarna() {
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
        private Set<SøknadBarn> barna;
        private boolean borBeggeForeldreSammen;

        public Builder setBarna(Set<SøknadBarn> barna) {
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
