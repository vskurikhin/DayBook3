package su.svn.api.resources.handlers;

import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    GlobalExceptionHandler handler;

    @Mock
    Logger log;

    @Test
    void shouldHandleExceptionWithoutDebug() {
        var exception = new RuntimeException("boom");

        when(log.isDebugEnabled()).thenReturn(false);

        Response response = handler.toResponse(exception);

        assertThat(response.getStatus()).isEqualTo(500);
        assertThat(response.getEntity())
                .isEqualTo("HTTP 500 Oops, something went wrong");

        verify(log).errorf(
                eq("Unhandled exception: %s message: %s %s"),
                eq(RuntimeException.class.getName()),
                eq("boom"),
                eq("")
        );
    }

    @Test
    void shouldHandleExceptionWithDebug() {
        var exception = new RuntimeException("boom");

        when(log.isDebugEnabled()).thenReturn(true);

        Response response = handler.toResponse(exception);

        assertThat(response.getStatus()).isEqualTo(500);
        assertThat(response.getEntity())
                .isEqualTo("HTTP 500 Oops, something went wrong");

        verify(log).errorf(
                eq("Unhandled exception: %s message: %s %s"),
                eq(RuntimeException.class.getName()),
                eq("boom"),
                contains("RuntimeException")
        );
    }
}