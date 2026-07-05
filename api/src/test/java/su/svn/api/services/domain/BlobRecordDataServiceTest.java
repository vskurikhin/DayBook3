package su.svn.api.services.domain;

import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import su.svn.api.models.dto.NewBlobRecord;
import su.svn.api.models.dto.ResourceBlobRecord;
import su.svn.api.models.dto.UpdateBlobRecord;
import su.svn.api.repository.BlobRecordRepository;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class BlobRecordDataServiceTest {

    private BlobRecordRepository repository;
    private BlobRecordSyncTrigger trigger;
    private BlobRecordDataService service;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(BlobRecordRepository.class);
        trigger = Mockito.mock(BlobRecordSyncTrigger.class);

        service = new BlobRecordDataService();
        service.repository = repository;
        service.trigger = trigger;
    }

    @Test
    void shouldPostAndTriggerSync() {
        NewBlobRecord request = Mockito.mock(NewBlobRecord.class);
        ResourceBlobRecord response = Mockito.mock(ResourceBlobRecord.class);

        Mockito.when(repository.post(request))
                .thenReturn(Uni.createFrom().item(response));

        ResourceBlobRecord result = service.post(request)
                .await()
                .indefinitely();

        assertNotNull(result);

        Mockito.verify(trigger)
                .accept(response);
    }

    @Test
    void shouldPutAndTriggerSync() {
        UpdateBlobRecord request = Mockito.mock(UpdateBlobRecord.class);
        ResourceBlobRecord response = Mockito.mock(ResourceBlobRecord.class);

        Mockito.when(repository.put(request))
                .thenReturn(Uni.createFrom().item(response));

        ResourceBlobRecord result = service.put(request)
                .await()
                .indefinitely();

        assertNotNull(result);

        Mockito.verify(trigger)
                .accept(response);
    }

    @Test
    void shouldDeleteAndTriggerSync() {
        UUID id = UUID.randomUUID();

        Mockito.when(repository.delete(id))
                .thenReturn(Uni.createFrom().voidItem());

        service.delete(id)
                .await()
                .indefinitely();

        Mockito.verify(trigger)
                .accept(id);
    }
}