package no.nav.familie.ks.sak.config;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

import no.nav.familie.ks.sak.app.behandling.StartBehandlingController;
import no.nav.familie.ks.sak.app.integrasjon.personopplysning.PersonopplysningerController;

@Component
@ApplicationPath("/")
public class RestConfiguration extends ResourceConfig {
    public RestConfiguration() {
        register(PersonopplysningerController.class);
        register(StartBehandlingController.class);
    }
}
