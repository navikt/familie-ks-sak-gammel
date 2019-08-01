package no.nav.familie.ks.sak;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

import no.nav.familie.ks.sak.config.ApplicationConfig;
import no.nav.familie.ks.sak.config.DelayedShutdownHook;
import no.nav.security.spring.oidc.api.EnableOIDCTokenValidation;

@SpringBootApplication(exclude = ErrorMvcAutoConfiguration.class)
@EnableOIDCTokenValidation(ignore = "org.springframework")
public class Launcher {

    public static void main(String... args) {
        SpringApplication app = new SpringApplication(ApplicationConfig.class);
        app.setRegisterShutdownHook(false);
        ConfigurableApplicationContext applicationContext = app.run(args);
        Runtime.getRuntime().addShutdownHook(new DelayedShutdownHook(applicationContext));
    }

}
