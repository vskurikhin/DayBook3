package su.svn.api.services.mappers;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import su.svn.api.domain.entities.PostRecord;
import su.svn.api.models.dto.ResourceXmlRecord;
import su.svn.api.models.dto.UpdateXmlRecord;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class XmlRecordMapperTest {

    private final XmlRecordMapper mapper =
            Mappers.getMapper(XmlRecordMapper.class);

    @Test
    void shouldMapUpdateRecordToEntity() {
        var id = UUID.randomUUID();

        var record = UpdateXmlRecord.builder()
                .id(id)
                .parentId(UUID.randomUUID())
                .title("xml")
                .xml("<root/>")
                .postAt(OffsetDateTime.now())
                .refreshAt(OffsetDateTime.now())
                .visible(true)
                .flags(3)
                .tags(Set.of("xml"))
                .build();

        PostRecord entity = mapper.toEntity(record);

        assertThat(entity.id()).isEqualTo(record.id());
        assertThat(entity.parentId()).isEqualTo(record.parentId());
        assertThat(entity.title()).isEqualTo(record.title());
        assertThat(entity.xml()).isEqualTo(record.xml());
        assertThat(entity.postAt()).isEqualTo(record.postAt());
        assertThat(entity.refreshAt()).isEqualTo(record.refreshAt());
        assertThat(entity.visible()).isEqualTo(record.visible());
        assertThat(entity.flags()).isEqualTo(record.flags());
        assertThat(entity.tags())
                .containsExactlyInAnyOrderElementsOf(record.tags());
    }

    @Test
    void shouldMapEntityToResource() {
        var entity = PostRecord.builder()
                .id(UUID.randomUUID())
                .parentId(UUID.randomUUID())
                .title("resource")
                .xml("<data/>")
                .postAt(OffsetDateTime.now())
                .refreshAt(OffsetDateTime.now())
                .visible(true)
                .flags(11)
                .build();

        ResourceXmlRecord resource = mapper.toResource(entity);

        assertThat(resource.id()).isEqualTo(entity.id());
        assertThat(resource.parentId()).isEqualTo(entity.parentId());
        assertThat(resource.title()).isEqualTo(entity.title());
        assertThat(resource.xml()).isEqualTo(entity.xml());
        assertThat(resource.postAt()).isEqualTo(entity.postAt());
        assertThat(resource.refreshAt()).isEqualTo(entity.refreshAt());
        assertThat(resource.visible()).isEqualTo(entity.visible());
        assertThat(resource.flags()).isEqualTo(entity.flags());
    }
}