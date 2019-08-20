package no.nav.familie.ks.sak.app.behandling.Domene.Søknad;

import no.nav.familie.ks.sak.app.behandling.Domene.BaseEntitet;
import no.nav.familie.ks.sak.app.grunnlag.Søknad;
import no.nav.familie.ks.sak.app.grunnlag.søknad.Barnehageplass;
import no.nav.familie.ks.sak.util.DateParser;

import javax.persistence.*;
import java.time.LocalDate;

@Entity(name="Søknad barn")
@Table(name="SO_BARN")
public class SøknadBarn extends BaseEntitet {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_SØKNAD_BARN")
    private Long id;

    @Column(name="aktor_id", nullable = false, updatable = false)
    private String aktørId;

    @Column(name="barnehage_status", nullable = false, updatable = false)
    private Barnehageplass.BarnehageplassVerdier barnehageStatus;

    @Column(name="barnehage_antall_timer", nullable = true, updatable = false)
    private int barnehageAntallTimer;

    @Column(name="barnehage_dato", nullable = true, updatable = false)
    private LocalDate barnehageDato;

    @Column(name="barnehage_kommune", nullable = true, updatable = false)
    private String barnehageKommune;


    SøknadBarn() {
        // hibernate
    }

    SøknadBarn (Søknad søknad) {
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
}
