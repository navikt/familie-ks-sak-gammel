package no.nav.familie.ks.sak;

import org.springframework.boot.test.util.TestPropertyValues;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.ArrayList;
import java.util.List;

public class ApplicationTestPropertyValues {


    public static TestPropertyValues using(PostgreSQLContainer<?> postgreSQLContainer) {
        List<String> pairs = new ArrayList<>();

        // postgres
        pairs.add("spring.datasource.url=" + postgreSQLContainer.getJdbcUrl());
        pairs.add("spring.datasource.username=" + postgreSQLContainer.getUsername());
        pairs.add("spring.datasource.password=" + postgreSQLContainer.getPassword());

        pairs.add("spring.cloud.vault.database.role=" + postgreSQLContainer.getUsername());

        return TestPropertyValues.of(pairs);
    }

}
