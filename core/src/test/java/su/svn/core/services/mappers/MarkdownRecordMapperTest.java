package su.svn.core.services.mappers;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import su.svn.core.domain.entities.BaseRecord;
import su.svn.core.domain.entities.TextRecord;
import su.svn.core.models.dto.NewMarkdownRecord;
import su.svn.core.models.dto.ResourceMarkdownRecord;
import su.svn.core.models.dto.UpdateMarkdownRecord;
import su.svn.lib.RecordType;
import su.svn.lib.TextRecordType;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MarkdownRecordMapperTest {

    private final MarkdownRecordMapper mapper =
            Mappers.getMapper(MarkdownRecordMapper.class);

    @Test
    void shouldMapEntityToResource() {
        UUID id = UUID.randomUUID();

        BaseRecord baseRecord = BaseRecord.builder()
                .id(id)
                .parentId(UUID.randomUUID())
                .type(RecordType.Text)
                .tags(List.of())
                .postAt(OffsetDateTime.now())
                .refreshAt(OffsetDateTime.now())
                .title("title")
                .aHref("<a href=''></a>")
                .build();

        TextRecord entity = TextRecord.builder()
                .id(id)
                .baseRecord(baseRecord)
                .value("# markdown")
                .type(TextRecordType.Markdown)
                .userName("root")
                .visible(true)
                .flags(1)
                .build();

        ResourceMarkdownRecord result = mapper.toResource(entity);

        assertEquals(entity.id(), result.id());
        assertEquals(entity.baseRecord().title(), result.title());
        assertEquals(entity.baseRecord().aHref(), result.aHref());
        assertEquals(entity.value(), result.markdown());
        assertEquals(entity.visible(), result.visible());
    }

    @Test
    void shouldMapNewRecordToResource() {
        NewMarkdownRecord dto = NewMarkdownRecord.builder()
                .title("title")
                .markdown("# text")
                .aHref("<a href=''></a>")
                .tags(Set.of("one"))
                .build();

        ResourceMarkdownRecord result = mapper.toResource(dto);

        assertEquals("title", result.title());
        assertEquals("<a href=''></a>", result.aHref());
        assertEquals("# text", result.markdown());
        assertEquals(Set.of("one"), result.tags());
    }

    @Test
    void shouldMapUpdateRecordToResource() {
        UUID id = UUID.randomUUID();

        UpdateMarkdownRecord dto = UpdateMarkdownRecord.builder()
                .id(id)
                .title("updated")
                .aHref("<a href='updated'></a>")
                .markdown("content")
                .build();

        ResourceMarkdownRecord result = mapper.toResource(dto);

        assertEquals(id, result.id());
        assertEquals("updated", result.title());
        assertEquals("<a href='updated'></a>", result.aHref());
        assertEquals("content", result.markdown());
    }

    @Test
    void shouldMapResourceToEntity() {
        UUID id = UUID.randomUUID();

        ResourceMarkdownRecord dto = ResourceMarkdownRecord.builder()
                .id(id)
                .title("title")
                .aHref("<a href=''></a>")
                .markdown("# markdown")
                .visible(true)
                .flags(10)
                .build();

        TextRecord result = mapper.toEntity(dto);

        assertEquals(id, result.id());
        assertEquals("title", result.baseRecord().title());
        assertEquals("<a href=''></a>", result.baseRecord().aHref());
        assertEquals("# markdown", result.value());
        assertTrue(result.visible());
        assertEquals(10, result.flags());
    }
}