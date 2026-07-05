package su.svn.api.models.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class NewVectorRecordTest {

    private final ObjectMapper mapper =
            new ObjectMapper().findAndRegisterModules();

    @Test
    void shouldBuildRecord() {
        var now = OffsetDateTime.now();

        var record = NewVectorRecord.builder()
                .parentId(UUID.randomUUID())
                .title("vector")
                .vector(new float[]{1.0f, 2.0f})
                .postAt(now)
                .visible(true)
                .flags(10)
                .tags(Set.of("ml"))
                .build();

        assertThat(record.title()).isEqualTo("vector");
        assertThat(record.vector()).containsExactly(1.0f, 2.0f);
        assertThat(record.visible()).isTrue();
        assertThat(record.flags()).isEqualTo(10);
    }

    @Test
    void shouldSerializeDeserialize() throws Exception {
        var record = NewVectorRecord.builder()
                .parentId(UUID.randomUUID())
                .title("test")
                .vector(new float[]{1.5f, 2.5f})
                .postAt(OffsetDateTime.now())
                .visible(true)
                .flags(5)
                .tags(Set.of("x"))
                .build();

        var json = mapper.writeValueAsString(record);

        var restored = mapper.readValue(json, NewVectorRecord.class);

        assertThat(restored.parentId()).isEqualTo(record.parentId());
        assertThat(restored.title()).isEqualTo(record.title());

        assertThat(restored.vector())
                .containsExactly(record.vector());

        assertThat(restored.postAt().toInstant())
                .isEqualTo(record.postAt().toInstant());

        assertThat(restored.visible()).isEqualTo(record.visible());
        assertThat(restored.flags()).isEqualTo(record.flags());
        assertThat(restored.tags())
                .containsExactlyInAnyOrderElementsOf(record.tags());
    }
}