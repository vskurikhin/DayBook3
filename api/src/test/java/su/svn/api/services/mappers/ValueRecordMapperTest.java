package su.svn.api.services.mappers;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import su.svn.api.domain.entities.PostRecord;
import su.svn.api.models.dto.ResourceValueRecord;
import su.svn.api.models.dto.UpdateValueRecord;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ValueRecordMapperTest {

    private final ValueRecordMapper mapper =
            Mappers.getMapper(ValueRecordMapper.class);

    @Test
    void shouldMapUpdateDtoToEntity() {
        UUID id = UUID.randomUUID();

        UpdateValueRecord dto = UpdateValueRecord.builder()
                .id(id)
                .parentId(id)
                .title("title")
                .value("value")
                .postAt(OffsetDateTime.now())
                .refreshAt(OffsetDateTime.now())
                .tags(Set.of("one"))
                .build();

        PostRecord entity = mapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals(dto.id(), entity.id());
        assertEquals(dto.value(), entity.value());
        assertEquals(dto.title(), entity.title());
    }

    @Test
    void shouldMapEntityToResource() {
        UUID id = UUID.randomUUID();

        PostRecord entity = PostRecord.builder()
                .id(id)
                .parentId(id)
                .title("title")
                .value("value")
                .build();

        ResourceValueRecord resource = mapper.toResource(entity);

        assertNotNull(resource);
        assertEquals(entity.id(), resource.id());
        assertEquals(entity.value(), resource.value());
        assertEquals(entity.title(), resource.title());
    }
}