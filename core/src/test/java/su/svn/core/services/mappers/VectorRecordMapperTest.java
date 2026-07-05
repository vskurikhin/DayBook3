package su.svn.core.services.mappers;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import su.svn.core.domain.entities.BaseRecord;
import su.svn.core.domain.entities.Tag;
import su.svn.core.domain.entities.VectorRecord;
import su.svn.core.models.dto.NewVectorRecord;
import su.svn.core.models.dto.ResourceVectorRecord;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class VectorRecordMapperTest {

    private final VectorRecordMapper mapper =
            Mappers.getMapper(VectorRecordMapper.class);

    @Test
    void shouldMapEntityToResource() {

        UUID id = UUID.randomUUID();

        BaseRecord baseRecord = BaseRecord.builder()
                .id(id)
                .parentId(UUID.randomUUID())
                .tags(Lists.list(Tag.builder().tag("vector").build()))
                .postAt(OffsetDateTime.now())
                .refreshAt(OffsetDateTime.now())
                .title("title")
                .aHref("<a href=''></a>")
                .build();

        VectorRecord entity = VectorRecord.builder()
                .id(id)
                .vector(new float[]{1.0f, 2.0f})
                .baseRecord(baseRecord)
                .userName("root")
                .build();

        ResourceVectorRecord result = mapper.toResource(entity);

        assertEquals(entity.id(), result.id());
        assertEquals(entity.baseRecord().title(), result.title());
        assertEquals(entity.baseRecord().aHref(), result.aHref());
        assertArrayEquals(entity.vector(), result.vector());
        assertArrayEquals(new String[]{"vector"}, result.tags().toArray());
    }

    @Test
    void shouldMapNewRecordToResource() {

        NewVectorRecord record = NewVectorRecord.builder()
                .title("vector")
                .aHref("<a href=''></a>")
                .vector(new float[]{3.0f})
                .tags(Set.of("vector"))
                .build();

        ResourceVectorRecord result = mapper.toResource(record);

        assertEquals(record.title(), result.title());
        assertEquals("<a href=''></a>", result.aHref());
        assertArrayEquals(record.vector(), result.vector());
        assertEquals(record.tags(), result.tags());
    }

    @Test
    void shouldMapResourceToEntity() {

        UUID id = UUID.randomUUID();

        ResourceVectorRecord resource = ResourceVectorRecord.builder()
                .id(id)
                .parentId(UUID.randomUUID())
                .title("vector")
                .aHref("<a href=''></a>")
                .vector(new float[]{5.0f})
                .tags(Set.of("vector"))
                .build();

        VectorRecord result = mapper.toEntity(resource);

        assertEquals(resource.id(), result.id());
        assertEquals(resource.title(), result.baseRecord().title());
        assertEquals("<a href=''></a>", result.baseRecord().aHref());
        assertArrayEquals(resource.vector(), result.vector());

        assertNotNull(result.baseRecord());
        assertEquals(resource.parentId(), result.baseRecord().parentId());
    }
}