package no.nav.familie.ks.sak.app.rest.Behandling;

import java.util.Set;

public class RestOppgittUtlandsTilknytning {
    private Set<RestAktørArbeidYtelseUtland> aktørerArbeidYtelseIUtlandet;
    private Set<RestAktørTilknytningUtland> aktørerTilknytningTilUtlandet;

    public RestOppgittUtlandsTilknytning(Set<RestAktørArbeidYtelseUtland> aktørerArbeidYtelseIUtlandet, Set<RestAktørTilknytningUtland> aktørerTilknytningTilUtlandet) {
        this.aktørerArbeidYtelseIUtlandet = aktørerArbeidYtelseIUtlandet;
        this.aktørerTilknytningTilUtlandet = aktørerTilknytningTilUtlandet;
    }
}
