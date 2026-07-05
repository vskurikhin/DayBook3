/*
 * This file was last modified at 2026.05.24 13:27 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * VectorRecord.java
 * $Id$
 */

package su.svn.core.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

/**
 * Entity representing an XML-based text record.
 *
 * <p>
 * This entity stores XML content together with metadata inherited
 * through the associated {@link BaseRecord} entity.
 * It is mapped to the {@code core.vector_records} database table.
 * </p>
 *
 * <h2>Main Features</h2>
 * <ul>
 *     <li>Stores XML document content</li>
 *     <li>Supports logical deletion through the {@code enabled} flag</li>
 *     <li>Maintains ownership and audit information</li>
 *     <li>Supports tagging and parent-child relationships via {@link BaseRecord}</li>
 * </ul>
 *
 * <h2>Persistence Details</h2>
 * <ul>
 *     <li>The primary key is shared with {@link BaseRecord}</li>
 *     <li>XML data is stored using SQL XML type</li>
 *     <li>Creation and update timestamps are automatically managed</li>
 * </ul>
 */
@Accessors(fluent = true)
@AllArgsConstructor
@Builder
@Entity
@EqualsAndHashCode(callSuper = false, exclude = "id")
@FieldDefaults(level = PRIVATE)
@Getter
@NoArgsConstructor
@Setter
@Table(name = "xml_records", schema = "core")
@ToString(exclude = "baseRecord")
public class XmlRecord {
    @Id
    @Column(name = "id", updatable = false)
    UUID id;

    @MapsId
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(
            name = "id",
            referencedColumnName = "id",
            nullable = false)
    BaseRecord baseRecord;

    @Column(name = "xml", nullable = false)
    @JdbcTypeCode(SqlTypes.SQLXML)
    String xml;

    @Builder.Default
    @Column(name = "type")
    su.svn.lib.TextRecordType type = su.svn.lib.TextRecordType.Value;

    @Column(name = "user_name", nullable = false)
    String userName;

    @CreationTimestamp
    @Column(name = "create_time", updatable = false, nullable = false)
    LocalDateTime createTime;

    @UpdateTimestamp
    @Column(name = "update_time")
    LocalDateTime updateTime;

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
}
