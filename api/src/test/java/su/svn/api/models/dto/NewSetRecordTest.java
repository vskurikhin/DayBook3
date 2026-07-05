package su.svn.api.models.dto;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class NewSetRecordTest {

    @Test
    void shouldCreateRecord() {
        UUID id = UUID.randomUUID();

        NewSetRecord record = NewSetRecord.builder()
                .parentId(id)
                .title("title")
                .texts(Set.of("value"))
                .postAt(OffsetDateTime.now())
                .visible(true)
                .flags(1)
                .tags(Set.of("tag"))
                .build();

        assertEquals(id, record.parentId());
        assertTrue(record.texts().contains("value"));
        assertEquals(1, record.texts().size());
        assertTrue(record.visible());
        assertEquals(1, record.flags());
    }
}