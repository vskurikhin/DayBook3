package su.svn;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.PostgreSQLContainer;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class PostgreSQLTestResource implements QuarkusTestResourceLifecycleManager {
    static PostgreSQLContainer<?> db;
    private static final AtomicInteger COUNTER = new AtomicInteger();

    @Override
    public Map<String, String> start() {
        db = new PostgreSQLContainer<>("pgvector/pgvector:pg18");
        db.start();
        int id = COUNTER.incrementAndGet();
        System.err.printf("START %d %s%n", id, db);
        var url = String.format(
                "postgresql://%s:%d/%s",
                db.getHost(),
                db.getMappedPort(5432),
                db.getDatabaseName());

        System.setProperty("quarkus.datasource.reactive.url",
                String.format("postgresql://%s:%d/%s",
                        db.getHost(),
                        db.getMappedPort(5432),
                        db.getDatabaseName()));
        try {
            db.execInContainer("psql",
                    "-U", db.getUsername(),
                    "-d", db.getDatabaseName(),
                    "-c", "CREATE SCHEMA IF NOT EXISTS api;");
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        var jdbcUrl = db.getJdbcUrl() + "&currentSchema=api,public";
        System.out.println("jdbcUrl = " + jdbcUrl);
        System.out.println("db.getJdbcUrl() = " + db.getJdbcUrl());
        System.out.println("db.getDatabaseName() = " + db.getDatabaseName());
        System.out.println("db.getExposedPorts() = " + db.getExposedPorts());
        System.out.println("db.getFirstMappedPort() = " + db.getFirstMappedPort());
        System.out.println("db.getUsername() = " + db.getUsername());
        System.out.println("url = " + url);
        System.out.println("db.isRunning() = " + db.isRunning());

        // Pass container properties to Quarkus
        return Map.of(
                "quarkus.datasource.jdbc.url", jdbcUrl,
                "quarkus.datasource.reactive.url",
                String.format(
                        "postgresql://%s:%d/%s",
                        db.getHost(),
                        db.getMappedPort(5432),
                        db.getDatabaseName()),
                "quarkus.datasource.username", db.getUsername(),
                "quarkus.datasource.password", db.getPassword()
        );
    }

    @Override
    public void stop() {
        System.out.println("db.isRunning() = " + db.isRunning());
        System.err.printf("STOP %s running=%s%n", db, db.isRunning());
        System.out.println("STOP TEST RESOURCE");
        db.stop();
    }
}