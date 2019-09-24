package no.nav.familie.ks.sak.app.rest.Behandling.grunnlag.personinformasjon;

import no.nav.familie.ks.sak.app.grunnlag.PersonMedHistorikk;

import java.util.List;

public class RestPersonopplysninger {
    public RestPersonopplysning søker;
    public List<RestPersonopplysning> barna;
    public RestPersonopplysning annenPart;

    public RestPersonopplysninger(RestPersonopplysning søker, List<RestPersonopplysning> barna, RestPersonopplysning annenPart) {
        this.søker = søker;
        this.barna = barna;
        this.annenPart = annenPart;
    }
}
