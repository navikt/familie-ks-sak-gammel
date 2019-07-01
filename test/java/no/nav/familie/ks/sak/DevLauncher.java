package no.nav.familie.ks.sak;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import no.nav.familie.ks.sak.config.ApplicationConfig;
import no.nav.security.oidc.test.support.spring.TokenGeneratorConfiguration;


@SpringBootApplication(exclude = ErrorMvcAutoConfiguration.class)
@Import({ ApplicationConfig.class, TokenGeneratorConfiguration.class })
@ActiveProfiles(value = "dev")
public class DevLauncher {

    public static void main(String... args) {
        SpringApplication.run(ApplicationConfig.class, args);
    }
}
