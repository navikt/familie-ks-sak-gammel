package no.nav.familie.ks.sak.app.integrasjon.personopplysning;

import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;

import no.nav.familie.ks.sak.app.integrasjon.personopplysning.internal.PersonConsumer;


@Service
@ApplicationScope
public class PersonopplysningerTjeneste {

    private final PersonConsumer personConsumer;

    public PersonopplysningerTjeneste(PersonConsumer personConsumer) {
        this.personConsumer = personConsumer;
    }


}
