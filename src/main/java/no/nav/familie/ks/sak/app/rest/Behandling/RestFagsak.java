package no.nav.familie.ks.sak.app.rest.Behandling;

import no.nav.familie.ks.sak.app.behandling.domene.Fagsak;

import java.util.List;

public class RestFagsak {
    Fagsak fagsak;
    List<RestBehandling> behandlinger;

    public RestFagsak(Fagsak fagsak, List<RestBehandling> behandlinger) {
        this.fagsak = fagsak;
        this.behandlinger = behandlinger;
    }
}
