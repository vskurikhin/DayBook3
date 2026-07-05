package su.svn.api.services.schedulers;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import su.svn.api.profile.NoContainersProfile;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@QuarkusTest
@TestProfile(NoContainersProfile.class)
class FireSchedulerServiceTest {

    @Inject
    FireSchedulerService fireSchedulerService;

    @InjectMock
    RecordSchedulerService recordSchedulerService;

    @Test
    void shouldTriggerRecordScheduler() {
        fireSchedulerService.job();

        verify(recordSchedulerService, times(1)).fire(true);
        verifyNoMoreInteractions(recordSchedulerService);
    }
}