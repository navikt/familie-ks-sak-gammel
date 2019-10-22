package no.nav.familie.ks.sak.config.toggle;

import no.finn.unleash.DefaultUnleash;
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
import org.springframework.context.annotation.Scope;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;


@Configuration
public class FeatureToggleConfig {
    private static final Logger logger = LoggerFactory.getLogger(FeatureToggleConfig.class);
    private static final String APP_NAME_PROPERTY_NAME = "${NAIS_APP_NAME}";
    private static final String UNLEASH_API_URL_PROPERTY_NAME = "${UNLEASH_API_URL}";
    private static final String ENVIRONMENT_NAME = "${ENVIRONMENT_NAME}";

    @Profile("!dev")
    @Bean
    @Scope(SCOPE_SINGLETON)
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

    @Profile("!dev")
    @Bean
    public Strategy isNotProd(@Value(ENVIRONMENT_NAME) String env){
        return new IsNotProdStrategy(env);
    }

    @Profile("!dev")
    @Bean
    public Strategy byEnvironment(@Value(ENVIRONMENT_NAME) String env){
        return new ByEnvironmentStrategy(env);
    }

}
