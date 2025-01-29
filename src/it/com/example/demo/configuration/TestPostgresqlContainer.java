package com.example.demo.configuration;

import org.testcontainers.containers.PostgreSQLContainer;

public class TestPostgresqlContainer extends PostgreSQLContainer<TestPostgresqlContainer> {
    private static final String IMAGE_VERSION = "postgres:14.15";
    private static TestPostgresqlContainer container;

    private TestPostgresqlContainer() {
        super(IMAGE_VERSION);
    }

    public static TestPostgresqlContainer getInstance() {
        if (container == null) {
            container = new TestPostgresqlContainer().withDatabaseName("postgres")
                    .withUsername("sebastian")
                    .withPassword("password");
            container.addFixedExposedPort(8889, 5432);
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void stop() {
        //do nothing, JVM handles shut down
    }
}