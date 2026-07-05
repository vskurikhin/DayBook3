package su.svn.api.services.mappers;

import org.junit.jupiter.api.Test;
import su.svn.api.domain.entities.PostRecord;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PostRecordMapperTest {

    @Test
    void shouldCopyMutableFields() {

        UUID id = UUID.randomUUID();

        LocalDateTime originalLastChanged = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime newLastChanged = LocalDateTime.of(2025, 1, 1, 10, 0);

        PostRecord target = PostRecord.builder()
                .sequenceId(100L)
                .id(id)
                .title("old title")
                .value("old value")
                .userName("old user")
                .enabled(true)
                .lastChangedTime(originalLastChanged)
                .build();

        PostRecord source = PostRecord.builder()
                .sequenceId(999L)
                .id(id)
                .title("new title")
                .value("new value")
                .userName("new user")
                .enabled(false)
                .lastChangedTime(newLastChanged)
                .build();

        PostRecordMapper.INSTANCE.update(target, source);

        assertEquals("new title", target.title());
        assertEquals("new value", target.value());
        assertEquals("new user", target.userName());
        assertFalse(target.enabled());

        // ignored fields
        assertEquals(100L, target.sequenceId());
        assertEquals(originalLastChanged, target.lastChangedTime());
    }

    @Test
    void shouldNotUpdateIgnoredFields() {

        LocalDateTime original = LocalDateTime.now().minusDays(1);

        PostRecord target = PostRecord.builder()
                .sequenceId(1L)
                .lastChangedTime(original)
                .build();

        PostRecord source = PostRecord.builder()
                .sequenceId(999L)
                .lastChangedTime(LocalDateTime.now())
                .build();

        PostRecordMapper.INSTANCE.update(target, source);

        assertEquals(1L, target.sequenceId());
        assertEquals(original, target.lastChangedTime());
    }

    @Test
    void shouldCopyNullValues() {

        PostRecord target = PostRecord.builder()
                .title("title")
                .value("value")
                .build();

        PostRecord source = PostRecord.builder()
                .title(null)
                .value(null)
                .build();

        PostRecordMapper.INSTANCE.update(target, source);

        assertNull(target.title());
        assertNull(target.value());
    }
}