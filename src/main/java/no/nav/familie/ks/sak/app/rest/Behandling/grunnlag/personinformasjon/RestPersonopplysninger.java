package no.nav.familie.ks.sak.app.rest.Behandling.grunnlag.personinformasjon;

import no.nav.familie.ks.sak.app.grunnlag.PersonMedHistorikk;

import java.util.List;

public class RestPersonopplysninger {
    public PersonMedHistorikk søker;
    public List<PersonMedHistorikk> barna;
    public PersonMedHistorikk annenPart;

    public RestPersonopplysninger(PersonMedHistorikk søker, List<PersonMedHistorikk> barna, PersonMedHistorikk annenPart) {
        this.søker = søker;
        this.barna = barna;
        this.annenPart = annenPart;
    }
}
