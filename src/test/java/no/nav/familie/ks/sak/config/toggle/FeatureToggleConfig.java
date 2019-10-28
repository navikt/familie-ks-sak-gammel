package no.nav.familie.ks.sak.config.toggle;

import no.finn.unleash.FakeUnleash;
import no.finn.unleash.Unleash;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev")
public class FeatureToggleConfig {

    @Bean
    public Unleash unleash() {
        return new FakeUnleash();
    }
}
