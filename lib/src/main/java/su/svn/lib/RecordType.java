/*
 * This file was last modified at 2026.05.22 18:49 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * RecordType.java
 * $Id$
 */

package su.svn.lib;

/**
 * Enumeration of supported record types in the system.
 * <p>
 * Each value represents a specific content model
 * stored and processed by the application.
 * </p>
 * <p>Defines the type of content stored in {@link su.svn.core.domain.entities.BaseRecord}.</p>
 */
public enum RecordType {

    /**
     * Generic base record type.
     */
    Base,

    /**
     * Binary large object record.
     */
    Blob,

    /**
     * JSON structured record.
     */
    Json,

    /**
     * Set-based record.
     */
    Set,

    /**
     * Plain text record.
     */
    Text,

    /**
     * Vector-based record.
     */
    Vector,

    /**
     * XML structured record.
     */
    Xml
}
