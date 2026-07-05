/*
 * This file was last modified at 2026.05.22 18:49 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * SetRecord.java
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
import java.util.Set;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

/**
 * Entity representing a set-based record stored in the system.
 *
 * <p>This entity contains a collection of unique string values along with
 * metadata such as visibility, flags, ownership, and audit timestamps.</p>
 *
 * <p>The entity is linked one-to-one with {@link BaseRecord} using a shared identifier.</p>
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
@Table(name = "set_records", schema = "core")
@ToString(exclude = "baseRecord")
public class SetRecord {
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

    @Column(name = "texts", nullable = false)
    @JdbcTypeCode(SqlTypes.ARRAY)
    Set<String> texts;

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
