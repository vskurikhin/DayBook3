package su.svn.api.services.mappers;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import su.svn.api.domain.entities.PostRecord;
import su.svn.api.models.dto.ResourceMarkdownRecord;
import su.svn.api.models.dto.UpdateMarkdownRecord;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MarkdownRecordMapperTest {

    private final MarkdownRecordMapper mapper =
            Mappers.getMapper(MarkdownRecordMapper.class);

    @Test
    void shouldMapUpdateDtoToEntity() {
        UUID id = UUID.randomUUID();

        UpdateMarkdownRecord dto = UpdateMarkdownRecord.builder()
                .id(id)
                .parentId(id)
                .title("title")
                .markdown("markdown")
                .postAt(OffsetDateTime.now())
                .refreshAt(OffsetDateTime.now())
                .tags(Set.of("one"))
                .build();

        PostRecord entity = mapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals(dto.id(), entity.id());
        assertEquals(dto.markdown(), entity.markdown());
        assertEquals(dto.title(), entity.title());
    }

    @Test
    void shouldMapEntityToResource() {
        UUID id = UUID.randomUUID();

        PostRecord entity = PostRecord.builder()
                .id(id)
                .parentId(id)
                .title("title")
                .markdown("markdown")
                .build();

        ResourceMarkdownRecord resource = mapper.toResource(entity);

        assertNotNull(resource);
        assertEquals(entity.id(), resource.id());
        assertEquals(entity.markdown(), resource.markdown());
        assertEquals(entity.title(), resource.title());
    }
}