package su.svn.core.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;
import su.svn.core.TestcontainersConfiguration;
import su.svn.core.domain.entities.BaseRecord;
import su.svn.core.domain.entities.JsonRecord;
import su.svn.core.domain.entities.UserName;

import java.time.*;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.OFFSET_TIME;

@Import(TestcontainersConfiguration.class)
@DataJpaTest
class JpaTest {

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @Transactional
    void baseRecordShouldBePersistAndFlush() {
        BaseRecord record = BaseRecord.builder()
                .parentId(UUID.fromString("00000000-0000-0000-0000-000000000000"))
                .userName("root")
                .postAt(OffsetDateTime.of(LocalDateTime.now(), ZoneOffset.UTC))
                .build();

        BaseRecord saved = entityManager.persistAndFlush(record);
        entityManager.refresh(saved);

        assertThat(saved.id()).isNotNull();
        assertThat(saved.createTime()).isNotNull();
    }

    @Test
    @Transactional
    void jsonRecordShouldBePersistAndFlush() {
        BaseRecord baseRecord = BaseRecord.builder()
                .parentId(UUID.fromString("00000000-0000-0000-0000-000000000000"))
                .userName("root")
                .postAt(OffsetDateTime.of(LocalDateTime.now(), ZoneOffset.UTC))
                .build();
        JsonRecord jsonRecord = JsonRecord.builder()
                .baseRecord(baseRecord)
                .userName("root")
                .json(Map.of())
                .build();

        JsonRecord saved = entityManager.persistAndFlush(jsonRecord);

        assertThat(saved.id()).isNotNull();
        assertThat(saved.createTime()).isNotNull();
    }

    @Test
    @Transactional
    public void userNameShouldBePersistAndFlush()  {
        UserName userName = UserName.builder()
                .userName(UUID.randomUUID().toString())
                .id(UUID.randomUUID())
                .build();

        UserName saved = entityManager.persistAndFlush(userName);

        assertThat(saved.id()).isNotNull();
        assertThat(saved.createTime()).isNotNull();
    }
}