package su.svn.core.services.mappers;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import su.svn.core.domain.entities.BaseRecord;
import su.svn.core.domain.entities.Tag;
import su.svn.core.domain.entities.XmlRecord;
import su.svn.core.models.dto.NewXmlRecord;
import su.svn.core.models.dto.ResourceXmlRecord;
import su.svn.core.models.dto.UpdateXmlRecord;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class XmlRecordMapperTest {

    private XmlRecordMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(XmlRecordMapper.class);
    }

    @Test
    void shouldMapEntityToResource() {
        UUID id = UUID.randomUUID();
        UUID parentId = UUID.randomUUID();

        BaseRecord baseRecord = BaseRecord.builder()
                .id(id)
                .parentId(parentId)
                .postAt(OffsetDateTime.now())
                .refreshAt(OffsetDateTime.now())
                .tags(Lists.list(Tag.builder().tag("xml").build()))
                .title("title")
                .aHref("<a href=''></a>")
                .build();

        XmlRecord entity = XmlRecord.builder()
                .id(id)
                .baseRecord(baseRecord)
                .xml("<root/>")
                .userName("user")
                .visible(true)
                .flags(1)
                .build();

        ResourceXmlRecord result = mapper.toResource(entity);

        assertNotNull(result);
        assertEquals(id, result.id());
        assertEquals(parentId, result.parentId());
        assertEquals(entity.baseRecord().title(), result.title());
        assertEquals(entity.baseRecord().aHref(), result.aHref());
        assertEquals("<root/>", result.xml());
        assertTrue(result.visible());
        assertEquals(1, result.flags());
        assertEquals(Set.of("xml"), result.tags());
    }

    @Test
    void shouldMapNewRecordToResource() {
        OffsetDateTime postAt = OffsetDateTime.now();

        NewXmlRecord dto = NewXmlRecord.builder()
                .parentId(UUID.randomUUID())
                .title("new")
                .aHref("<a href=''></a>")
                .xml("<root/>")
                .postAt(postAt)
                .visible(true)
                .flags(2)
                .tags(Set.of("tag"))
                .build();

        ResourceXmlRecord result = mapper.toResource(dto);

        assertNotNull(result);
        assertEquals(dto.parentId(), result.parentId());
        assertEquals(dto.title(), result.title());
        assertEquals("<a href=''></a>", result.aHref());
        assertEquals(dto.xml(), result.xml());
        assertEquals(dto.postAt(), result.postAt());
        assertEquals(dto.visible(), result.visible());
        assertEquals(dto.flags(), result.flags());
    }

    @Test
    void shouldMapUpdateRecordToResource() {
        UUID id = UUID.randomUUID();

        UpdateXmlRecord dto = UpdateXmlRecord.builder()
                .id(id)
                .parentId(UUID.randomUUID())
                .title("updated")
                .aHref("<a href='updated'></a>")
                .xml("<updated/>")
                .postAt(OffsetDateTime.now())
                .refreshAt(OffsetDateTime.now())
                .visible(true)
                .flags(3)
                .tags(Set.of("updated"))
                .build();

        ResourceXmlRecord result = mapper.toResource(dto);

        assertNotNull(result);
        assertEquals(dto.id(), result.id());
        assertEquals(dto.parentId(), result.parentId());
        assertEquals(dto.title(), result.title());
        assertEquals("<a href='updated'></a>", result.aHref());
        assertEquals(dto.xml(), result.xml());
        assertEquals(dto.flags(), result.flags());
    }

    @Test
    void shouldMapResourceToEntity() {
        UUID id = UUID.randomUUID();
        UUID parentId = UUID.randomUUID();

        ResourceXmlRecord resource = ResourceXmlRecord.builder()
                .id(id)
                .parentId(parentId)
                .title("resource")
                .aHref("<a href=''></a>")
                .xml("<root/>")
                .postAt(OffsetDateTime.now())
                .refreshAt(OffsetDateTime.now())
                .visible(true)
                .flags(7)
                .tags(Set.of("xml"))
                .build();

        XmlRecord result = mapper.toEntity(resource);

        assertNotNull(result);
        assertEquals(resource.id(), result.id());
        assertEquals(resource.title(), result.baseRecord().title());
        assertEquals("<a href=''></a>", result.baseRecord().aHref());
        assertEquals(resource.xml(), result.xml());
        assertTrue(result.visible());
        assertEquals(resource.flags(), result.flags());

        assertNotNull(result.baseRecord());
        assertEquals(resource.id(), result.baseRecord().id());
        assertEquals(resource.parentId(), result.baseRecord().parentId());
        assertEquals(resource.postAt(), result.baseRecord().postAt());
        assertEquals(resource.refreshAt(), result.baseRecord().refreshAt());
        assertArrayEquals(resource.tags().toArray(), result.baseRecord().tags().stream().map(Tag::tag).toArray());
    }
}