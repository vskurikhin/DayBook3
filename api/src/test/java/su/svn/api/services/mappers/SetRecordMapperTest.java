package su.svn.api.services.mappers;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import su.svn.api.domain.entities.PostRecord;
import su.svn.api.models.dto.ResourceSetRecord;
import su.svn.api.models.dto.UpdateSetRecord;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SetRecordMapperTest {

    private final SetRecordMapper mapper =
            Mappers.getMapper(SetRecordMapper.class);

    @Test
    void shouldMapUpdateDtoToEntity() {
        UUID id = UUID.randomUUID();

        UpdateSetRecord dto = UpdateSetRecord.builder()
                .id(id)
                .parentId(id)
                .title("title")
                .texts(Set.of("value"))
                .postAt(OffsetDateTime.now())
                .refreshAt(OffsetDateTime.now())
                .tags(Set.of("one"))
                .build();

        PostRecord entity = mapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals(dto.id(), entity.id());
        assertTrue(dto.texts().contains("value"));
        assertEquals(1, dto.texts().size());
        assertEquals(dto.title(), entity.title());
    }

    @Test
    void shouldMapEntityToResource() {
        UUID id = UUID.randomUUID();

        PostRecord entity = PostRecord.builder()
                .id(id)
                .parentId(id)
                .title("title")
                .texts(Set.of("value"))
                .build();

        ResourceSetRecord resource = mapper.toResource(entity);

        assertNotNull(resource);
        assertEquals(entity.id(), resource.id());
        assertTrue(entity.texts().contains("value"));
        assertEquals(1, entity.texts().size());
        assertEquals(entity.title(), resource.title());
    }
}