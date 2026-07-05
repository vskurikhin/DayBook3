package su.svn.api.models.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class NewXmlRecordTest {

    private final ObjectMapper mapper =
            new ObjectMapper().findAndRegisterModules();

    @Test
    void shouldBuildRecord() {
        var record = NewXmlRecord.builder()
                .parentId(UUID.randomUUID())
                .title("xml")
                .xml("<root/>")
                .postAt(OffsetDateTime.now())
                .visible(true)
                .flags(5)
                .tags(Set.of("xml"))
                .build();

        assertThat(record.title()).isEqualTo("xml");
        assertThat(record.xml()).isEqualTo("<root/>");
        assertThat(record.visible()).isTrue();
        assertThat(record.flags()).isEqualTo(5);
    }

    @Test
    void shouldSerializeDeserialize() throws Exception {
        var record = NewXmlRecord.builder()
                .parentId(UUID.randomUUID())
                .title("test")
                .xml("<xml>value</xml>")
                .postAt(OffsetDateTime.now())
                .visible(true)
                .flags(1)
                .tags(Set.of("x"))
                .build();

        var json = mapper.writeValueAsString(record);

        var restored = mapper.readValue(json, NewXmlRecord.class);

        assertThat(restored.parentId()).isEqualTo(record.parentId());
        assertThat(restored.title()).isEqualTo(record.title());
        assertThat(restored.xml()).isEqualTo(record.xml());

        assertThat(restored.postAt().toInstant())
                .isEqualTo(record.postAt().toInstant());

        assertThat(restored.visible()).isEqualTo(record.visible());
        assertThat(restored.flags()).isEqualTo(record.flags());

        assertThat(restored.tags())
                .containsExactlyInAnyOrderElementsOf(record.tags());
    }
}