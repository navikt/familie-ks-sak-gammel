package no.nav.familie.ks.sak.app.rest.Behandling.grunnlag.søknad;

import java.util.Set;

public class RestOppgittFamilieforhold {
    public Set<RestBarn> barna;
    public boolean borBeggeForeldreSammen;

    public RestOppgittFamilieforhold(Set<RestBarn> barna, boolean borBeggeForeldreSammen) {
        this.barna = barna;
        this.borBeggeForeldreSammen = borBeggeForeldreSammen;
    }
}
