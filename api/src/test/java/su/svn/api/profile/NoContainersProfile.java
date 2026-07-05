package su.svn.api.profile;

import io.quarkus.test.junit.QuarkusTestProfile;

import java.util.Map;
import java.util.Set;

public class NoContainersProfile implements QuarkusTestProfile {
    @Override
    public Map<String, String> getConfigOverrides() {
        return Map.of(
                "quarkus.devservices.enabled", "false",
                "quarkus.hibernate-orm.enabled", "true"
        );
    }

    @Override
    public Set<Class<?>> getEnabledAlternatives() {
        return Set.of(MockPersistenceUnit.class);
    }
}
