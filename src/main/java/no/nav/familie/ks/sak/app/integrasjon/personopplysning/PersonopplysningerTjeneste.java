package no.nav.familie.ks.sak.app.integrasjon.personopplysning;

import java.time.LocalDate;
import java.util.Objects;

import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.Personinfo;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.NorskIdent;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.PersonIdent;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;

import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.PersonhistorikkInfo;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.TpsOversetter;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.internal.PersonConsumer;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonhistorikkPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonhistorikkSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonhistorikkRequest;


@Service
@ApplicationScope
public class PersonopplysningerTjeneste {

    private final PersonConsumer personConsumer;
    private TpsOversetter oversetter;

    public PersonopplysningerTjeneste(PersonConsumer personConsumer, TpsOversetter oversetter) {
        this.personConsumer = personConsumer;
        this.oversetter = oversetter;
    }

    public PersonhistorikkInfo hentHistorikkFor(String personident, LocalDate fom, LocalDate tom) {
        Objects.requireNonNull(fom, "fom");
        Objects.requireNonNull(tom, "tom");
        Objects.requireNonNull(personident, "personident");
        NorskIdent norskIdent = new NorskIdent().withIdent(personident);
        var request = new HentPersonhistorikkRequest();
        request.setAktoer(new PersonIdent().withIdent(norskIdent));
        try {
            var response = personConsumer.hentPersonhistorikkResponse(request);
            return oversetter.tilPersonhistorikkInfo(personident, response);
        } catch (HentPersonhistorikkSikkerhetsbegrensning hentPersonhistorikkSikkerhetsbegrensning) {
            throw new IllegalArgumentException(hentPersonhistorikkSikkerhetsbegrensning);
        } catch (HentPersonhistorikkPersonIkkeFunnet hentPersonhistorikkPersonIkkeFunnet) {
            // Fant ikke personen returnerer tomt sett
            return PersonhistorikkInfo.builder().medPersonIdent(personident).build();
        }
    }

    public Personinfo hentPersoninfoFor(String personident) {
        Objects.requireNonNull(personident, "personident");
        var norskIdent = new NorskIdent().withIdent(personident);
        var personIdent = new PersonIdent().withIdent(norskIdent);
        var request = new HentPersonRequest();
        request.setAktoer(personIdent);
        try {
            var response = personConsumer.hentPersonResponse(request);
            return oversetter.tilPersonInfo(personident, response.getPerson());
        } catch (HentPersonSikkerhetsbegrensning hentPersonSikkerhetsbegrensning) {
            throw new IllegalArgumentException(hentPersonSikkerhetsbegrensning);
        } catch (HentPersonPersonIkkeFunnet hentPersonPersonIkkeFunnet) {
            // Fant ikke personen returnerer tomt objekt
            return new Personinfo.Builder().medPersonIdent(new no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.PersonIdent(personident)).build();
        }
    }
}
