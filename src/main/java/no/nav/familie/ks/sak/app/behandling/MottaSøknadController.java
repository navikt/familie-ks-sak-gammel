package no.nav.familie.ks.sak.app.behandling;

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

    public MottaSøknadController() {
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "dokument")
    public ResponseEntity mottaDokument(@RequestBody String soeknad) {

        return new ResponseEntity(HttpStatus.OK);
    }
}
