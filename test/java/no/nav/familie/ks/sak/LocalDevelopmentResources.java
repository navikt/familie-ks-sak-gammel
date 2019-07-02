package no.nav.familie.ks.sak;

import javax.ws.rs.ApplicationPath;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import no.nav.familie.ks.sak.config.RestConfiguration;
import no.nav.security.oidc.test.support.jersey.TestTokenGeneratorResource;

@Primary
@Component
@ApplicationPath("/")
public class LocalDevelopmentResources extends RestConfiguration {
    public LocalDevelopmentResources() {
        register(TestTokenGeneratorResource.class);
    }
}
