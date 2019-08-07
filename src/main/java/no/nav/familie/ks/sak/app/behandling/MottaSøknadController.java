package no.nav.familie.ks.sak.app.behandling;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import no.nav.familie.ks.sak.FunksjonelleMetrikker;
import no.nav.familie.ks.sak.Saksbehandling;
import no.nav.familie.ks.sak.app.behandling.resultat.UtfallType;
import no.nav.familie.ks.sak.app.behandling.resultat.Vedtak;
import no.nav.familie.ks.sak.app.behandling.resultat.årsak.VilkårIkkeOppfyltÅrsak;
import no.nav.familie.ks.sak.app.grunnlag.Søknad;
import no.nav.security.oidc.api.ProtectedWithClaims;
import no.nav.security.oidc.api.Unprotected;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;

@RestController
@RequestMapping("/api/mottak")
@ProtectedWithClaims(issuer = "intern")
public class MottaSøknadController {

    private static final Logger log = LoggerFactory.getLogger(MottaSøknadController.class);
    private final Counter sokerKanBehandlesAutomatisk = Metrics.counter("soknad.kontantstotte.behandling.automatisk", "status", "JA", "beskrivelse", "Søknaden kan automatisk godkjennes");
    private final Counter sokerKanIkkeBehandlesAutomatisk = Metrics.counter("soknad.kontantstotte.behandling.automatisk", "status", "NEI", "beskrivelse", "Søknanden kan ikke automatisk godkjennes");
    private final HashMap<String, Counter> vilkårIkkeOppfyltCounters = Maps.newHashMap();

    private final FunksjonelleMetrikker funksjonelleMetrikker;
    private final Saksbehandling saksbehandling;

    public MottaSøknadController(@Autowired Saksbehandling saksbehandling, @Autowired FunksjonelleMetrikker funksjonelleMetrikker) {
        this.funksjonelleMetrikker = funksjonelleMetrikker;
        this.saksbehandling = saksbehandling;


        Lists.newArrayList(VilkårIkkeOppfyltÅrsak.values()).forEach(vilkårIkkeOppfyltÅrsak -> vilkårIkkeOppfyltCounters.put(
                Integer.toString(vilkårIkkeOppfyltÅrsak.getÅrsakKode()),
                Metrics.counter("soknad.kontantstotte.behandling.automatisk", "status", Integer.toString(vilkårIkkeOppfyltÅrsak.getÅrsakKode()), "beskrivelse", vilkårIkkeOppfyltÅrsak.getBeskrivelse())
        ));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "dokument")
    public ResponseEntity mottaDokument(@RequestHeader(value="Nav-Personident") String personident,
                                        @RequestBody Søknad søknad) {
        Vedtak vedtak = saksbehandling.behandle(søknad, personident);
        if (vedtak.getVilkårvurdering().getUtfallType().equals(UtfallType.OPPFYLT)) {
            log.info("Søknad kan behandles automatisk. Årsak " +
                    vedtak.getVilkårvurdering().getVilkårÅrsak().getÅrsakKode() + ": " +
                    vedtak.getVilkårvurdering().getVilkårÅrsak().getBeskrivelse());
            sokerKanBehandlesAutomatisk.increment();
        } else {
            log.info("Søknad kan ikke behandles automatisk. Årsak " +
                    vedtak.getVilkårvurdering().getVilkårÅrsak().getÅrsakKode() + ": " +
                    vedtak.getVilkårvurdering().getVilkårÅrsak().getBeskrivelse());
            vilkårIkkeOppfyltCounters.get(Integer.toString(vedtak.getVilkårvurdering().getVilkårÅrsak().getÅrsakKode())).increment();
            sokerKanIkkeBehandlesAutomatisk.increment();
        }

        funksjonelleMetrikker.tellFunksjonelleMetrikker(søknad);

        return new ResponseEntity(HttpStatus.OK);
    }
}
