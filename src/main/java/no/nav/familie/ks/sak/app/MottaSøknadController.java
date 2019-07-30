package no.nav.familie.ks.sak.app;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import no.nav.familie.ks.sak.Saksbehandling;
import no.nav.familie.ks.sak.app.behandling.resultat.UtfallType;
import no.nav.familie.ks.sak.app.behandling.resultat.Vedtak;
import no.nav.familie.ks.sak.app.grunnlag.Søknad;
import no.nav.security.oidc.api.Unprotected;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import no.nav.security.oidc.api.ProtectedWithClaims;

@RestController
@RequestMapping("/api/mottak")
@ProtectedWithClaims(issuer = "intern")
public class MottaSøknadController {

    private final Counter sokerKanBehandlesAutomatisk = Metrics.counter("soknad.kontantstotte.behandling.automatisk", "status", "JA");
    private final Counter sokerKanIkkeBehandlesAutomatisk = Metrics.counter("soknad.kontantstotte.behandling.automatisk", "status", "NEI");
    private final Saksbehandling saksbehandling;

    public MottaSøknadController(@Autowired Saksbehandling saksbehandling) {
        this.saksbehandling = saksbehandling;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "dokument")
    @Unprotected
    public ResponseEntity mottaDokument(@RequestHeader(value="Nav-Personident") String personident,
                                        @RequestBody Søknad søknad) {

        Vedtak vedtak = saksbehandling.behandle(søknad, personident);
        if (vedtak.getVilkårvurdering().getUtfallType().equals(UtfallType.OPPFYLT)) {
            sokerKanBehandlesAutomatisk.increment();
        } else {
            sokerKanIkkeBehandlesAutomatisk.increment();
        }

        return new ResponseEntity(HttpStatus.OK);
    }
}
