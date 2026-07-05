package su.svn.api.resources.mappers;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NotFoundExceptionMapperTest {

    private final NotFoundExceptionMapper mapper =
            new NotFoundExceptionMapper();

    @Test
    void shouldMapException() {
        var exception = new NotFoundException("resource not found");

        Response response = mapper.toResponse(exception);

        assertThat(response.getStatus())
                .isEqualTo(Response.Status.NOT_FOUND.getStatusCode());

        assertThat(response.getEntity())
                .isEqualTo("resource not found");
    }
}