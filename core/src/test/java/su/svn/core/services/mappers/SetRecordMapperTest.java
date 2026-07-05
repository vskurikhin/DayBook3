package su.svn.core.services.mappers;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import su.svn.core.domain.entities.BaseRecord;
import su.svn.core.domain.entities.SetRecord;
import su.svn.core.domain.entities.Tag;
import su.svn.core.models.dto.NewSetRecord;
import su.svn.core.models.dto.ResourceSetRecord;
import su.svn.core.models.dto.UpdateSetRecord;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SetRecordMapperTest {

    private final SetRecordMapper mapper = Mappers.getMapper(SetRecordMapper.class);

    @Test
    void shouldMapEntityToResource() {
        UUID id = UUID.randomUUID();

        BaseRecord baseRecord = BaseRecord.builder()
                .id(id)
                .parentId(UUID.randomUUID())
                .tags(Set.of(
                        Tag.builder().tag("tag1").build()
                ).stream().toList())
                .postAt(OffsetDateTime.now())
                .refreshAt(OffsetDateTime.now())
                .title("title")
                .aHref("<a href=''></a>")
                .build();

        SetRecord entity = SetRecord.builder()
                .id(id)
                .userName("user")
                .texts(Set.of("a", "b"))
                .baseRecord(baseRecord)
                .build();

        ResourceSetRecord result = mapper.toResource(entity);

        assertNotNull(result);
        assertEquals(entity.id(), result.id());
        assertEquals(entity.baseRecord().title(), result.title());
        assertEquals(entity.baseRecord().aHref(), result.aHref());
        assertEquals(entity.texts(), result.texts());
    }

    @Test
    void shouldMapNewRecordToResource() {
        NewSetRecord dto = NewSetRecord.builder()
                .title("title")
                .aHref("<a href=''></a>")
                .texts(Set.of("one"))
                .tags(Set.of("tag"))
                .build();

        ResourceSetRecord result = mapper.toResource(dto);

        assertNotNull(result);
        assertEquals(dto.title(), result.title());
        assertEquals("<a href=''></a>", result.aHref());
        assertEquals(dto.texts(), result.texts());
    }

    @Test
    void shouldMapUpdateRecordToResource() {
        UUID id = UUID.randomUUID();

        UpdateSetRecord dto = UpdateSetRecord.builder()
                .id(id)
                .title("updated")
                .aHref("<a href='updated'></a>")
                .texts(Set.of("value"))
                .build();

        ResourceSetRecord result = mapper.toResource(dto);

        assertNotNull(result);
        assertEquals(id, result.id());
        assertEquals("updated", result.title());
        assertEquals("<a href='updated'></a>", result.aHref());
    }

    @Test
    void shouldMapResourceToEntity() {
        UUID id = UUID.randomUUID();

        ResourceSetRecord dto = ResourceSetRecord.builder()
                .id(id)
                .parentId(UUID.randomUUID())
                .title("resource")
                .aHref("<a href=''></a>")
                .texts(Set.of("a"))
                .tags(Set.of("tag"))
                .build();

        SetRecord result = mapper.toEntity(dto);

        assertNotNull(result);
        assertEquals(dto.id(), result.id());
        assertEquals(dto.title(), result.baseRecord().title());
        assertEquals("<a href=''></a>", result.baseRecord().aHref());
        assertEquals(dto.texts(), result.texts());
    }
}