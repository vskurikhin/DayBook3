package su.svn.api.models.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class NewBlobRecordTest {

    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    @Test
    void shouldCreateRecordUsingBuilder() {
        var parentId = UUID.randomUUID();
        var postAt = OffsetDateTime.now();

        var record = NewBlobRecord.builder()
                .parentId(parentId)
                .title("title")
                .blob(new byte[]{1, 2, 3})
                .postAt(postAt)
                .visible(true)
                .flags(10)
                .tags(Set.of("a", "b"))
                .build();

        assertThat(record.parentId()).isEqualTo(parentId);
        assertThat(record.title()).isEqualTo("title");
        assertThat(record.blob()).containsExactly(1, 2, 3);
        assertThat(record.postAt()).isEqualTo(postAt);
        assertThat(record.visible()).isTrue();
        assertThat(record.flags()).isEqualTo(10);
        assertThat(record.tags()).containsExactlyInAnyOrder("a", "b");
    }

    @Test
    void shouldSerializeAndDeserialize() throws Exception {
        var record = NewBlobRecord.builder()
                .parentId(UUID.randomUUID())
                .title("test")
                .blob(new byte[]{1, 2})
                .postAt(OffsetDateTime.now())
                .visible(true)
                .flags(5)
                .tags(Set.of("x"))
                .build();

        var json = mapper.writeValueAsString(record);

        var restored = mapper.readValue(json, NewBlobRecord.class);

        assertThat(restored.parentId()).isEqualTo(record.parentId());
        assertThat(restored.title()).isEqualTo(record.title());

        // byte[] compare by content
        assertThat(restored.blob())
                .containsExactly(record.blob());

        // compare instant instead of offset
        assertThat(restored.postAt().toInstant())
                .isEqualTo(record.postAt().toInstant());

        assertThat(restored.visible()).isEqualTo(record.visible());
        assertThat(restored.flags()).isEqualTo(record.flags());
        assertThat(restored.tags())
                .containsExactlyInAnyOrderElementsOf(record.tags());
    }
}