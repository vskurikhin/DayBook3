/*
 * This file was last modified at 2026.05.29 19:00 by Victor N. Skurikhin.
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
import org.hibernate.annotations.Array;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

/**
 * Entity representing a vector-based record stored in the system.
 *
 * <p>
 * This entity is mapped to the {@code core.vector_records} table and stores
 * vector embeddings together with metadata and ownership information.
 * </p>
 *
 * <h2>Main Features</h2>
 * <ul>
 *     <li>Stores vector embeddings using PostgreSQL VECTOR type</li>
 *     <li>Linked to {@link BaseRecord} through a shared primary key</li>
 *     <li>Supports logical deletion using the {@code enabled} flag</li>
 *     <li>Tracks creation and update timestamps automatically</li>
 * </ul>
 *
 * <h2>Persistence</h2>
 * <p>
 * The entity uses a one-to-one relationship with {@link BaseRecord}
 * and shares the same identifier value.
 * </p>
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
@Table(name = "vector_records", schema = "core")
@ToString(exclude = "baseRecord")
public class VectorRecord {
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

    @Column(name = "vector", columnDefinition = "core.vector(1024)", nullable = false)
    @JdbcTypeCode(SqlTypes.VECTOR)
    @Array(length = 1024)
    float[] vector;

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
