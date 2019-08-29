package no.nav.familie.ks.sak.app.rest;

import no.nav.familie.ks.sak.Saksbehandling;
import no.nav.familie.ks.sak.app.Ressurs;
import no.nav.familie.ks.sak.app.behandling.BehandlingslagerService;
import no.nav.familie.ks.sak.app.behandling.domene.Fagsak;
import no.nav.security.oidc.api.Unprotected;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class FagsakController {

    private BehandlingslagerService behandlingslagerService;
    private Saksbehandling saksbehandling;

    @Autowired
    FagsakController(BehandlingslagerService behandlingslagerService, Saksbehandling saksbehandling){
        this.behandlingslagerService = behandlingslagerService;
        this.saksbehandling = saksbehandling;
    }

    @GetMapping(path = "/fagsak/{fagsakId}")
    @Unprotected
    public Ressurs fagsak(@PathVariable Long fagsakId) {
        return behandlingslagerService.hentFagsakForSaksbehandler(fagsakId);
    }

    @GetMapping(path = "/fagsak")
    @Unprotected
    public List<Fagsak> fagsak() {
        return behandlingslagerService.hentFagsaker();
    }

    @PostMapping(path = "/behandle")
    @Unprotected
    public Ressurs fagsak(@RequestBody no.nav.familie.ks.sak.app.grunnlag.Søknad søknad) {
        Long behandlingsId = saksbehandling.behandle(søknad).getBehandlingsId();

        return behandlingslagerService.hentFagsakForSaksbehandler(behandlingsId);
    }
}
