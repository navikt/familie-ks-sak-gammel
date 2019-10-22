package no.nav.familie.ks.sak.config.toggle;

import no.finn.unleash.Unleash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

/**
 * Provides an Unleash toggle implementation from a static context. That way we don't need to modify injected
 * dependencies to toggle features.
 */
@Component
public class UnleashProvider {

    private Unleash unleash;

    @Autowired
    public UnleashProvider(Unleash unleash) {
        this.unleash = unleash;
    }

    public Toggle toggle(String toggle) {
        return new Toggle(unleash, toggle);
    }

    public static class Toggle {

        private final String toggle;
        private Unleash unleash;

        Toggle(Unleash unleash, String toggle) {
            this.toggle = toggle;
            this.unleash = unleash;
        }

        public <E extends Throwable> void throwIfDisabled(Supplier<E> supplier) throws E {
            if(!unleash.isEnabled(toggle)) {
                throw supplier.get();
            }
        }

        public boolean isDisabled() {
            return !unleash.isEnabled(toggle);
        }

        public boolean isEnabled() {
            return unleash.isEnabled(toggle);
        }
    }

}
