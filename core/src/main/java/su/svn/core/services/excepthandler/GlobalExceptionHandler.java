/*
 * This file was last modified at 2026.05.08 14:03 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * GlobalExceptionHandler.java
 * $Id$
 */

package su.svn.core.services.excepthandler;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import su.svn.core.models.dto.ErrorResponse;
import su.svn.core.models.exceptions.CustomNotFoundException;

import java.io.IOException;
import java.util.stream.Collectors;
/**
 * Global exception handler for REST controllers.
 *
 * <p>This class intercepts exceptions thrown by controllers and converts them
 * into standardized {@link su.svn.core.models.dto.ErrorResponse} objects.</p>
 *
 * <p>Handles common application exceptions such as:</p>
 * <ul>
 *     <li>Entity not found</li>
 *     <li>Validation errors</li>
 *     <li>Malformed JSON / request body</li>
 *     <li>Unexpected server errors</li>
 * </ul>
 *
 * <p>Annotated with {@code @RestControllerAdvice} to apply globally.</p>
 *
 * @author Victor N. Skurikhin
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ChangeSetPersister.NotFoundException.class)
    public ErrorResponse handleNotFoundException(ChangeSetPersister.NotFoundException e, WebRequest request) {
        log.warn("Entity not found, request details: {}", request, e);
        return new ErrorResponse("Resource not found", System.currentTimeMillis());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleValidationExceptions(MethodArgumentNotValidException e, WebRequest request) {
        String validationErrors = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining(", "));
        log.warn("Validation failed: Errors: {}, Request details: {}", validationErrors, request);
        log.debug("Stacktrace:", e);
        return new ErrorResponse("Validation error: " + validationErrors, System.currentTimeMillis());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ErrorResponse handleHttpMessageNotReadableException(HttpMessageNotReadableException e, WebRequest request) {
        e.getMostSpecificCause();
        String detailedError = e.getMostSpecificCause().getMessage();

        log.error("Failed to read HTTP message. Detailed error: {}, Request details: {}, Message: {}", detailedError, request, e.getMessage());

        String userFriendlyMessage = "Invalid input. Please check your request format or parameters.";
        return new ErrorResponse(userFriendlyMessage, System.currentTimeMillis());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(CustomNotFoundException.class)
    public ErrorResponse handleCustomNotFound(CustomNotFoundException e, WebRequest request) {
        log.warn("Resource not found, request details: {}", request, e);
        return new ErrorResponse("Resource not found", System.currentTimeMillis());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResponse handleGlobalException(Exception e, WebRequest request) {
        log.error("Unexpected error: {} {}", e.getMessage(), request, e);
        return new ErrorResponse("An unexpected error occurred", System.currentTimeMillis());
    }

    @ExceptionHandler(ClientAbortException.class)
    public void handleClientAbort(ClientAbortException ex) {
        log.debug("Client disconnected: {}", ex.getMessage());
    }

    @ExceptionHandler(IOException.class)
    public void handleIOException(IOException ex) {
        if (ex.getMessage() != null && ex.getMessage().contains("Broken pipe")) {

            log.debug("Broken pipe: client disconnected");
            return;
        }
        log.error("IO error", ex);
    }
}
