package no.nav.familie.ks.sak.app.rest.Behandling;

import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.BarnehageplassStatus;
import no.nav.familie.ks.sak.app.behandling.domene.typer.AktørId;

import java.time.LocalDate;

public class RestBarn {
    private AktørId aktørId;
    private BarnehageplassStatus barnehageStatus;
    private int barnehageAntallTimer;
    private LocalDate barnehageDato;
    private String barnehageKommune;

    public RestBarn(AktørId aktørId, BarnehageplassStatus barnehageStatus, int barnehageAntallTimer, LocalDate barnehageDato, String barnehageKommune) {
        this.aktørId = aktørId;
        this.barnehageStatus = barnehageStatus;
        this.barnehageAntallTimer = barnehageAntallTimer;
        this.barnehageDato = barnehageDato;
        this.barnehageKommune = barnehageKommune;
    }
}
