package no.nav.familie.ks.sak.config.toggle;


import no.finn.unleash.strategy.Strategy;

import java.util.Map;
import java.util.Optional;

import static java.util.Arrays.asList;

class ByEnvironmentStrategy implements Strategy {
    private String env;

    ByEnvironmentStrategy(String env) {
        this.env = env;
    }

    @Override
    public String getName() {
        return "byEnvironment";
    }

    @Override
    public boolean isEnabled(Map<String, String> parameters) {
        return Optional.ofNullable(parameters.get("miljø"))
                .map(miljo -> asList(miljo.split(",")))
                .filter(f -> f.contains(env)).isPresent();
    }
}
