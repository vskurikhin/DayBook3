package su.svn.api.resources.mappers;

import jakarta.ws.rs.NotSupportedException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NotSupportedExceptionMapperTest {

    private final NotSupportedExceptionMapper mapper =
            new NotSupportedExceptionMapper();

    @Test
    void shouldMapException() {
        var exception = new NotSupportedException("method forbidden");

        Response response = mapper.toResponse(exception);

        assertThat(response.getStatus())
                .isEqualTo(Response.Status.FORBIDDEN.getStatusCode());

        assertThat(response.getEntity())
                .isEqualTo("HTTP 403 method forbidden");
    }
}