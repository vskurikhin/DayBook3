package su.svn.api.models.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UpdateVectorRecordTest {

    private final ObjectMapper mapper =
            new ObjectMapper().findAndRegisterModules();

    @Test
    void shouldBuildRecord() {
        var record = UpdateVectorRecord.builder()
                .id(UUID.randomUUID())
                .vector(new float[]{9.0f})
                .refreshAt(OffsetDateTime.now())
                .visible(true)
                .flags(7)
                .tags(Set.of("ai"))
                .build();

        assertThat(record.vector()).containsExactly(9.0f);
        assertThat(record.visible()).isTrue();
        assertThat(record.flags()).isEqualTo(7);
    }

    @Test
    void shouldSerializeDeserialize() throws Exception {
        var record = UpdateVectorRecord.builder()
                .id(UUID.randomUUID())
                .vector(new float[]{7.0f, 8.0f})
                .refreshAt(OffsetDateTime.now())
                .build();

        var json = mapper.writeValueAsString(record);

        var restored = mapper.readValue(json, UpdateVectorRecord.class);

        assertThat(restored.id()).isEqualTo(record.id());

        assertThat(restored.vector())
                .containsExactly(record.vector());

        assertThat(restored.refreshAt().toInstant())
                .isEqualTo(record.refreshAt().toInstant());
    }
}