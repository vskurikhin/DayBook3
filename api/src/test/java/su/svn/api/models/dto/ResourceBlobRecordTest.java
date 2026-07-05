package su.svn.api.models.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ResourceBlobRecordTest {

    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    @Test
    void shouldBuildRecord() {
        var now = OffsetDateTime.now();

        var record = ResourceBlobRecord.builder()
                .id(UUID.randomUUID())
                .parentId(UUID.randomUUID())
                .title("title")
                .blob(new byte[]{1})
                .userName("admin")
                .postAt(now)
                .refreshAt(now)
                .visible(true)
                .flags(1)
                .build();

        assertThat(record.title()).isEqualTo("title");
        assertThat(record.userName()).isEqualTo("admin");
        assertThat(record.visible()).isTrue();
    }

    @Test
    void shouldIgnoreUserNameInJson() throws Exception {
        var record = ResourceBlobRecord.builder()
                .id(UUID.randomUUID())
                .userName("secret")
                .build();

        var json = mapper.writeValueAsString(record);

        assertThat(json).doesNotContain("userName");
    }
}