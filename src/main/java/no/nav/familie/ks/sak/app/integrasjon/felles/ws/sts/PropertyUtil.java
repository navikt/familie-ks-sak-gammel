package no.nav.familie.ks.sak.app.integrasjon.felles.ws.sts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
class PropertyUtil implements EnvironmentAware {

    private static Environment environment;

    PropertyUtil() {
        // hidden ctor
    }

    static String getProperty(String key) {
        var val = environment.getProperty(key);
        if (val == null) {
            val = System.getenv(key.toUpperCase().replace('.', '_'));
        }
        return val;
    }

    @Autowired
    @Override
    public void setEnvironment(Environment environment) {
        PropertyUtil.environment = environment;
    }
}
