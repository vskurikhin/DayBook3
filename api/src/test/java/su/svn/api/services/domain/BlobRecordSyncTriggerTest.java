package su.svn.api.services.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import su.svn.api.models.dto.ResourceBlobRecord;
import su.svn.api.services.schedulers.RecordSchedulerService;

import java.util.UUID;

class BlobRecordSyncTriggerTest {

    private RecordSchedulerService schedulerService;
    private BlobRecordSyncTrigger trigger;

    @BeforeEach
    void setUp() {
        schedulerService = Mockito.mock(RecordSchedulerService.class);

        trigger = new BlobRecordSyncTrigger();
        trigger.schedulerService = schedulerService;
    }

    @Test
    void shouldTriggerSchedulerForResource() {
        ResourceBlobRecord resource = Mockito.mock(ResourceBlobRecord.class);

        trigger.accept(resource);

        Mockito.verify(schedulerService)
                .fire(true);
    }

    @Test
    void shouldTriggerSchedulerForId() {
        UUID id = UUID.randomUUID();

        trigger.accept(id);

        Mockito.verify(schedulerService)
                .fire(true);
    }
}