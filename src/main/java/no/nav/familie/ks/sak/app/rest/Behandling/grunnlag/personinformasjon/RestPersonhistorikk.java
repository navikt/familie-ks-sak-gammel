package no.nav.familie.ks.sak.app.rest.Behandling.grunnlag.personinformasjon;

import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.personopplysning.PersonAdresse;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.personopplysning.Personopplysning;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.personopplysning.PersonopplysningerInformasjon;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.personopplysning.Statsborgerskap;

import java.util.List;

public class RestPersonhistorikk {
    public List<PersonAdresse> adresser;
    public List<Personopplysning> personopplysninger;
    public List<Statsborgerskap> statsborgerskap;

    public RestPersonhistorikk(PersonopplysningerInformasjon personopplysningerInformasjon) {
        this.adresser = personopplysningerInformasjon.getAdresser();
        this.personopplysninger = personopplysningerInformasjon.getPersonopplysninger();
        this.statsborgerskap = personopplysningerInformasjon.getStatsborgerskap();
    }
}
