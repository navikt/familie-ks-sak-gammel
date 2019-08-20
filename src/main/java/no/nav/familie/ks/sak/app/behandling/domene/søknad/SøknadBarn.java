package no.nav.familie.ks.sak.app.behandling.domene.søknad;

import no.nav.familie.ks.sak.app.behandling.domene.BaseEntitet;
import no.nav.familie.ks.sak.app.grunnlag.Søknad;
import no.nav.familie.ks.sak.app.grunnlag.søknad.Barnehageplass;
import no.nav.familie.ks.sak.util.DateParser;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "SO_BARN")
public class SøknadBarn extends BaseEntitet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "familieforhold_id")
    private OppgittFamilieforhold familieforhold;

    @Column(name = "aktor_id", nullable = false, updatable = false)
    private String aktørId;

    @Column(name = "barnehage_status", nullable = false, updatable = false)
    private Barnehageplass.BarnehageplassVerdier barnehageStatus;

    @Column(name = "barnehage_antall_timer", nullable = true, updatable = false)
    private int barnehageAntallTimer;

    @Column(name = "barnehage_dato", nullable = true, updatable = false)
    private LocalDate barnehageDato;

    @Column(name = "barnehage_kommune", nullable = true, updatable = false)
    private String barnehageKommune;

    SøknadBarn() {
        // hibernate
    }

    SøknadBarn(OppgittFamilieforhold familieforhold, String aktørId, Barnehageplass.BarnehageplassVerdier barnehageStatus, int barnehageAntallTimer, LocalDate barnehageDato, String barnehageKommune) {
        this.familieforhold = familieforhold;
        this.aktørId = aktørId;
        this.barnehageStatus = barnehageStatus;
        this.barnehageAntallTimer = barnehageAntallTimer;
        this.barnehageDato = barnehageDato;
        this.barnehageKommune = barnehageKommune;
    }

    SøknadBarn(Søknad søknad) {
        this.barnehageStatus = søknad.barnehageplass.barnBarnehageplassStatus;
        this.aktørId = søknad.getMineBarn().getFødselsnummer(); // FIXME veksle inn til aktørId

        switch (this.barnehageStatus) {
            case harBarnehageplass:
                this.barnehageAntallTimer = Integer.parseInt(søknad.barnehageplass.harBarnehageplassAntallTimer);
                this.barnehageDato = DateParser.parseSøknadDato(søknad.barnehageplass.harBarnehageplassDato);
                this.barnehageKommune = søknad.barnehageplass.harBarnehageplassKommune;
            case harSluttetIBarnehage:
                this.barnehageAntallTimer = Integer.parseInt(søknad.barnehageplass.harSluttetIBarnehageAntallTimer);
                this.barnehageDato = DateParser.parseSøknadDato(søknad.barnehageplass.harSluttetIBarnehageDato);
                this.barnehageKommune = søknad.barnehageplass.harSluttetIBarnehageKommune;
            case skalSlutteIBarnehage:
                this.barnehageAntallTimer = Integer.parseInt(søknad.barnehageplass.skalSlutteIBarnehageAntallTimer);
                this.barnehageDato = DateParser.parseSøknadDato(søknad.barnehageplass.skalSlutteIBarnehageDato);
                this.barnehageKommune = søknad.barnehageplass.skalSlutteIBarnehageKommune;
            case skalBegynneIBarnehage:
                this.barnehageAntallTimer = Integer.parseInt(søknad.barnehageplass.skalBegynneIBarnehageAntallTimer);
                this.barnehageDato = DateParser.parseSøknadDato(søknad.barnehageplass.skalBegynneIBarnehageDato);
                this.barnehageKommune = søknad.barnehageplass.skalBegynneIBarnehageKommune;
        }
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
        SøknadBarn that = (SøknadBarn) o;
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

    void setOppgittFamilieforhold(OppgittFamilieforhold oppgittFamilieforhold) {
        this.familieforhold = oppgittFamilieforhold;
    }

    public static class Builder {
        private OppgittFamilieforhold familieforhold;
        private String aktørId;
        private Barnehageplass.BarnehageplassVerdier barnehageStatus;
        private int barnehageAntallTimer;
        private LocalDate barnehageDato;
        private String barnehageKommune;

        public Builder setFamilieforhold(OppgittFamilieforhold familieforhold) {
            this.familieforhold = familieforhold;
            return this;
        }

        public Builder setAktørId(String aktørId) {
            this.aktørId = aktørId;
            return this;
        }

        public Builder setBarnehageStatus(Barnehageplass.BarnehageplassVerdier barnehageStatus) {
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

        public SøknadBarn build() {
            return new SøknadBarn(familieforhold, aktørId, barnehageStatus, barnehageAntallTimer, barnehageDato, barnehageKommune);
        }
    }
}
