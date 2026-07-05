package su.svn.api.services.domain;

import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import su.svn.api.domain.entities.PostRecord;
import su.svn.api.models.dto.NewJsonRecord;
import su.svn.api.models.dto.ResourceJsonRecord;
import su.svn.api.models.dto.UpdateJsonRecord;
import su.svn.api.repository.JsonRecordRepository;
import su.svn.lib.RecordType;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JsonRecordDataServiceTest {

    @InjectMocks
    JsonRecordDataService jsonRecordDataService;

    @Mock
    JsonRecordRepository jsonRecordRepository;

    @Mock
    JsonRecordSyncTrigger trigger;

    UUID id;
    PostRecord postRecord;
    ResourceJsonRecord resourceJsonRecord;

    @BeforeEach
    void setUp() {

        id = UUID.randomUUID();

        postRecord = PostRecord.builder()
                .id(id)
                .parentId(UUID.randomUUID())
                .type(RecordType.Json)
                .userName("root")
                .title("title")
                .json(Map.of("key", "value"))
                .enabled(true)
                .visible(true)
                .flags(1)
                .postAt(OffsetDateTime.now(ZoneOffset.UTC))
                .refreshAt(OffsetDateTime.now(ZoneOffset.UTC))
                .lastChangedTime(LocalDateTime.now())
                .build();

        resourceJsonRecord = ResourceJsonRecord.builder()
                .id(id)
                .title("title")
                .json(Map.of("key", "value"))
                .build();
    }

    @Test
    void shouldDeleteRecord() {

        when(jsonRecordRepository.delete(id))
                .thenReturn(Uni.createFrom().voidItem());

        Void result = jsonRecordDataService.delete(id)
                .await().indefinitely();

        assertThat(result).isNull();

        verify(jsonRecordRepository).delete(id);
    }

    @Test
    void shouldPostRecord() {
        var id = UUID.randomUUID();
        var dateTime = OffsetDateTime.now();

        NewJsonRecord request = NewJsonRecord.builder()
                .parentId(id)
                .title("title")
                .json(Map.of("key", "value"))
                .postAt(dateTime)
                .build();
        var response = ResourceJsonRecord.builder()
                .id(id)
                .parentId(id)
                .title("title")
                .json(Map.of("key", "value"))
                .postAt(dateTime)
                .build();

        when(jsonRecordRepository.post(request))
                .thenReturn(Uni.createFrom().item(response));

        ResourceJsonRecord result = jsonRecordDataService.post(request)
                .await().indefinitely();

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(id);
        assertThat(result.title()).isEqualTo("title");

        verify(jsonRecordRepository).post(request);
        verify(trigger).accept(response);
    }

    @Test
    void shouldUpdateRecord() {
        var id = UUID.randomUUID();
        var dateTime = OffsetDateTime.now();

        UpdateJsonRecord request = UpdateJsonRecord.builder()
                .id(id)
                .parentId(id)
                .title("updated")
                .json(Map.of("key", "value"))
                .refreshAt(dateTime)
                .build();
        var response = ResourceJsonRecord.builder()
                .id(id)
                .parentId(id)
                .title("updated")
                .json(Map.of("key", "value"))
                .postAt(dateTime)
                .build();

        when(jsonRecordRepository.put(request))
                .thenReturn(Uni.createFrom().item(response));

        ResourceJsonRecord result = jsonRecordDataService.put(request)
                .await().indefinitely();

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(id);

        verify(jsonRecordRepository).put(request);
        verify(trigger).accept(response);
    }

    @Test
    void shouldCreateNewJsonRecordBuilder() {

        OffsetDateTime postAt = OffsetDateTime.now();

        NewJsonRecord dto = NewJsonRecord.builder()
                .parentId(UUID.randomUUID())
                .title("title")
                .json(Map.of("k", "v"))
                .postAt(postAt)
                .visible(true)
                .flags(10)
                .build();

        assertThat(dto.title()).isEqualTo("title");
        assertThat(dto.postAt()).isEqualTo(postAt);
        assertThat(dto.flags()).isEqualTo(10);
        assertThat(dto.visible()).isTrue();
    }

    @Test
    void shouldCreateUpdateJsonRecordBuilder() {

        UUID id = UUID.randomUUID();
        OffsetDateTime refreshAt = OffsetDateTime.now();

        UpdateJsonRecord dto = UpdateJsonRecord.builder()
                .id(id)
                .parentId(UUID.randomUUID())
                .title("updated")
                .refreshAt(refreshAt)
                .visible(true)
                .flags(99)
                .build();

        assertThat(dto.id()).isEqualTo(id);
        assertThat(dto.title()).isEqualTo("updated");
        assertThat(dto.refreshAt()).isEqualTo(refreshAt);
        assertThat(dto.flags()).isEqualTo(99);
    }

    @Test
    void shouldBuildPostRecord() {

        UUID id = UUID.randomUUID();

        PostRecord record = PostRecord.builder()
                .id(id)
                .parentId(UUID.randomUUID())
                .type(RecordType.Json)
                .userName("root")
                .title("title")
                .enabled(true)
                .flags(5)
                .json(Map.of("a", "b"))
                .build();

        assertThat(record.id()).isEqualTo(id);
        assertThat(record.userName()).isEqualTo("root");
        assertThat(record.type()).isEqualTo(RecordType.Json);
        assertThat(record.flags()).isEqualTo(5);
        assertThat(record.json()).containsEntry("a", "b");
    }
}