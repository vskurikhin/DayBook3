/*
 * This file was last modified at 2026.05.22 09:26 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * BlobRecord.java
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

@Accessors(fluent = true)
@AllArgsConstructor
@Builder
@Entity
@EqualsAndHashCode(callSuper = false, exclude = "id")
@FieldDefaults(level = PRIVATE)
@Getter
@NoArgsConstructor
@Setter
@Table(name = "blob_records", schema = "core")
@ToString(exclude = "baseRecord")
public class BlobRecord {
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

    @Column(name = "blob", nullable = false)
    byte[] blob;

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
