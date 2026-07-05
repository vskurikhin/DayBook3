package su.svn;

import io.quarkus.test.InjectMock;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.security.TestSecurity;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import su.svn.api.models.dto.NewJsonRecord;
import su.svn.api.models.dto.ResourceJsonRecord;
import su.svn.api.models.dto.UpdateJsonRecord;
import su.svn.api.profile.ContainersProfile;
import su.svn.api.resources.JsonRecordResource;
import su.svn.api.services.domain.JsonRecordDataService;
import su.svn.api.services.schedulers.RecordSchedulerService;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@QuarkusTest
@QuarkusTestResource(value = PostgreSQLTestResource.class)
@TestProfile(ContainersProfile.class)
public class ApiIT {

    @Inject
    JsonRecordResource resource;

    @InjectMock
    JsonRecordDataService jsonRecordDataService;

    @InjectMock
    RecordSchedulerService recordSchedulerService;

    @BeforeEach
    void beforeEach(TestInfo testInfo) {
        System.err.println("Running: " + testInfo.getDisplayName());
    }

    @TestSecurity(user = "john", roles = {"USER"})
    @Test
    @DisplayName("JsonRecordResource create")
    void jsonRecordResourceTest_shouldCreateRecord() {
        // given
        NewJsonRecord request = NewJsonRecord.builder()
                .json(Collections.emptyMap())
                .postAt(OffsetDateTime.now())
                .build();
        ResourceJsonRecord responseDto = ResourceJsonRecord.builder().build();

        when(jsonRecordDataService.post(request))
                .thenReturn(Uni.createFrom().item(responseDto));

        // when
        try (var response = resource.create(request).await().indefinitely()) {
            // then
            assertThat(response.getStatus()).isEqualTo(Response.Status.CREATED.getStatusCode());
            assertThat(response.getEntity()).isEqualTo(responseDto);
        }

        verify(recordSchedulerService, atLeastOnce()).fire(true);
    }

    @TestSecurity(user = "john", roles = {"USER"})
    @Test
    @DisplayName("JsonRecordResource delete")
    void jsonRecordResourceTest_shouldDeleteRecord() {
        // given
        UUID id = UUID.randomUUID();

        when(jsonRecordDataService.delete(id))
                .thenReturn(Uni.createFrom().voidItem());

        // when
        try (Response response = resource.delete(id)
                .await().indefinitely()) {

            // then
            assertThat(response.getStatus()).isEqualTo(Response.Status.NO_CONTENT.getStatusCode());
        }

        verify(recordSchedulerService).fire(true);
    }

    @TestSecurity(user = "john", roles = {"USER"})
    @Test
    @DisplayName("JsonRecordResource update")
    void jsonRecordResourceTest_shouldUpdateRecord() {
        // given
        UpdateJsonRecord request = UpdateJsonRecord.builder()
                .id(UUID.randomUUID())
                .parentId(UUID.randomUUID())
                .json(Collections.emptyMap())
                .refreshAt(OffsetDateTime.now())
                .build();
        ResourceJsonRecord responseDto = ResourceJsonRecord.builder().build();

        when(jsonRecordDataService.put(request))
                .thenReturn(Uni.createFrom().item(responseDto));

        // when
        try (var response = resource.update(request).await().indefinitely()) {
            // then
            assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
            assertThat(response.getEntity()).isEqualTo(responseDto);
        }

        verify(recordSchedulerService).fire(true);
    }

    @TestSecurity(user = "john", roles = {"USER"})
    @Test
    @DisplayName("JsonRecordResource should trigger scheduler only after success create")
    void jsonRecordResourceTest_shouldTriggerSchedulerOnlyAfterSuccess_create() {
        // given
        NewJsonRecord request = NewJsonRecord.builder()
                .json(Collections.emptyMap())
                .postAt(OffsetDateTime.now())
                .build();

        when(jsonRecordDataService.post(request))
                .thenReturn(Uni.createFrom().item(ResourceJsonRecord.builder().build()));

        // when
        //noinspection resource
        resource.create(request).await().indefinitely();

        // then
        verify(recordSchedulerService, times(1)).fire(true);
    }

    @TestSecurity(user = "john", roles = {"USER"})
    @Test
    @DisplayName("JsonRecordResource should not trigger scheduler on failure")
    void jsonRecordResourceTest_shouldNotTriggerSchedulerOnFailure() {
        // given
        NewJsonRecord request = NewJsonRecord.builder()
                .json(Collections.emptyMap())
                .postAt(OffsetDateTime.now())
                .build();

        when(jsonRecordDataService.post(request))
                .thenReturn(Uni.createFrom().failure(new RuntimeException("boom")));

        // when / then
        //noinspection EmptyTryBlock
        try (var ignored = resource.create(request).await().indefinitely()) {
        } catch (RuntimeException ignored) {
        }

        verify(recordSchedulerService, never()).fire(true);
    }
}
