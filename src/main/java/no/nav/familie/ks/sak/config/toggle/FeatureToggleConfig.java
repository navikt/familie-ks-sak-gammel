package no.nav.familie.ks.sak.config.toggle;

import no.finn.unleash.DefaultUnleash;
import no.finn.unleash.FakeUnleash;
import no.finn.unleash.Unleash;
import no.finn.unleash.strategy.Strategy;
import no.finn.unleash.util.UnleashConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class FeatureToggleConfig {
    private static final Logger logger = LoggerFactory.getLogger(FeatureToggleConfig.class);
    private static final String APP_NAME_PROPERTY_NAME = "${NAIS_APP_NAME}";
    private static final String UNLEASH_API_URL_PROPERTY_NAME = "${UNLEASH_API_URL}";
    private static final String ENVIRONMENT_NAME = "${ENVIRONMENT_NAME}";

    @Profile("prod | preprod")
    @Bean
    @Autowired
    public Unleash unleash(
            @Value(APP_NAME_PROPERTY_NAME) String appName,
            @Value(UNLEASH_API_URL_PROPERTY_NAME) String unleashApiUrl,
            Strategy... strategies
    ) {
        logger.info("oppretter UnleashConfig med appName: " + appName + ", unleashApi: " + unleashApiUrl);

        UnleashConfig config = UnleashConfig.builder()
                .appName(appName)
                .unleashAPI(unleashApiUrl)
                .build();

        return new DefaultUnleash(config, strategies);
    }

    @Profile("e2e")
    @Bean
    @Autowired
    public Unleash fakeUnleash() {
        logger.info("oppretter UnleashConfig med en fake Unleash");
        return new FakeUnleash();
    }

    @Profile("prod | preprod")
    @Bean
    public Strategy isNotProd(@Value(ENVIRONMENT_NAME) String env){
        return new IsNotProdStrategy(env);
    }

    @Profile("prod | preprod")
    @Bean
    public Strategy byEnvironment(@Value(ENVIRONMENT_NAME) String env){
        return new ByEnvironmentStrategy(env);
    }

}