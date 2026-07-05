package su.svn.api.resources;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.security.TestSecurity;
import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.Test;
import su.svn.api.domain.entities.PostRecord;
import su.svn.api.domain.enums.ResourcePath;
import su.svn.api.models.dto.Page;
import su.svn.api.profile.NoContainersProfile;
import su.svn.api.services.domain.PostRecordDataSyncService;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyByte;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@QuarkusTest
@TestProfile(NoContainersProfile.class)
class PostRecordDataSyncServiceReadPageTest {

    @InjectMock
    PostRecordDataSyncService mockPostRecordDataSyncService;

    @TestSecurity(user = "john", roles = {"USER"})
    @Test
    void testHelloEndpoint() {
        var p1 = PostRecord.builder()
                .id(new UUID(0, 1))
                .parentId(new UUID(0, 2))
                .postAt(OffsetDateTime.MIN.withMinute(0))
                .flags(0)
                .build();
        var p2 = Page.<PostRecord>builder()
                .list(List.of(p1))
                .build();
        when(mockPostRecordDataSyncService.readPage(anyInt(), anyByte())).thenReturn(Uni.createFrom().item(p2));

        @SuppressWarnings("unchecked") Page<LinkedHashMap<String, Object>> resp = given()
                .when().get(ResourcePath.RECORDS)
                .then()
                .statusCode(200)
                .extract()
                .as(Page.class);
        assertAll(
                () -> assertNotNull(resp),
                () -> assertEquals("00000000-0000-0000-0000-000000000001", resp.list().getFirst().get("id")),
                () -> assertEquals("00000000-0000-0000-0000-000000000002", resp.list().getFirst().get("parentId")),
                () -> assertEquals("Base", resp.list().getFirst().get("type")),
                () -> assertEquals("-999999999-01-01T00:00:00+18:00", resp.list().getFirst().get("postAt"))
        );
    }
}