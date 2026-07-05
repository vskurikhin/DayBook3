package su.svn.api.models.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import su.svn.lib.models.dto.Link;

import static org.assertj.core.api.Assertions.assertThat;

class LinkTest {

    private final ObjectMapper mapper =
            new ObjectMapper().findAndRegisterModules();

    @Test
    void shouldBuildLink() {
        var link = Link.builder()
                .href("https://example.com")
                .hreflang("en")
                .title("Example")
                .type("GET")
                .deprecation("false")
                .profile("default")
                .name("self")
                .templated(true)
                .build();

        assertThat(link.href()).isEqualTo("https://example.com");
        assertThat(link.hreflang()).isEqualTo("en");
        assertThat(link.title()).isEqualTo("Example");
        assertThat(link.type()).isEqualTo("GET");
        assertThat(link.deprecation()).isEqualTo("false");
        assertThat(link.profile()).isEqualTo("default");
        assertThat(link.name()).isEqualTo("self");
        assertThat(link.templated()).isTrue();
    }

    @Test
    void shouldSerializeDeserialize() throws Exception {
        var link = Link.builder()
                .href("https://example.com")
                .title("Example")
                .templated(false)
                .build();

        var json = mapper.writeValueAsString(link);

        var restored = mapper.readValue(json, Link.class);

        assertThat(restored).isEqualTo(link);
    }

    @Test
    void shouldIgnoreNullFields() throws Exception {
        var link = Link.builder()
                .href("https://example.com")
                .build();

        var json = mapper.writeValueAsString(link);

        assertThat(json).contains("href");
        assertThat(json).doesNotContain("title");
        assertThat(json).doesNotContain("type");
    }
}