/*
 * This file was last modified at 2026.05.08 09:18 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * UserName.java
 * $Id$
 */

package su.svn.core.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

/**
 * Entity representing a user identifier mapping.
 *
 * <p>Stores a user name and its associated unique identifier.</p>
 *
 * <p>Includes audit fields and flags for soft deletion and visibility.</p>
 *
 * <p>Mapped to table {@code core.user_name}.</p>
 */
@Accessors(fluent = true)
@AllArgsConstructor
@Builder
@Entity
@EqualsAndHashCode
@FieldDefaults(level = PRIVATE)
@Getter
@NoArgsConstructor
@Setter
@Table(name = "user_name", schema = "core")
@ToString
public class UserName {
    @Column(name = "user_name", updatable = false, nullable = false)
    String userName;

    @Column(name = "id")
    UUID id;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userNameSeq")
    @SequenceGenerator(
            name = "userNameSeq",
            schema = "core",
            sequenceName = "user_name_seq",
            allocationSize = 1
    )
    @Column(name = "sequence_id")
    Long sequenceId;

    @CreationTimestamp
    @Column(name = "create_time", updatable = false, nullable = false)
    LocalDateTime createTime;

    @UpdateTimestamp
    @Column(name = "update_time", updatable = false)
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
