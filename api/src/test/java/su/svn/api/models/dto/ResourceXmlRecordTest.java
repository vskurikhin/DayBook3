package su.svn.api.models.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ResourceXmlRecordTest {

    private final ObjectMapper mapper =
            new ObjectMapper().findAndRegisterModules();

    @Test
    void shouldBuildRecord() {
        var record = ResourceXmlRecord.builder()
                .id(UUID.randomUUID())
                .parentId(UUID.randomUUID())
                .title("resource")
                .xml("<root/>")
                .userName("admin")
                .postAt(OffsetDateTime.now())
                .refreshAt(OffsetDateTime.now())
                .visible(true)
                .flags(7)
                .build();

        assertThat(record.title()).isEqualTo("resource");
        assertThat(record.xml()).isEqualTo("<root/>");
        assertThat(record.userName()).isEqualTo("admin");
    }

    @Test
    void shouldIgnoreUserNameInJson() throws Exception {
        var record = ResourceXmlRecord.builder()
                .id(UUID.randomUUID())
                .xml("<root/>")
                .userName("hidden")
                .build();

        var json = mapper.writeValueAsString(record);

        assertThat(json).doesNotContain("userName");
    }
}