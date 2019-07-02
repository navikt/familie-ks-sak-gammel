package no.nav.familie.ks.sak.app.integrasjon.personopplysning;

import java.time.LocalDate;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.springframework.stereotype.Component;

import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.AktørId;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.PersonhistorikkInfo;
import no.nav.familie.ks.sak.util.MDCOperations;
import no.nav.security.oidc.api.Unprotected;

@Component
@Path("personopplysninger")
public class PersonopplysningerController {

    private PersonopplysningerTjeneste personopplysningerTjeneste;

    public PersonopplysningerController(PersonopplysningerTjeneste personopplysningerTjeneste) {
        this.personopplysningerTjeneste = personopplysningerTjeneste;
    }

    @GET
    @Path("historikk")
    @Unprotected
    public PersonhistorikkInfo historikk(@QueryParam("id") String aktørId) {
        MDCOperations.putCallId(); // FIXME: Midlertidig, bør settes generelt i et filter elns
        LocalDate idag = LocalDate.now();
        return personopplysningerTjeneste.hentHistorikkFor(new AktørId(aktørId), idag.minusYears(5), idag);
    }
}
