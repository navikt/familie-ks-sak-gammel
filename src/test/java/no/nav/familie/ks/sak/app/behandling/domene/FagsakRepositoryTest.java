package no.nav.familie.ks.sak.app.behandling.domene;

import no.nav.familie.ks.sak.ApplicationTestPropertyValues;
import no.nav.familie.ks.sak.DevLauncher;
import no.nav.familie.ks.sak.app.behandling.domene.typer.AktørId;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.junit.Assert.assertEquals;


@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ContextConfiguration(classes = DevLauncher.class, initializers = { FagsakRepositoryTest.Initializer.class })
public class FagsakRepositoryTest {

    @ClassRule
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest");

    @Autowired
    private FagsakRepository repository;

    @Test
    public void shouldStoreEachNotification() {

        // given
        repository.save(new Fagsak(new AktørId("sdf"), "444"));

        // when
        long count = repository.count();

        // then
        assertEquals(count, 1);

    }


    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {

            ApplicationTestPropertyValues.using(postgreSQLContainer)
                .applyTo(configurableApplicationContext.getEnvironment());

        }

    }

}
