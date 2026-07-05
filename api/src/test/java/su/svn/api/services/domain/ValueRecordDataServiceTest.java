package su.svn.api.services.domain;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import su.svn.api.models.dto.NewValueRecord;
import su.svn.api.models.dto.ResourceValueRecord;
import su.svn.api.models.dto.UpdateValueRecord;
import su.svn.api.repository.ValueRecordRepository;

import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ValueRecordDataServiceTest {

    @InjectMocks
    ValueRecordDataService service;

    @Mock
    ValueRecordRepository recordRepository;

    @Mock
    ValueRecordSyncTrigger trigger;

    @Test
    void shouldDeleteRecord() {
        var id = UUID.randomUUID();

        when(recordRepository.delete(id))
                .thenReturn(Uni.createFrom().voidItem());

        service.delete(id)
                .subscribe()
                .withSubscriber(UniAssertSubscriber.create())
                .assertCompleted();

        verify(recordRepository).delete(id);
    }

    @Test
    void shouldPostRecord() {
        var dto = mock(NewValueRecord.class);
        var response = mock(ResourceValueRecord.class);

        when(recordRepository.post(dto))
                .thenReturn(Uni.createFrom().item(response));

        service.post(dto)
                .subscribe()
                .withSubscriber(UniAssertSubscriber.create())
                .assertCompleted();

        verify(recordRepository).post(dto);
        verify(trigger).accept(response);
    }

    @Test
    void shouldPutRecord() {
        var update = mock(UpdateValueRecord.class);
        var resource = mock(ResourceValueRecord.class);

        when(recordRepository.put(update))
                .thenReturn(Uni.createFrom().item(resource));

        service.put(update)
                .subscribe()
                .withSubscriber(UniAssertSubscriber.create())
                .assertCompleted();

        verify(recordRepository).put(update);
        verify(trigger).accept(resource);
    }
}