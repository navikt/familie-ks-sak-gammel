package no.nav.familie.ks.sak.app.behandling.Domene.Søknad;

import no.nav.familie.ks.sak.app.behandling.Domene.BaseEntitet;
import no.nav.familie.ks.sak.app.grunnlag.Søknad;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;

public class SøknadEntitet extends BaseEntitet {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_SOKNAD")
    private Long id;

    @Column(name = "innsendt_tidspunkt", nullable = false, updatable=false)
    private LocalDate innsendtTidspunkt;

    SøknadEntitet() {
        // hibernate
    }

    /**
     * Deep copy.
     */
    SøknadEntitet(Søknad søknad) {
        this.innsendtTidspunkt = LocalDate.from(søknad.innsendingsTidspunkt);
    }
}
