/*
 * This file was last modified at 2026.05.21 16:48 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * CustomParseException.java
 * $Id$
 */

package su.svn.api.models.exceptions;

import io.smallrye.jwt.auth.principal.ParseException;

public class CustomParseException extends RuntimeException {
    public CustomParseException(ParseException e) {
        super(e);
    }
}
