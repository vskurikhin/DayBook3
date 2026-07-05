/*
 * This file was last modified at 2026.03.27 14:01 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * ErrorResponse.java
 * $Id$
 */
package su.svn.core.models.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

/**
 * DTO for representing error responses returned by the API.
 *
 * <p>Contains a human-readable error message and a timestamp indicating
 * when the error occurred.</p>
 *
 * <p>Used by {@link su.svn.core.services.excepthandler.GlobalExceptionHandler}
 * to standardize error responses across the application.</p>
 */
@AllArgsConstructor
@Builder
@FieldDefaults(level = PRIVATE)
@Getter
@NoArgsConstructor
@Setter
public class ErrorResponse {
    private String error;
    private long time;
}