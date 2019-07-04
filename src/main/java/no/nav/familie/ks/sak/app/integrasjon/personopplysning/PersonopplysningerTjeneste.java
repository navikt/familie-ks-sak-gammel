package no.nav.familie.ks.sak.app.integrasjon.personopplysning;

import java.time.LocalDate;
import java.util.Objects;

import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.Personinfo;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Bruker;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;

import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.AktørId;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.PersonhistorikkInfo;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.TpsOversetter;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.internal.PersonConsumer;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonhistorikkPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonhistorikkSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.AktoerId;
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

    public PersonhistorikkInfo hentHistorikkFor(AktørId aktørId, LocalDate fom, LocalDate tom) {
        Objects.requireNonNull(aktørId, "aktørId");
        Objects.requireNonNull(fom, "fom");
        Objects.requireNonNull(tom, "tom");
        var request = new HentPersonhistorikkRequest();
        request.setAktoer(new AktoerId().withAktoerId(aktørId.getId()));
        try {
            var response = personConsumer.hentPersonhistorikkResponse(request);
            return oversetter.tilPersonhistorikkInfo(aktørId.getId(), response);
        } catch (HentPersonhistorikkSikkerhetsbegrensning hentPersonhistorikkSikkerhetsbegrensning) {
            throw new IllegalArgumentException(hentPersonhistorikkSikkerhetsbegrensning);
        } catch (HentPersonhistorikkPersonIkkeFunnet hentPersonhistorikkPersonIkkeFunnet) {
            // Fant ikke personen returnerer tomt sett
            return PersonhistorikkInfo.builder().medAktørId(aktørId.getId()).build();
        }
    }

    public Personinfo hentPersoninfoFor(AktørId aktørId) {
        Objects.requireNonNull(aktørId, "aktørId");
        var request = new HentPersonRequest();
        request.setAktoer(new AktoerId().withAktoerId(aktørId.getId()));
        try {
            var response = personConsumer.hentPersonResponse(request);
            return oversetter.tilPersonInfo(aktørId.getId(), response.getPerson());
        } catch (HentPersonSikkerhetsbegrensning hentPersonSikkerhetsbegrensning) {
            throw new IllegalArgumentException(hentPersonSikkerhetsbegrensning);
        } catch (HentPersonPersonIkkeFunnet hentPersonPersonIkkeFunnet) {
            // Fant ikke personen returnerer tomt objekt
            return new Personinfo.Builder().medAktørId(aktørId).build();
        }
    }
}
