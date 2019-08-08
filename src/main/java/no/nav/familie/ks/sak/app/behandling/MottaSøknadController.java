package no.nav.familie.ks.sak.app.behandling;

import no.nav.familie.ks.sak.FunksjonelleMetrikker;
import no.nav.familie.ks.sak.Saksbehandling;
import no.nav.familie.ks.sak.app.behandling.resultat.UtfallType;
import no.nav.familie.ks.sak.app.behandling.resultat.Vedtak;
import no.nav.familie.ks.sak.app.grunnlag.Søknad;
import no.nav.security.oidc.api.ProtectedWithClaims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mottak")
@ProtectedWithClaims(issuer = "intern")
public class MottaSøknadController {

    private static final Logger log = LoggerFactory.getLogger(MottaSøknadController.class);

    private final FunksjonelleMetrikker funksjonelleMetrikker;
    private final Saksbehandling saksbehandling;

    public MottaSøknadController(@Autowired Saksbehandling saksbehandling, @Autowired FunksjonelleMetrikker funksjonelleMetrikker) {
        this.funksjonelleMetrikker = funksjonelleMetrikker;
        this.saksbehandling = saksbehandling;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "dokument")
    public ResponseEntity mottaDokument(@RequestHeader(value="Nav-Personident") String personident,
                                        @RequestBody Søknad søknad) {
        Vedtak vedtak = saksbehandling.behandle(søknad, personident);
        if (vedtak.getVilkårvurdering().getUtfallType().equals(UtfallType.OPPFYLT)) {
            log.info("Søknad kan behandles automatisk. Årsak " +
                    vedtak.getVilkårvurdering().getVilkårÅrsak().getÅrsakKode() + ": " +
                    vedtak.getVilkårvurdering().getVilkårÅrsak().getBeskrivelse());
        } else {
            log.info("Søknad kan ikke behandles automatisk. Årsak " +
                    vedtak.getVilkårvurdering().getVilkårÅrsak().getÅrsakKode() + ": " +
                    vedtak.getVilkårvurdering().getVilkårÅrsak().getBeskrivelse());
        }

        funksjonelleMetrikker.tellFunksjonelleMetrikker(søknad, vedtak);

        return new ResponseEntity(HttpStatus.OK);
    }
}
