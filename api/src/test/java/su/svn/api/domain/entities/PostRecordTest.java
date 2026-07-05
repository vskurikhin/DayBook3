package su.svn.api.domain.entities;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import org.junit.jupiter.api.Test;
import su.svn.PostgreSQLTestResource;
import su.svn.api.profile.ContainersProfile;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@QuarkusTestResource(value = PostgreSQLTestResource.class)
@TestProfile(ContainersProfile.class)
class PostRecordTest {

    @Test
    void shouldBuildEntityWithDefaults() {
        var id = UUID.randomUUID();

        var entity = PostRecord.builder()
                .id(id)
                .build();

        assertEquals(id, entity.id());
        assertTrue(entity.enabled());
        assertTrue(entity.localChange());
        assertEquals(su.svn.lib.RecordType.Base, entity.type());
    }

    @Test
    void shouldSetAndGetFields() {
        var now = LocalDateTime.now();

        var entity = new PostRecord();

        entity.title("title");
        entity.value("value");
        entity.lastChangedTime(now);
        entity.enabled(false);

        assertEquals("title", entity.title());
        assertEquals("value", entity.value());
        assertEquals(now, entity.lastChangedTime());
        assertFalse(entity.enabled());
    }

    @Test
    void shouldContainEnabledConstant() {
        assertTrue(PostRecord.ENABLED.containsKey("enabled"));
        assertEquals(Boolean.TRUE, PostRecord.ENABLED.get("enabled"));
    }

    @Test
    void shouldHaveTimeoutDuration() {
        assertEquals(2000, PostRecord.TIMEOUT_DURATION.toMillis());
    }

    @Test
    void shouldCreateVoidUni() {
        UniAssertSubscriber<Void> subscriber = io.smallrye.mutiny.Uni
                .createFrom()
                .voidItem()
                .subscribe()
                .withSubscriber(UniAssertSubscriber.create());

        subscriber.assertCompleted();
    }

    @Test
    void shouldCompareEntities() {
        var id = UUID.randomUUID();

        var first = PostRecord.builder()
                .id(id)
                .title("title")
                .build();

        var second = PostRecord.builder()
                .id(id)
                .title("title")
                .build();

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void shouldGenerateToString() {
        var entity = PostRecord.builder()
                .title("test")
                .value("value")
                .build();

        var result = entity.toString();

        assertNotNull(result);
        assertTrue(result.contains("test"));
    }
}