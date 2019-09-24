package no.nav.familie.ks.sak.app.rest.Behandling.grunnlag.søknad;

import java.util.Set;

public class RestOppgittUtlandsTilknytning {
    public Set<RestAktørArbeidYtelseUtland> aktørerArbeidYtelseIUtlandet;
    public Set<RestAktørTilknytningUtland> aktørerTilknytningTilUtlandet;

    public RestOppgittUtlandsTilknytning(Set<RestAktørArbeidYtelseUtland> aktørerArbeidYtelseIUtlandet, Set<RestAktørTilknytningUtland> aktørerTilknytningTilUtlandet) {
        this.aktørerArbeidYtelseIUtlandet = aktørerArbeidYtelseIUtlandet;
        this.aktørerTilknytningTilUtlandet = aktørerTilknytningTilUtlandet;
    }
}
