package no.nav.familie.ks.sak.app;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import no.nav.familie.ks.sak.app.behandling.soknad.KontantstotteSoknad;
import no.nav.familie.ks.sak.config.ApplicationConfig;
import no.nav.security.oidc.api.Unprotected;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.nav.security.oidc.api.ProtectedWithClaims;

@RestController
@RequestMapping("/api/mottak")
@ProtectedWithClaims(issuer = "intern")
public class MottaSøknadController {

    private final Counter sokerKanBehandlesAutomatisk = Metrics.counter("soknad.kontantstotte.behandling.automatisk", "status", "JA");
    private final Counter sokerKanIkkeBehandlesAutomatisk = Metrics.counter("soknad.kontantstotte.behandling.automatisk", "status", "NEI");

    public MottaSøknadController() {
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "dokument")
    @Unprotected
    public ResponseEntity mottaDokument(@RequestBody KontantstotteSoknad soknad) {
        if (soknad.oppsummering.erGyldig()) {
            sokerKanBehandlesAutomatisk.increment();
        } else {
            sokerKanIkkeBehandlesAutomatisk.increment();
        }

        return new ResponseEntity(HttpStatus.OK);
    }
}
