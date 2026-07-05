package su.svn.api.resources.mappers;

import io.smallrye.jwt.auth.principal.ParseException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import su.svn.api.models.exceptions.CustomParseException;

import static org.assertj.core.api.Assertions.assertThat;

class CustomParseExceptionMapperTest {

    private final CustomParseExceptionMapper mapper =
            new CustomParseExceptionMapper();

    @Test
    void shouldMapException() {
        var exception =
                new CustomParseException(new ParseException("Error SRABC12345 invalid token"));

        Response response = mapper.toResponse(exception);

        assertThat(response.getStatus())
                .isEqualTo(Response.Status.UNAUTHORIZED.getStatusCode());

        assertThat(response.getEntity())
                .isEqualTo("HTTP 401 SRABC12345 invalid token");
    }

    @Test
    void shouldConvertMessage() {
        String result = CustomParseExceptionMapper.toMessage(
                "prefix text SRXYZ99999 access denied"
        );

        assertThat(result)
                .isEqualTo("HTTP 401 SRXYZ99999 access denied");
    }

    @Test
    void shouldReturnOriginalMessageWhenPatternNotFound() {
        String result = CustomParseExceptionMapper.toMessage(
                "plain message"
        );

        assertThat(result)
                .isEqualTo("plain message");
    }
}