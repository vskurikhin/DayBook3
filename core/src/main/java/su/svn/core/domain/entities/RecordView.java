/*
 * This file was last modified at 2026.05.24 13:27 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * RecordView.java
 * $Id$
 */

package su.svn.core.domain.entities;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.*;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

/**
 * JPA entity representing a database view of records with extended attributes.
 *
 * <p>This entity maps to the database view <b>core.records_view</b> and is used
 * for read operations that combine data from multiple underlying tables
 * (e.g., {@link BaseRecord} and related JSON data).</p>
 *
 * <p>Unlike standard entities, this class is typically backed by a database view
 * and is intended for querying and projection purposes rather than direct
 * modification.</p>
 *
 * <h2>Key Features</h2>
 * <ul>
 *     <li>Represents a unified view of record data</li>
 *     <li>Includes hierarchical relationship via {@link #parent}</li>
 *     <li>Supports JSON storage via {@link #json}</li>
 *     <li>Contains audit fields such as creation and update timestamps</li>
 *     <li>Supports soft-delete and visibility flags</li>
 * </ul>
 *
 * <h2>Relationships</h2>
 * <ul>
 *     <li>{@link #parent} — many-to-one relationship to {@link BaseRecord}</li>
 * </ul>
 *
 * <h2>Audit Fields</h2>
 * <ul>
 *     <li>{@link #createTime} — timestamp when the record was created</li>
 *     <li>{@link #updateTime} — timestamp when the record was last updated</li>
 *     <li>{@link #lastChangedTime} — timestamp of the last modification</li>
 * </ul>
 *
 * <h2>Flags</h2>
 * <ul>
 *     <li>{@link #enabled} — indicates whether the record is active</li>
 *     <li>{@link #localChange} — indicates whether the record was modified locally</li>
 *     <li>{@link #visible} — controls visibility of the record</li>
 *     <li>{@link #flags} — custom integer flags for additional metadata</li>
 * </ul>
 *
 * <h2>JSON Support</h2>
 * <p>The {@link #json} field stores structured data in JSON format and is
 * mapped using Hibernate's {@link org.hibernate.annotations.JdbcTypeCode}.</p>
 *
 * <p><b>Note:</b> The {@link #id} field is generated and not insertable/updatable,
 * as it is managed by the database view.</p>
 *
 * @see BaseRecord
 * @see su.svn.lib.RecordType
 */
@Accessors(fluent = true)
@AllArgsConstructor
@Builder
@Entity
@EqualsAndHashCode(callSuper = false, exclude = {"id"})
@FieldDefaults(level = PRIVATE)
@Getter
@NoArgsConstructor
@Setter
@Table(name = "records_view", schema = "core")
@ToString
public class RecordView {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id", updatable = false, insertable = false)
    UUID id;

    @Column(name = "parent_id", nullable = false)
    UUID parentId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "parent_id",
            referencedColumnName = "id",
            insertable = false,
            nullable = false,
            updatable = false)
    @Fetch(FetchMode.JOIN)
    BaseRecord parent;

    @Builder.Default
    @Column(name = "type")
    su.svn.lib.RecordType type = su.svn.lib.RecordType.Base;

    @Column(name = "user_name", nullable = false)
    String userName;

    @Column(name = "post_at", nullable = false)
    OffsetDateTime postAt;

    @Column(name = "refresh_at")
    OffsetDateTime refreshAt;

    @CreationTimestamp
    @Column(name = "create_time", updatable = false, nullable = false)
    LocalDateTime createTime;

    @UpdateTimestamp
    @Column(name = "update_time", updatable = false)
    LocalDateTime updateTime;

    @UpdateTimestamp
    @Column(name = "last_changed_time", updatable = false, nullable = false)
    LocalDateTime lastChangedTime;

    @Builder.Default
    @Column(name = "enabled")
    boolean enabled = true;

    @Builder.Default
    @Column(name = "local_change")
    boolean localChange = true;

    @Column(name = "visible")
    boolean visible;

    @Column(name = "flags")
    int flags;

    @Column(name = "title")
    String title;

    @Column(name = "ahref")
    String aHref;

    @Column(name = "blob")
    byte[] blob;

    @Column(name = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    Map<String, String> json;

    @Column(name = "texts")
    @JdbcTypeCode(SqlTypes.ARRAY)
    Set<String> texts;

    @Column(name = "file_name")
    String fileName;

    @Column(name = "html")
    String html;

    @Column(name = "link")
    String link;

    @Column(name = "markdown")
    String markdown;

    @Column(name = "value")
    String value;

    @Column(name = "vector", nullable = false)
    @JdbcTypeCode(SqlTypes.VECTOR)
    @Array(length = 1024)
    float[] vector;

    @Column(name = "xml", nullable = false)
    @JdbcTypeCode(SqlTypes.SQLXML)
    String xml;

    @Column(name = "tags")
    @JdbcTypeCode(SqlTypes.ARRAY)
    List<String> tags;
}
