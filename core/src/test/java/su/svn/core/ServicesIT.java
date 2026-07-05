package su.svn.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;
import su.svn.core.domain.entities.UserName;
import su.svn.core.models.dto.*;
import su.svn.core.models.exceptions.CustomNotFoundException;
import su.svn.core.repository.JsonRecordRepository;
import su.svn.core.services.domain.JsonRecordServiceImpl;
import su.svn.core.services.domain.RecordViewService;
import su.svn.core.services.domain.UserNameServiceImpl;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@Transactional
class ServicesIT {


    @Autowired
    private JsonRecordServiceImpl jsonRecordService;

    @Autowired
    private JsonRecordRepository jsonRecordRepository;

    @Autowired
    private RecordViewService recordViewService;

    @Autowired
    private UserNameServiceImpl userNameService;

    @Test
    @WithMockUser(username = "root")
    void JsonRecordServiceImpl_shouldSaveAndFindPlusUpdateAndFindUser() throws Exception {
        // given
        var postAt = OffsetDateTime.of(LocalDateTime.now(), ZoneOffset.UTC);
        NewJsonRecord dto = NewJsonRecord
                .builder()
                .parentId(UUID.fromString("00000000-0000-0000-0000-000000000000"))
                .title("title1")
                .postAt(postAt)
                .json(Map.of())
                .tags(Collections.singleton("tag1"))
                .build();

        // when
        ResourceJsonRecord saved = jsonRecordService.save(dto);
        jsonRecordRepository.flush();

        // then
        assertThat(saved.id()).isNotNull();

        ResourceJsonRecord found = jsonRecordService.findById(saved.id());
        assertThat(found.title()).isEqualTo("title1");
        assertThat(found.parentId()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        org.junit.jupiter.api.Assertions.assertNotNull(found.postAt());

        var refreshAt = OffsetDateTime.of(LocalDateTime.now(), ZoneOffset.UTC);

        UpdateJsonRecord upDto = UpdateJsonRecord.builder()
                .id(found.id())
                .parentId(found.parentId())
                .title("title2")
                .json(found.json())
                .refreshAt(refreshAt)
                .tags(Set.of("tag1", "tag2"))
                .build();

        ResourceJsonRecord updated = jsonRecordService.update(upDto);

        ResourceJsonRecord foundUpdated = jsonRecordService.findById(updated.id());

        assertThat(foundUpdated.title()).isEqualTo("title2");
        assertThat(foundUpdated.parentId()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        assertThat(foundUpdated.refreshAt()).isEqualTo(refreshAt);
        org.junit.jupiter.api.Assertions.assertNotNull(foundUpdated.tags());
        org.junit.jupiter.api.Assertions.assertTrue(foundUpdated.tags().contains("tag1"));
        org.junit.jupiter.api.Assertions.assertTrue(foundUpdated.tags().contains("tag2"));

        var result = recordViewService.getFilteredRecords(new ResourceRecordViewFilter(null, null, null, null, false), PageRequest.of(0, 2));
        result.get().forEach(Assertions::assertNotNull);
        jsonRecordService.disable(updated.id());

        assertThatExceptionOfType(CustomNotFoundException.class)
                .isThrownBy(() -> jsonRecordService.findById(updated.id()));
    }

    @Test
    void UserNameServiceImpl_shouldSaveAndFindUser() throws Exception {
        // given
        String userName = UUID.randomUUID().toString();
        NewUserName dto = NewUserName
                .builder()
                .userName(userName)
                .id(UUID.randomUUID())
                .build();

        // when
        UserName saved = userNameService.save(dto);

        // then
        assertThat(saved.id()).isNotNull();

        UserName found = userNameService.findByUserName(userName);
        assertThat(found.userName()).isEqualTo(userName);
    }

    @Test
    void UserNameServiceImpl_shouldThrowWhenUserNotFound() {
        assertThatThrownBy(() -> userNameService.findByUserName("unknown"))
                .isInstanceOf(Exception.class);
    }
}