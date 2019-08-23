package no.nav.familie.ks.sak.app.behandling.domene.grunnlag.barnehagebarn;

import no.nav.familie.ks.sak.app.behandling.domene.BaseEntitet;
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.BarnehageplassStatus;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "SO_BARN")
public class Barn extends BaseEntitet {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "so_barn_seq")
    @SequenceGenerator(name = "so_barn_seq")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "familieforhold_id")
    private OppgittFamilieforhold familieforhold;

    @Column(name = "aktor_id", nullable = false, updatable = false)
    private String aktørId;

    @Column(name = "barnehage_status", nullable = false, updatable = false)
    private BarnehageplassStatus barnehageStatus;

    @Column(name = "barnehage_antall_timer", nullable = true, updatable = false)
    private int barnehageAntallTimer;

    @Column(name = "barnehage_dato", nullable = true, updatable = false)
    private LocalDate barnehageDato;

    @Column(name = "barnehage_kommune", nullable = true, updatable = false)
    private String barnehageKommune;

    Barn() {
        // hibernate
    }

    Barn(String aktørId, BarnehageplassStatus barnehageStatus, int barnehageAntallTimer, LocalDate barnehageDato, String barnehageKommune) {
        this.aktørId = aktørId;
        this.barnehageStatus = barnehageStatus;
        this.barnehageAntallTimer = barnehageAntallTimer;
        this.barnehageDato = barnehageDato;
        this.barnehageKommune = barnehageKommune;
    }

    public String getAktørId() {
        return aktørId;
    }

    public int getBarnehageAntallTimer() {
        return barnehageAntallTimer;
    }

    public LocalDate getBarnehageDato() {
        return barnehageDato;
    }

    public String getBarnehageKommune() {
        return barnehageKommune;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Barn that = (Barn) o;
        return Objects.equals(aktørId, that.aktørId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aktørId);
    }

    @Override
    public String toString() {
        return "SøknadBarn{" +
                "id=" + id +
                ", familieforhold=" + familieforhold +
                ", aktørId='" + aktørId + '\'' +
                ", barnehageStatus=" + barnehageStatus +
                ", barnehageAntallTimer=" + barnehageAntallTimer +
                ", barnehageDato=" + barnehageDato +
                ", barnehageKommune='" + barnehageKommune + '\'' +
                '}';
    }

    Barn setOppgittFamilieforhold(OppgittFamilieforhold oppgittFamilieforhold) {
        this.familieforhold = oppgittFamilieforhold;
        return this;
    }

    public static class Builder {
        private String aktørId;
        private BarnehageplassStatus barnehageStatus;
        private int barnehageAntallTimer;
        private LocalDate barnehageDato;
        private String barnehageKommune;

        public Builder setAktørId(String aktørId) {
            this.aktørId = aktørId;
            return this;
        }

        public Builder setBarnehageStatus(BarnehageplassStatus barnehageStatus) {
            this.barnehageStatus = barnehageStatus;
            return this;
        }

        public Builder setBarnehageAntallTimer(int barnehageAntallTimer) {
            this.barnehageAntallTimer = barnehageAntallTimer;
            return this;
        }

        public Builder setBarnehageDato(LocalDate barnehageDato) {
            this.barnehageDato = barnehageDato;
            return this;
        }

        public Builder setBarnehageKommune(String barnehageKommune) {
            this.barnehageKommune = barnehageKommune;
            return this;
        }

        public Barn build() {
            return new Barn(aktørId, barnehageStatus, barnehageAntallTimer, barnehageDato, barnehageKommune);
        }
    }
}
