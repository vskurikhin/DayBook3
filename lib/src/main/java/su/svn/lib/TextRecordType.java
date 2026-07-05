/*
 * This file was last modified at 2026.05.22 18:49 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * TextRecordType.java
 * $Id$
 */

package su.svn.lib;

/**
 * Enumeration of supported text content formats.
 * <p>
 * Defines the semantic type of textual content
 * stored inside {@code TextRecord}.
 * </p>
 */
public enum TextRecordType {

    /**
     * Plain text value.
     */
    Value,

    /**
     * File name reference.
     */
    FileName,

    /**
     * HTML formatted content.
     */
    Html,

    /**
     * Hyperlink value.
     */
    Link,

    /**
     * Markdown formatted content.
     */
    Markdown
}
