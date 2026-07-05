package su.svn.api.models.dto;

import org.junit.jupiter.api.Test;
import su.svn.lib.RecordType;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RecordDataTest {

    @Test
    void shouldCreateRecordDataUsingBuilder() {

        UUID id = UUID.randomUUID();
        UUID parentId = UUID.randomUUID();

        OffsetDateTime postAt = OffsetDateTime.now();
        OffsetDateTime refreshAt = OffsetDateTime.now();

        byte[] blob = new byte[]{1, 2, 3};

        RecordData record = RecordData.builder()
                .id(id)
                .parentId(parentId)
                .type(RecordType.Text)
                .postAt(postAt)
                .refreshAt(refreshAt)
                .visible(true)
                .flags(10)
                .title("title")
                .blob(blob)
                .json(Map.of("key", "value"))
                .texts(Set.of("one", "two"))
                .fileName("file.txt")
                .html("<b>html</b>")
                .link("https://example.com")
                .markdown("# markdown")
                .value("plain value")
                .build();

        assertNotNull(record);

        assertEquals(id, record.id());
        assertEquals(parentId, record.parentId());
        assertEquals(RecordType.Text, record.type());

        assertEquals(postAt, record.postAt());
        assertEquals(refreshAt, record.refreshAt());

        assertTrue(record.visible());
        assertEquals(10, record.flags());

        assertEquals("title", record.title());

        assertArrayEquals(blob, record.blob());

        assertEquals("value", record.json().get("key"));

        assertTrue(record.texts().contains("one"));
        assertTrue(record.texts().contains("two"));

        assertEquals("file.txt", record.fileName());
        assertEquals("<b>html</b>", record.html());
        assertEquals("https://example.com", record.link());
        assertEquals("# markdown", record.markdown());
        assertEquals("plain value", record.value());
    }

    @Test
    void shouldAllowNullOptionalFields() {

        RecordData record = RecordData.builder()
                .build();

        assertNotNull(record);

        assertNull(record.id());
        assertNull(record.parentId());
        assertNull(record.type());
        assertNull(record.postAt());
        assertNull(record.refreshAt());
        assertNull(record.visible());
        assertEquals(0, record.flags());

        assertNull(record.title());
        assertNull(record.blob());
        assertNull(record.json());
        assertNull(record.texts());
        assertNull(record.fileName());
        assertNull(record.html());
        assertNull(record.link());
        assertNull(record.markdown());
        assertNull(record.value());
    }
}