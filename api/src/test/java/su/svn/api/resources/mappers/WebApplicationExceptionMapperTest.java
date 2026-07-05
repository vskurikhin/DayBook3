package su.svn.api.resources.mappers;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WebApplicationExceptionMapperTest {

    private final WebApplicationExceptionMapper mapper =
            new WebApplicationExceptionMapper();

    @Test
    void shouldMapException() {
        var exception = new WebApplicationException("bad request");

        Response response = mapper.toResponse(exception);

        assertThat(response.getStatus())
                .isEqualTo(Response.Status.BAD_REQUEST.getStatusCode());

        assertThat(response.getEntity())
                .isEqualTo("bad request");
    }
}