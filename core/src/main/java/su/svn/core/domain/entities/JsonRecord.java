/*
 * This file was last modified at 2026.05.21 16:48 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * JsonRecord.java
 * $Id$
 */

package su.svn.core.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

/**
 * Entity representing a JSON-based record.
 *
 * <p>This entity extends {@link BaseRecord} using a shared primary key
 * and stores additional structured data in JSON format.</p>
 *
 * <p>Key features:</p>
 * <ul>
 *     <li>One-to-one relationship with {@link BaseRecord}</li>
 *     <li>JSON storage using {@code Map<String, String>}</li>
 *     <li>Additional metadata fields</li>
 * </ul>
 *
 * <p>Mapped to table {@code core.json_records}.</p>
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
@Table(name = "json_records", schema = "core")
@ToString(exclude = "baseRecord")
public class JsonRecord {
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

    @Column(name = "json", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    Map<String, String> json;

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
