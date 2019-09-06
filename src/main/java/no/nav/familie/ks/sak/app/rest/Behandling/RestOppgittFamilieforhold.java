package no.nav.familie.ks.sak.app.rest.Behandling;

import java.util.Set;

public class RestOppgittFamilieforhold {
    private Set<RestBarn> barna;
    private boolean borBeggeForeldreSammen;

    public RestOppgittFamilieforhold(Set<RestBarn> barna, boolean borBeggeForeldreSammen) {
        this.barna = barna;
        this.borBeggeForeldreSammen = borBeggeForeldreSammen;
    }
}
