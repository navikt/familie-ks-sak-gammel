package no.nav.familie.ks.sak.config.toggle;

import no.finn.unleash.strategy.Strategy;

import java.util.Map;

public class IsNotProdStrategy implements Strategy {
    private final String env;

    public IsNotProdStrategy(String env) {
        this.env = env;
    }

    @Override
    public String getName() {
        return "isNotProd";
    }

    @Override
    public boolean isEnabled(Map<String, String> map) {
        return !isProd(this.env);
    }

    private boolean isProd(String environment) {
        return "p".equalsIgnoreCase(environment);
    }
}
