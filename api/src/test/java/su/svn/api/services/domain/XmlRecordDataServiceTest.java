package su.svn.api.services.domain;

import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import su.svn.api.models.dto.NewXmlRecord;
import su.svn.api.models.dto.ResourceXmlRecord;
import su.svn.api.models.dto.UpdateXmlRecord;
import su.svn.api.repository.XmlRecordRepository;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class XmlRecordDataServiceTest {

    @InjectMocks
    XmlRecordDataService service;

    @Mock
    XmlRecordRepository repository;

    @Mock
    XmlRecordSyncTrigger trigger;

    @Test
    void shouldDeleteRecord() {
        var id = UUID.randomUUID();

        when(repository.delete(id))
                .thenReturn(Uni.createFrom().voidItem());

        service.delete(id)
                .await()
                .indefinitely();

        verify(repository).delete(id);
        verify(trigger).accept(id);
    }

    @Test
    void shouldCreateRecord() {
        var request = NewXmlRecord.builder()
                .parentId(UUID.randomUUID())
                .title("xml")
                .xml("<root/>")
                .postAt(OffsetDateTime.now())
                .build();

        var response = ResourceXmlRecord.builder()
                .id(UUID.randomUUID())
                .xml("<root/>")
                .build();

        when(repository.post(request))
                .thenReturn(Uni.createFrom().item(response));

        var result = service.post(request)
                .await()
                .indefinitely();

        assertThat(result).isEqualTo(response);

        verify(repository).post(request);
        verify(trigger).accept(response);
    }

    @Test
    void shouldUpdateRecord() {
        var request = UpdateXmlRecord.builder()
                .id(UUID.randomUUID())
                .xml("<updated/>")
                .refreshAt(OffsetDateTime.now())
                .build();

        var response = ResourceXmlRecord.builder()
                .id(request.id())
                .xml("<updated/>")
                .build();

        when(repository.put(request))
                .thenReturn(Uni.createFrom().item(response));

        var result = service.put(request)
                .await()
                .indefinitely();

        assertThat(result).isEqualTo(response);

        verify(repository).put(request);
        verify(trigger).accept(response);
    }
}