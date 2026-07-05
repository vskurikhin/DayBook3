package su.svn.api.models.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UpdateXmlRecordTest {

    private final ObjectMapper mapper =
            new ObjectMapper().findAndRegisterModules();

    @Test
    void shouldBuildRecord() {
        var record = UpdateXmlRecord.builder()
                .id(UUID.randomUUID())
                .title("updated")
                .xml("<updated/>")
                .refreshAt(OffsetDateTime.now())
                .visible(true)
                .flags(9)
                .tags(Set.of("xml"))
                .build();

        assertThat(record.title()).isEqualTo("updated");
        assertThat(record.xml()).isEqualTo("<updated/>");
        assertThat(record.visible()).isTrue();
        assertThat(record.flags()).isEqualTo(9);
    }

    @Test
    void shouldSerializeDeserialize() throws Exception {
        var record = UpdateXmlRecord.builder()
                .id(UUID.randomUUID())
                .title("test")
                .xml("<xml/>")
                .refreshAt(OffsetDateTime.now())
                .tags(Set.of("x"))
                .build();

        var json = mapper.writeValueAsString(record);

        var restored = mapper.readValue(json, UpdateXmlRecord.class);

        assertThat(restored.id()).isEqualTo(record.id());
        assertThat(restored.title()).isEqualTo(record.title());
        assertThat(restored.xml()).isEqualTo(record.xml());

        assertThat(restored.refreshAt().toInstant())
                .isEqualTo(record.refreshAt().toInstant());

        assertThat(restored.tags())
                .containsExactlyInAnyOrderElementsOf(record.tags());
    }
}