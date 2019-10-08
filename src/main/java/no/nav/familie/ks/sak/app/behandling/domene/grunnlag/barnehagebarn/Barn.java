package no.nav.familie.ks.sak.app.behandling.domene.grunnlag.barnehagebarn;

import com.fasterxml.jackson.annotation.JsonIgnore;
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.BarnehageplassStatus;
import no.nav.familie.ks.sak.app.behandling.domene.typer.BaseEntitet;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "SO_BARN")
public class Barn extends BaseEntitet {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "so_barn_seq")
    @SequenceGenerator(name = "so_barn_seq")
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "familieforhold_id")
    private OppgittFamilieforhold familieforhold;

    @Column(name = "fnr", nullable = true, updatable = true)
    private String fødselsnummer;

    @Enumerated(EnumType.STRING)
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

    Barn(String fødselsnummer, BarnehageplassStatus barnehageStatus, Double barnehageAntallTimer, LocalDate barnehageDato, String barnehageKommune) {
        this.fødselsnummer = fødselsnummer;
        this.barnehageStatus = barnehageStatus;
        this.barnehageAntallTimer = barnehageAntallTimer;
        this.barnehageDato = barnehageDato;
        this.barnehageKommune = barnehageKommune;
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
    public String toString() {
        return "SøknadBarn{" +
            "id=" + id +
            ", familieforhold=" + familieforhold +
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

    public String getFødselsnummer() {
        return fødselsnummer;
    }

    public void setFødselsnummer(String fødselsnummer) {
        this.fødselsnummer = fødselsnummer;
    }

    public static class Builder {
        private String fødselsnummer;
        private BarnehageplassStatus barnehageStatus;
        private Double barnehageAntallTimer;
        private LocalDate barnehageDato;
        private String barnehageKommune;

        public Builder setFødselsnummer(String fødselsnummer) {
            this.fødselsnummer = fødselsnummer;
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
            return new Barn(fødselsnummer, barnehageStatus, barnehageAntallTimer, barnehageDato, barnehageKommune);
        }
    }
}
