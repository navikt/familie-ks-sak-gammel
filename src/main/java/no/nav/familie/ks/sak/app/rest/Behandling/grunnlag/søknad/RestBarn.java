package no.nav.familie.ks.sak.app.rest.Behandling.grunnlag.søknad;

import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.BarnehageplassStatus;
import no.nav.familie.ks.sak.app.behandling.domene.typer.AktørId;

import java.time.LocalDate;

public class RestBarn {
    public AktørId aktørId;
    public BarnehageplassStatus barnehageStatus;
    public Double barnehageAntallTimer;
    public LocalDate barnehageDato;
    public String barnehageKommune;

    public RestBarn(AktørId aktørId, BarnehageplassStatus barnehageStatus, Double barnehageAntallTimer, LocalDate barnehageDato, String barnehageKommune) {
        this.aktørId = aktørId;
        this.barnehageStatus = barnehageStatus;
        this.barnehageAntallTimer = barnehageAntallTimer;
        this.barnehageDato = barnehageDato;
        this.barnehageKommune = barnehageKommune;
    }
}
