/*
 * This file was last modified at 2026.05.08 09:18 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * BaseRecord.java
 * $Id$
 */

package su.svn.core.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

/**
 * Base entity representing a generic record in the system.
 *
 * <p>This entity serves as the root for different record types and supports
 * hierarchical relationships via self-referencing parent linkage.</p>
 *
 * <p>Key features:</p>
 * <ul>
 *     <li>UUID-based identifier</li>
 *     <li>Parent-child relationship</li>
 *     <li>Audit fields (creation/update timestamps)</li>
 *     <li>Soft-delete and visibility flags</li>
 * </ul>
 *
 * <p>Mapped to table {@code core.base_records}.</p>
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
@Table(name = "base_records", schema = "core")
@ToString
public class BaseRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "id",
            updatable = false,
            nullable = false,
            columnDefinition = "uuid DEFAULT pg_catalog.uuidv7()"
    )
    UUID id;

    @Column(name = "parent_id", nullable = false)
    UUID parentId;

    @Builder.Default
    @Column(name = "type")
    su.svn.lib.RecordType type = su.svn.lib.RecordType.Base;

    @Column(name = "user_name", nullable = false)
    String userName;

    @Column(name = "post_at", updatable = false, nullable = false)
    OffsetDateTime postAt;

    @Column(name = "refresh_at")
    OffsetDateTime refreshAt;

    @Column(name = "create_time", updatable = false, nullable = false)
    LocalDateTime createTime;

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

    @Column(name = "title")
    String title;

    @Column(name = "ahref")
    String aHref;

    @ManyToMany
    @JoinTable(
            schema = "core",
            name = "base_records_has_tags",
            joinColumns = @JoinColumn(name = "base_records_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @Builder.Default
    List<Tag> tags = new ArrayList<>();
}
