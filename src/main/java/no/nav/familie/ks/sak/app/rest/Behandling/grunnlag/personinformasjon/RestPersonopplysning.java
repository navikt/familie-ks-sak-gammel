package no.nav.familie.ks.sak.app.rest.Behandling.grunnlag.personinformasjon;

import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.personopplysning.PersonRelasjon;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.personopplysning.Personopplysning;
import no.nav.familie.ks.sak.app.behandling.domene.grunnlag.personopplysning.PersonopplysningerInformasjon;
import no.nav.familie.ks.sak.app.behandling.domene.kodeverk.Landkode;

import java.time.LocalDate;
import java.util.List;

public class RestPersonopplysning {
    public String fødselsnummer;
    public String navn;
    public LocalDate fødselsdato;
    public LocalDate dødsdato;
    public Landkode statsborgerskap;
    public List<PersonRelasjon> relasjoner;
    public RestPersonhistorikk personhistorikk;

    RestPersonopplysning(String fødselsnummer, Personopplysning personopplysning, PersonopplysningerInformasjon personopplysningerInformasjon) {
        this.fødselsnummer = fødselsnummer;
        this.navn = personopplysning.getNavn();
        this.fødselsdato = personopplysning.getFødselsdato();
        this.dødsdato = personopplysning.getDødsdato();
        this.relasjoner = personopplysningerInformasjon.getRelasjoner();
        this.statsborgerskap = personopplysning.getStatsborgerskap();
        this.personhistorikk = new RestPersonhistorikk(personopplysningerInformasjon);
    }

}
