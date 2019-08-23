package no.nav.familie.ks.sak.app.mottak;

import no.nav.familie.ks.sak.FunksjonelleMetrikker;
import no.nav.familie.ks.sak.Saksbehandling;
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.UtfallType;
import no.nav.familie.ks.sak.app.behandling.resultat.Vedtak;
import no.nav.familie.ks.sak.app.grunnlag.Søknad;
import no.nav.security.oidc.api.ProtectedWithClaims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity mottaDokument(@RequestBody Søknad søknad) {
        Vedtak vedtak = saksbehandling.behandle(søknad);
        final var vilkårvurdering = vedtak.getVilkårvurdering();
        final var årsakType = vilkårvurdering.getÅrsakType().iterator().next();
        if (vilkårvurdering.getUtfallType().equals(UtfallType.OPPFYLT)) {
            log.info("Søknad kan behandles automatisk. Årsak " +
                    årsakType.getÅrsakKode() + ": " +
                    årsakType.getBeskrivelse());
        } else {
            log.info("Søknad kan ikke behandles automatisk. Årsak " +
                    årsakType.getÅrsakKode() + ": " +
                    årsakType.getBeskrivelse());
        }

        funksjonelleMetrikker.tellFunksjonelleMetrikker(søknad, vedtak);

        return new ResponseEntity(HttpStatus.OK);
    }
}
