package su.svn;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusIntegrationTest
class RecordResourceIT {

    @Test
    @DisplayName("GET /api/v2/records returns page")
    void testPage() {

        given()
                .queryParam("page", 0)
                .queryParam("size", 10)
                .accept(ContentType.JSON)

                .when()
                .get("/api/v2/records")

                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)

                // проверка структуры page
                .body("page", Matchers.notNullValue())
                .body("size", Matchers.notNullValue())

                // проверка списка records
                .body("records", Matchers.notNullValue());
    }
}