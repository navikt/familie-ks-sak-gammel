package no.nav.familie.ks.sak.app.rest.Behandling;

import no.nav.familie.ks.sak.app.rest.Behandling.grunnlag.personinformasjon.RestPersonopplysninger;
import no.nav.familie.ks.sak.app.rest.Behandling.grunnlag.søknad.RestSøknad;
import no.nav.familie.ks.sak.app.rest.Behandling.resultat.RestBehandlingsresultat;

public class RestBehandling {
    public Long behandlingId;
    public RestSøknad søknadGrunnlag;
    public RestBehandlingsresultat behandlingsresultat;
    public RestPersonopplysninger personopplysninger;

    public RestBehandling(Long behandlingId, RestSøknad søknadGrunnlag, RestBehandlingsresultat behandlingsresultat, RestPersonopplysninger personopplysninger) {
        this.behandlingId = behandlingId;
        this.søknadGrunnlag = søknadGrunnlag;
        this.behandlingsresultat = behandlingsresultat;
        this.personopplysninger = personopplysninger;
    }
}
