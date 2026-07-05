package su.svn.core.services.mappers;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import su.svn.core.domain.entities.BaseRecord;
import su.svn.core.domain.entities.TextRecord;
import su.svn.core.models.dto.NewValueRecord;
import su.svn.core.models.dto.ResourceValueRecord;
import su.svn.core.models.dto.UpdateValueRecord;
import su.svn.lib.RecordType;
import su.svn.lib.TextRecordType;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValueRecordMapperTest {

    private final ValueRecordMapper mapper =
            Mappers.getMapper(ValueRecordMapper.class);

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

        ResourceValueRecord result = mapper.toResource(entity);

        assertEquals(entity.id(), result.id());
        assertEquals(entity.baseRecord().title(), result.title());
        assertEquals(entity.baseRecord().aHref(), result.aHref());
        assertEquals(entity.value(), result.value());
        assertEquals(entity.visible(), result.visible());
    }

    @Test
    void shouldMapNewRecordToResource() {
        NewValueRecord dto = NewValueRecord.builder()
                .title("title")
                .aHref("<a href=''></a>")
                .value("# text")
                .tags(Set.of("one"))
                .build();

        ResourceValueRecord result = mapper.toResource(dto);

        assertEquals("title", result.title());
        assertEquals("<a href=''></a>", result.aHref());
        assertEquals("# text", result.value());
        assertEquals(Set.of("one"), result.tags());
    }

    @Test
    void shouldMapUpdateRecordToResource() {
        UUID id = UUID.randomUUID();

        UpdateValueRecord dto = UpdateValueRecord.builder()
                .id(id)
                .title("updated")
                .aHref("<a href='updated'></a>")
                .value("content")
                .build();

        ResourceValueRecord result = mapper.toResource(dto);

        assertEquals(id, result.id());
        assertEquals("updated", result.title());
        assertEquals("<a href='updated'></a>", result.aHref());
        assertEquals("content", result.value());
    }

    @Test
    void shouldMapResourceToEntity() {
        UUID id = UUID.randomUUID();

        ResourceValueRecord dto = ResourceValueRecord.builder()
                .id(id)
                .title("title")
                .aHref("<a href=''></a>")
                .value("# value")
                .visible(true)
                .flags(10)
                .build();

        TextRecord result = mapper.toEntity(dto);

        assertEquals(id, result.id());
        assertEquals("title", result.baseRecord().title());
        assertEquals("<a href=''></a>", result.baseRecord().aHref());
        assertEquals("# value", result.value());
        assertTrue(result.visible());
        assertEquals(10, result.flags());
    }
}