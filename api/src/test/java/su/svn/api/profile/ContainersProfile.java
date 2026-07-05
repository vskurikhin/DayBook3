package su.svn.api.profile;

import io.quarkus.test.junit.QuarkusTestProfile;

import java.util.Map;

public class ContainersProfile implements QuarkusTestProfile {
    @Override
    public Map<String, String> getConfigOverrides() {
        return Map.of(
                "quarkus.hibernate-orm.enabled", "true",
                "quarkus.datasource.reactive.enabled", "true",
                "quarkus.datasource.username", "test",
                "quarkus.datasource.password", "test"

        );
    }
}
