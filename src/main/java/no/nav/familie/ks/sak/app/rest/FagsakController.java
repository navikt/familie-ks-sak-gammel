package no.nav.familie.ks.sak.app.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.familie.ks.sak.app.behandling.Saksbehandling;
import no.nav.familie.ks.sak.app.behandling.BehandlingslagerService;
import no.nav.familie.ks.sak.app.behandling.domene.Fagsak;
import no.nav.familie.ks.sak.util.Ressurs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class FagsakController {

    private static final Logger logger = LoggerFactory.getLogger(FagsakController.class);

    private BehandlingslagerService behandlingslagerService;
    private Saksbehandling saksbehandling;
    private ObjectMapper objectMapper;

    @Autowired
    FagsakController(BehandlingslagerService behandlingslagerService, Saksbehandling saksbehandling, ObjectMapper objectMapper){
        this.behandlingslagerService = behandlingslagerService;
        this.saksbehandling = saksbehandling;
        this.objectMapper = objectMapper;
    }

    @GetMapping(path = "/fagsak/{fagsakId}")
    public Ressurs fagsak(@PathVariable Long fagsakId) {
        try {
            return behandlingslagerService.hentRessursFagsak(fagsakId);
        } catch (Exception e) {
            logger.error("Henting av fagsak feilet", e);
            return new Ressurs.Builder().byggFeiletRessurs("Henting av fagsak feilet");
        }
    }

    @GetMapping(path = "/fagsak")
    public Ressurs fagsak() {
        try {
            List<Fagsak> fagsaker = behandlingslagerService.hentFagsaker();
            return new Ressurs.Builder().byggVellyketRessurs(objectMapper.valueToTree(fagsaker));
        } catch (Exception e) {
            logger.error("Henting av fagsaker feilet", e);
            return new Ressurs.Builder().byggFeiletRessurs("Henting av fagsak feilet");
        }
    }

    @PostMapping(path = "/behandle")
    public Ressurs fagsak(@RequestBody no.nav.familie.ks.sak.app.grunnlag.Søknad søknad) {
        Long behandlingsId = saksbehandling.behandle(søknad).getBehandlingsId();

        return behandlingslagerService.hentRessursFagsak(behandlingsId);
    }
}
