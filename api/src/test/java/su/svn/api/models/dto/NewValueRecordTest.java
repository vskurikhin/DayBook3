package su.svn.api.models.dto;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class NewValueRecordTest {

    @Test
    void shouldCreateRecord() {
        UUID id = UUID.randomUUID();

        NewValueRecord record = NewValueRecord.builder()
                .parentId(id)
                .title("title")
                .value("value")
                .postAt(OffsetDateTime.now())
                .visible(true)
                .flags(1)
                .tags(Set.of("tag"))
                .build();

        assertEquals(id, record.parentId());
        assertEquals("value", record.value());
        assertTrue(record.visible());
        assertEquals(1, record.flags());
    }
}