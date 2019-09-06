package no.nav.familie.ks.sak.app.rest.Behandling;

public class RestBehandling {
    private Long behandlingId;
    private RestSøknad søknadGrunnlag;
    private RestBehandlingsresultat behandlingsresultat;

    public RestBehandling(Long behandlingId, RestSøknad søknadGrunnlag, RestBehandlingsresultat behandlingsresultat) {
        this.behandlingId = behandlingId;
        this.søknadGrunnlag = søknadGrunnlag;
        this.behandlingsresultat = behandlingsresultat;
    }
}
