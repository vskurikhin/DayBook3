/*
 * This file was last modified at 2026.05.08 09:18 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * Tag.java
 * $Id$
 */

package su.svn.core.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Accessors(fluent = true)
@AllArgsConstructor
@Builder
@Entity
@EqualsAndHashCode(callSuper = false, exclude = {"id", "baseRecords"})
@FieldDefaults(level = PRIVATE)
@Getter
@NoArgsConstructor
@Setter
@Table(name = "tags", schema = "core")
@ToString(exclude = "baseRecords")
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "id",
            updatable = false,
            nullable = false,
            columnDefinition = "uuid DEFAULT pg_catalog.uuidv7()"
    )
    UUID id;

    @Column(name = "tag", updatable = false, nullable = false)
    String tag;

    @Column(name = "user_name", nullable = false)
    String userName;

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

    @ManyToMany(mappedBy = "tags")
    List<BaseRecord> baseRecords;
}
