package su.svn.api.models.dto;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MarkdownRecordDtoTest {

    @Test
    void shouldCreateNewMarkdownRecord() {
        var id = UUID.randomUUID();
        var now = OffsetDateTime.now();

        var dto = NewMarkdownRecord.builder()
                .parentId(id)
                .title("title")
                .markdown("# markdown")
                .postAt(now)
                .visible(true)
                .flags(1)
                .tags(Set.of("a"))
                .build();

        assertEquals(id, dto.parentId());
        assertEquals("title", dto.title());
        assertEquals("# markdown", dto.markdown());
        assertEquals(now, dto.postAt());
        assertTrue(dto.visible());
        assertEquals(1, dto.flags());
        assertEquals(Set.of("a"), dto.tags());
    }

    @Test
    void shouldCreateResourceMarkdownRecord() {
        var id = UUID.randomUUID();

        var dto = ResourceMarkdownRecord.builder()
                .id(id)
                .markdown("text")
                .visible(true)
                .flags(2)
                .build();

        assertEquals(id, dto.id());
        assertEquals("text", dto.markdown());
        assertTrue(dto.visible());
        assertEquals(2, dto.flags());
    }

    @Test
    void shouldCreateUpdateMarkdownRecord() {
        var id = UUID.randomUUID();

        var dto = UpdateMarkdownRecord.builder()
                .id(id)
                .markdown("updated")
                .build();

        assertEquals(id, dto.id());
        assertEquals("updated", dto.markdown());
    }
}