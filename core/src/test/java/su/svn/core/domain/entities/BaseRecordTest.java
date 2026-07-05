package su.svn.core.domain.entities;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BaseRecordTest {

    @Test
    void shouldCreateBaseRecordWithBuilder() {
        UUID parentId = UUID.randomUUID();

        BaseRecord record = BaseRecord.builder()
                .parentId(parentId)
                .userName("test_user")
                .visible(true)
                .flags(1)
                .build();

        assertThat(record).isNotNull();
        assertThat(record.parentId()).isEqualTo(parentId);
        assertThat(record.userName()).isEqualTo("test_user");
        assertThat(record.visible()).isTrue();
        assertThat(record.flags()).isEqualTo(1);
    }

    @Test
    void shouldSetDefaultValues() {
        BaseRecord record = BaseRecord.builder()
                .parentId(UUID.randomUUID())
                .build();

        assertThat(record.type()).isEqualTo(su.svn.lib.RecordType.Base);
        assertThat(record.enabled()).isTrue();
        assertThat(record.localChange()).isTrue();
    }

    @Test
    void shouldUseSettersAndGetters() {
        BaseRecord record = new BaseRecord();

        UUID id = UUID.randomUUID();
        UUID parentId = UUID.randomUUID();

        record.id(id);
        record.parentId(parentId);
        record.userName("user");

        assertThat(record.id()).isEqualTo(id);
        assertThat(record.parentId()).isEqualTo(parentId);
        assertThat(record.userName()).isEqualTo("user");
    }

    @Test
    void shouldCompareEqualsIgnoringIdAndParent() {
        UUID parentId = UUID.randomUUID();

        BaseRecord r1 = BaseRecord.builder()
                .parentId(parentId)
                .userName("user")
                .build();

        BaseRecord r2 = BaseRecord.builder()
                .parentId(parentId)
                .userName("user")
                .build();

        assertThat(r1).isEqualTo(r2);
    }

    @Test
    void shouldNotBeEqualWhenDifferentFields() {
        BaseRecord r1 = BaseRecord.builder()
                .parentId(UUID.randomUUID())
                .userName("user1")
                .build();

        BaseRecord r2 = BaseRecord.builder()
                .parentId(UUID.randomUUID())
                .userName("user2")
                .build();

        assertThat(r1).isNotEqualTo(r2);
    }
}