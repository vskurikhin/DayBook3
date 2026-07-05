package su.svn.api.services.schedulers;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import su.svn.api.domain.entities.PostRecord;
import su.svn.api.profile.NoContainersProfile;
import su.svn.api.services.domain.PostRecordDataSyncService;

import java.util.List;

import static org.mockito.Mockito.*;

@QuarkusTest
@TestProfile(NoContainersProfile.class)
class RecordSchedulerServiceTest {

    @InjectMock
    PostRecordDataSyncService mockPostRecordDataSyncService;

    @Inject
    RecordSchedulerService recordSchedulerService;

    @Test
    void shouldNotStartWhenFireIsFalse() {
        recordSchedulerService.job().await().indefinitely();

        verifyNoInteractions(mockPostRecordDataSyncService);
    }

    @Test
    void shouldSyncSinglePage() {

        when(mockPostRecordDataSyncService.sync(0, RecordSchedulerService.SYNC_SIZE))
                .thenReturn(Uni.createFrom().item(List.of()));

        recordSchedulerService.fire(true);

        recordSchedulerService.job().await().indefinitely();

        verify(mockPostRecordDataSyncService)
                .sync(0, RecordSchedulerService.SYNC_SIZE);

        verifyNoMoreInteractions(mockPostRecordDataSyncService);
    }

    @Test
    void shouldSyncAllPages() {

        when(mockPostRecordDataSyncService.sync(0, RecordSchedulerService.SYNC_SIZE))
                .thenReturn(Uni.createFrom().item(List.of(
                        PostRecord.builder().build(),
                        PostRecord.builder().build()
                )));

        when(mockPostRecordDataSyncService.sync(1, RecordSchedulerService.SYNC_SIZE))
                .thenReturn(Uni.createFrom().item(List.of(
                        PostRecord.builder().build()
                )));

        when(mockPostRecordDataSyncService.sync(2, RecordSchedulerService.SYNC_SIZE))
                .thenReturn(Uni.createFrom().item(List.of()));

        recordSchedulerService.fire(true);

        recordSchedulerService.job().await().indefinitely();

        verify(mockPostRecordDataSyncService).sync(0, RecordSchedulerService.SYNC_SIZE);
        verify(mockPostRecordDataSyncService).sync(1, RecordSchedulerService.SYNC_SIZE);
        verify(mockPostRecordDataSyncService).sync(2, RecordSchedulerService.SYNC_SIZE);

        verifyNoMoreInteractions(mockPostRecordDataSyncService);
    }

    @Test
    void shouldRecoverAfterFailure() {

        reset(mockPostRecordDataSyncService);

        when(mockPostRecordDataSyncService.sync(0, RecordSchedulerService.SYNC_SIZE))
                .thenReturn(Uni.createFrom().item(List.of()));

        recordSchedulerService.fire(true);

        recordSchedulerService.job().await().indefinitely();

        verify(mockPostRecordDataSyncService, times(1))
                .sync(0, RecordSchedulerService.SYNC_SIZE);

        verify(mockPostRecordDataSyncService)
                .sync(0, RecordSchedulerService.SYNC_SIZE);

        reset(mockPostRecordDataSyncService);

        when(mockPostRecordDataSyncService.sync(0, RecordSchedulerService.SYNC_SIZE))
                .thenReturn(Uni.createFrom().item(List.of()));

        recordSchedulerService.fire(true);

        recordSchedulerService.job().await().indefinitely();

        verify(mockPostRecordDataSyncService)
                .sync(0, RecordSchedulerService.SYNC_SIZE);
    }
//
//    @InjectMock
//    PostRecordDataSyncService mockPostRecordDataSyncService;
//
//    @Inject
//    RecordSchedulerService recordSchedulerService;
//
//    @BeforeEach
//    void beforeEach(TestInfo testInfo) {
//        when(mockPostRecordDataSyncService.sync(anyInt(), anyInt())).thenReturn(Uni.createFrom().item(List.of(PostRecord.builder().build())));
//        System.err.println("Running: " + testInfo.getDisplayName());
//    }
//
//    @Test
//    void tests(UniAsserter ignored) throws InterruptedException {
//        recordSchedulerService.fire(true);
//        recordSchedulerService.job();
//    }
}