package su.svn.api.models.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ResourceVectorRecordTest {

    private final ObjectMapper mapper =
            new ObjectMapper().findAndRegisterModules();

    @Test
    void shouldBuildRecord() {
        var record = ResourceVectorRecord.builder()
                .id(UUID.randomUUID())
                .parentId(UUID.randomUUID())
                .title("resource")
                .vector(new float[]{3.0f})
                .userName("admin")
                .postAt(OffsetDateTime.now())
                .refreshAt(OffsetDateTime.now())
                .visible(true)
                .flags(1)
                .build();

        assertThat(record.title()).isEqualTo("resource");
        assertThat(record.vector()).containsExactly(3.0f);
        assertThat(record.userName()).isEqualTo("admin");
    }

    @Test
    void shouldIgnoreUserNameInJson() throws Exception {
        var record = ResourceVectorRecord.builder()
                .id(UUID.randomUUID())
                .userName("hidden")
                .vector(new float[]{1.0f})
                .build();

        var json = mapper.writeValueAsString(record);

        assertThat(json).doesNotContain("userName");
    }
}