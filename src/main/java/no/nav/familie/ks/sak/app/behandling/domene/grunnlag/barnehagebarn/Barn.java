package no.nav.familie.ks.sak.app.behandling.domene.grunnlag.barnehagebarn;

import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.BarnehageplassStatus;
import no.nav.familie.ks.sak.app.behandling.domene.typer.AktørId;
import no.nav.familie.ks.sak.app.behandling.domene.typer.BaseEntitet;

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

    @Embedded
    @AttributeOverrides(@AttributeOverride(name = "aktørId", column = @Column(name = "aktoer_id", updatable = false)))
    private AktørId aktørId;

    @Column(name = "barnehage_status", nullable = false, updatable = false)
    private BarnehageplassStatus barnehageStatus;

    @Column(name = "barnehage_antall_timer", nullable = true, updatable = false)
    private Double barnehageAntallTimer;

    @Column(name = "barnehage_dato", nullable = true, updatable = false)
    private LocalDate barnehageDato;

    @Column(name = "barnehage_kommune", nullable = true, updatable = false)
    private String barnehageKommune;

    Barn() {
        // hibernate
    }

    Barn(AktørId aktørId, BarnehageplassStatus barnehageStatus, Double barnehageAntallTimer, LocalDate barnehageDato, String barnehageKommune) {
        this.aktørId = aktørId;
        this.barnehageStatus = barnehageStatus;
        this.barnehageAntallTimer = barnehageAntallTimer;
        this.barnehageDato = barnehageDato;
        this.barnehageKommune = barnehageKommune;
    }

    public AktørId getAktørId() {
        return aktørId;
    }

    public BarnehageplassStatus getBarnehageStatus() {
        return barnehageStatus;
    }

    public Double getBarnehageAntallTimer() {
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
        private AktørId aktørId;
        private BarnehageplassStatus barnehageStatus;
        private Double barnehageAntallTimer;
        private LocalDate barnehageDato;
        private String barnehageKommune;

        public Builder setAktørId(String aktørId) {
            this.aktørId = new AktørId(aktørId);
            return this;
        }

        public Builder setBarnehageStatus(BarnehageplassStatus barnehageStatus) {
            this.barnehageStatus = barnehageStatus;
            return this;
        }

        public Builder setBarnehageAntallTimer(double barnehageAntallTimer) {
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
