package no.nav.familie.ks.sak.app.integrasjon.personopplysning;

import java.time.LocalDate;

import javax.validation.constraints.NotNull;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.nav.familie.ks.sak.app.integrasjon.personopplysning.domene.PersonhistorikkInfo;
import no.nav.familie.ks.sak.util.MDCOperations;
import no.nav.security.oidc.api.ProtectedWithClaims;
import no.nav.security.oidc.api.Unprotected;

@RestController
@RequestMapping("/api/personopplysning")
@ProtectedWithClaims(issuer = "intern")
public class PersonopplysningerController {

    private PersonopplysningerTjeneste personopplysningerTjeneste;

    public PersonopplysningerController(PersonopplysningerTjeneste personopplysningerTjeneste) {
        this.personopplysningerTjeneste = personopplysningerTjeneste;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, path = "historikk")
    @Unprotected
    public PersonhistorikkInfo historikk(@NotNull @RequestParam(name = "id") String personident) {
        MDCOperations.putCallId(); // FIXME: Midlertidig, bør settes generelt i et filter elns
        LocalDate idag = LocalDate.now();
        return personopplysningerTjeneste.hentHistorikkFor(personident, idag.minusYears(5), idag);
    }
}
