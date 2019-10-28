package no.nav.familie.ks.sak.config;

import no.finn.unleash.Unleash;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import java.util.LinkedList;
import java.util.List;

@Configuration
public class UnleashTestConfig {

    @Bean
    @Profile("dev")
    @Primary
    public Unleash unleashDevConfig() {
        return new Unleash() {
            @Override
            public boolean isEnabled(String s) {
                return true;
            }

            @Override
            public boolean isEnabled(String s, boolean b) {
                return true;
            }

            @Override
            public List<String> getFeatureToggleNames() {
                return new LinkedList<>();
            }
        };
    }
}
