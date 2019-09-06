package no.nav.familie.ks.sak.app.rest.Behandling;

import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.BarnehageplassStatus;

import java.time.LocalDate;

public class RestBarn {
    private String aktørId;
    private BarnehageplassStatus barnehageStatus;
    private int barnehageAntallTimer;
    private LocalDate barnehageDato;
    private String barnehageKommune;

    public RestBarn(String aktørId, BarnehageplassStatus barnehageStatus, int barnehageAntallTimer, LocalDate barnehageDato, String barnehageKommune) {
        this.aktørId = aktørId;
        this.barnehageStatus = barnehageStatus;
        this.barnehageAntallTimer = barnehageAntallTimer;
        this.barnehageDato = barnehageDato;
        this.barnehageKommune = barnehageKommune;
    }
}
