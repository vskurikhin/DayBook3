package su.svn.api.models.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UpdateBlobRecordTest {

    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    @Test
    void shouldBuildRecord() {
        var now = OffsetDateTime.now();

        var record = UpdateBlobRecord.builder()
                .id(UUID.randomUUID())
                .parentId(UUID.randomUUID())
                .title("updated")
                .blob(new byte[]{9})
                .postAt(now)
                .refreshAt(now)
                .visible(true)
                .flags(7)
                .tags(Set.of("tag"))
                .build();

        assertThat(record.title()).isEqualTo("updated");
        assertThat(record.flags()).isEqualTo(7);
    }

    @Test
    void shouldSerializeDeserialize() throws Exception {
        var record = UpdateBlobRecord.builder()
                .id(UUID.randomUUID())
                .refreshAt(OffsetDateTime.now())
                .build();

        var json = mapper.writeValueAsString(record);

        var restored = mapper.readValue(json, UpdateBlobRecord.class);

        assertThat(restored.id())
                .isEqualTo(record.id());

        assertThat(restored.parentId())
                .isEqualTo(record.parentId());

        assertThat(restored.title())
                .isEqualTo(record.title());

        assertThat(restored.postAt())
                .isEqualTo(record.postAt());

        // IMPORTANT
        assertThat(restored.refreshAt().toInstant())
                .isEqualTo(record.refreshAt().toInstant());

        assertThat(restored.visible())
                .isEqualTo(record.visible());

        assertThat(restored.flags())
                .isEqualTo(record.flags());

        assertThat(restored.tags())
                .isEqualTo(record.tags());
    }
}