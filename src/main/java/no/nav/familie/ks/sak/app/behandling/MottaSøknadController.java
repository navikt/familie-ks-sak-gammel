package no.nav.familie.ks.sak.app.behandling;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.ws.rs.core.Response;

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

    @PostMapping(consumes = APPLICATION_JSON, path = "dokument")
    public Response mottaDokument(@RequestBody String soeknad) {

        return Response.ok().build();
    }
}
