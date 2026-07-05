package su.svn.api.services.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import su.svn.api.models.dto.ResourceValueRecord;
import su.svn.api.services.schedulers.RecordSchedulerService;

import java.util.UUID;

class ValueRecordSyncTriggerTest {

    private RecordSchedulerService schedulerService;
    private ValueRecordSyncTrigger trigger;

    @BeforeEach
    void setUp() {
        schedulerService = Mockito.mock(RecordSchedulerService.class);

        trigger = new ValueRecordSyncTrigger();
        trigger.schedulerService = schedulerService;
    }

    @Test
    void shouldTriggerSchedulerForResource() {
        ResourceValueRecord resource = Mockito.mock(ResourceValueRecord.class);

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