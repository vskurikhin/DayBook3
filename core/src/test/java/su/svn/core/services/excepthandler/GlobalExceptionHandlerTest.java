package su.svn.core.services.excepthandler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import su.svn.core.models.dto.ErrorResponse;
import su.svn.core.models.exceptions.CustomNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private WebRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        request = new ServletWebRequest(new MockHttpServletRequest());
    }

    @Test
    void ChangeSetPersistershouldHandleNotFoundException() {
        // given
        ChangeSetPersister.NotFoundException ex =
                new ChangeSetPersister.NotFoundException();

        // when
        ErrorResponse response = handler.handleNotFoundException(ex, request);

        // then
        assertNotNull(response);
        assertEquals("Resource not found", response.getError());
        assertTrue(response.getTime() > 0);
    }

    @Test
    void shouldHandleNotFoundException() {
        // given
        var ex = new CustomNotFoundException();

        // when
        ErrorResponse response = handler.handleCustomNotFound(ex, request);

        // then
        assertNotNull(response);
        assertEquals("Resource not found", response.getError());
        assertTrue(response.getTime() > 0);
    }

    @Test
    void shouldHandleValidationException() {
        // given
        Object target = new Object();
        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(target, "object");

        bindingResult.addError(new FieldError(
                "object",
                "title",
                "must not be null"
        ));

        MethodArgumentNotValidException ex =
                new MethodArgumentNotValidException(null, bindingResult);

        // when
        ErrorResponse response = handler.handleValidationExceptions(ex, request);

        // then
        assertNotNull(response);
        assertTrue(response.getError().contains("Validation error"));
        assertTrue(response.getError().contains("title: must not be null"));
        assertTrue(response.getTime() > 0);
    }

    @Test
    void shouldHandleHttpMessageNotReadableException() {
        // given
        Exception cause = new RuntimeException("JSON parse error");

        HttpMessageNotReadableException ex =
                new HttpMessageNotReadableException("Invalid JSON", cause);

        // when
        ErrorResponse response = handler.handleHttpMessageNotReadableException(ex, request);

        // then
        assertNotNull(response);
        assertEquals(
                "Invalid input. Please check your request format or parameters.",
                response.getError()
        );
        assertTrue(response.getTime() > 0);
    }

    @Test
    void shouldHandleGlobalException() {
        // given
        Exception ex = new RuntimeException("boom");

        // when
        ErrorResponse response = handler.handleGlobalException(ex, request);

        // then
        assertNotNull(response);
        assertEquals("An unexpected error occurred", response.getError());
        assertTrue(response.getTime() > 0);
    }
}