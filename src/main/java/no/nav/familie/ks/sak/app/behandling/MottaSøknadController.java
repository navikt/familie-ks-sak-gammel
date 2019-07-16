package no.nav.familie.ks.sak.app.behandling;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import no.nav.familie.ks.sak.config.ApplicationConfig;
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

    private static final Logger log = LoggerFactory.getLogger(MottaSøknadController.class);

    private final Counter soknadAutomatiskGodkjent = Metrics.counter("soknad.kontantstotte.automatisk.godkjent");

    public MottaSøknadController() {
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "dokument")
    public ResponseEntity mottaDokument(@RequestBody String soknad) {
        log.info(soknad);

        soknadAutomatiskGodkjent.increment();

        return new ResponseEntity(HttpStatus.OK);
    }
}
