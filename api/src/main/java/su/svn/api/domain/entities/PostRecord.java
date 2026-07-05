/*
 * This file was last modified at 2026.07.01 23:05 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * PostRecord.java
 * $Id$
 */

package su.svn.api.domain.entities;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.quarkus.hibernate.reactive.panache.PanacheQuery;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import su.svn.api.services.converters.FloatArrayVectorConverter;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.*;

import static lombok.AccessLevel.PRIVATE;

/**
 * Reactive entity representing a universal post record.
 *
 * <h2>Key Features</h2>
 * <ul>
 *     <li>Supports parent-child relationships via {@code parentId}</li>
 *     <li>Tracks lifecycle timestamps such as creation, update, and last change time</li>
 *     <li>Provides soft-disable functionality via the {@code enabled} flag</li>
 *     <li>Stores additional dynamic attributes in JSON format ({@code json})</li>
 * </ul>
 *
 * <p>This class is a reactive Active Record entity based on Panache and is mapped to the
 * {@code api.post_records} table. It encapsulates metadata and state related to a post,
 * including lifecycle timestamps, visibility flags, and hierarchical relationships and
 * is used as a polymorphic storage model for different record types such as:</p>
 *
 * <ul>
 *     <li>text records</li>
 *     <li>markdown records</li>
 *     <li>blob records</li>
 *     <li>HTML records</li>
 *     <li>JSON records</li>
 *     <li>link records</li>
 * </ul>
 *
 * <p>
 * The entity supports hierarchical parent-child relations,
 * soft deletion, visibility control, reactive CRUD operations,
 * and dynamic structured content.
 * </p>
 *
 * <h2>Reactive Features</h2>
 * <ul>
 *     <li>{@link #findByUUID(UUID)} — find record by UUID</li>
 *     <li>{@link su.svn.api.repository.PostRecordRepository#findLastChangedTime()} — get latest change timestamp</li>
 *     <li>{@link #findLastChangedTimePostRecord()} — get latest changed record</li>
 *     <li>{@link su.svn.api.repository.PostRecordRepository#disable(UUID)} — perform soft deletion</li>
 *     <li>{@link #readEnabledAndIdIn(List)} — load enabled records by identifiers</li>
 *     <li>{@link #readEnabledOrderByPostAtAndRefreshAtDesc()} — load ordered enabled records</li>
 *     <li>{@link su.svn.api.repository.PostRecordRepository#update(PostRecord)} — update mutable entity fields</li>
 * </ul>
 *
 * <h2>Named Queries</h2>
 * <ul>
 *     <li>{@value #FIND_BY_UUID} – find record by UUID</li>
 *     <li>{@value #FIND_LAST_CHANGED_TIME_POST_RECORD} – fetch latest changed record</li>
 *     <li>{@value #READ_ENABLED_AND_ID_IN} – fetch enabled records by ID list</li>
 *     <li>{@value #READ_ENABLED_ORDER_POST_REFRESH_DESC} – fetch enabled records ordered by timestamps</li>
 * </ul>
 *
 * <h2>Persistence Notes</h2>
 * <ul>
 *     <li>{@code sequenceId} is the database primary key</li>
 *     <li>{@code id} is a business UUID identifier</li>
 *     <li>JSON fields are stored using PostgreSQL JSON type</li>
 *     <li>Text collections are stored as SQL ARRAY values</li>
 * </ul>
 *
 * <h2>Soft Delete Strategy</h2>
 * <p>
 * Records are never physically removed from the database.
 * The {@code enabled} flag is used to mark records as inactive.
 * </p>
 *
 * <h2>Concurrency and Timeout</h2>
 * <p>
 * Operations such as {@link su.svn.api.repository.PostRecordRepository#disable(UUID)} and {@link su.svn.api.repository.PostRecordRepository#update(PostRecord)} include timeout
 * handling via {@link #TIMEOUT_DURATION} to prevent indefinite waiting.
 * </p>
 *
 * @see io.quarkus.hibernate.reactive.panache.PanacheEntityBase
 * @see java.util.UUID
 * @see io.smallrye.mutiny.Uni
 */
@Accessors(fluent = true)
@AllArgsConstructor
@Builder
@Entity
@EqualsAndHashCode(callSuper = false, exclude = {"id", "parent"})
@FieldDefaults(level = PRIVATE)
@Getter
@NoArgsConstructor
@Setter
@Table(schema = "api", name = "post_records")
@ToString(exclude = "parent")
@NamedQueries({
        @NamedQuery(
                name = PostRecord.FIND_BY_UUID,
                query = "FROM PostRecord WHERE id = :id"
        ),
        @NamedQuery(
                name = PostRecord.FIND_LAST_CHANGED_TIME_POST_RECORD,
                query = """
                        FROM PostRecord
                        ORDER BY lastChangedTime DESC, id DESC
                        """
        ),
        @NamedQuery(
                name = PostRecord.READ_ID_IN,
                query = """
                        FROM PostRecord
                        WHERE id IN :ids
                        ORDER BY postAt DESC, refreshAt DESC, id DESC
                        """
        ),
        @NamedQuery(
                name = PostRecord.READ_ENABLED_AND_ID_IN,
                query = """
                        FROM PostRecord
                        WHERE enabled = :enabled AND id IN :ids
                        ORDER BY postAt DESC, refreshAt DESC, id DESC
                        """
        ),
        @NamedQuery(
                name = PostRecord.READ_ENABLED_ORDER_POST_REFRESH_DESC,
                query = """
                        FROM PostRecord
                        WHERE enabled = :enabled
                        ORDER BY postAt DESC, refreshAt DESC, id DESC
                        """
        )
})
public class PostRecord extends PanacheEntityBase implements Serializable {

    /**
     * Default timeout duration for reactive operations.
     */
    public static final Duration TIMEOUT_DURATION = Duration.ofMillis(2000);

    /**
     * Named query for finding a record by UUID.
     */
    public static final String FIND_BY_UUID = "PostRecord.findByUUID";

    /**
     * Named query for retrieving the latest changed record.
     */
    public static final String FIND_LAST_CHANGED_TIME_POST_RECORD =
            "PostRecord.findLastChangedTimePostRecord";

    /**
     * Named query for reading records by identifiers.
     */
    public static final String READ_ID_IN =
            "PostRecord.readIdIn";

    /**
     * Named query for reading enabled records by identifiers.
     */
    public static final String READ_ENABLED_AND_ID_IN =
            "PostRecord.readEnabledAndIdIn";

    /**
     * Named query for reading enabled records ordered by timestamps.
     */
    public static final String READ_ENABLED_ORDER_POST_REFRESH_DESC =
            "PostRecord.readEnabledOrderByPostAtAndRefreshAtDesc";

    /**
     * Shared query parameter map for enabled records.
     */
    public static final Map<String, Object> ENABLED =
            Map.of("enabled", Boolean.TRUE);

    /**
     * Business UUID identifier.
     */
    @Column(name = "id", updatable = false, nullable = false)
    UUID id;

    /**
     * Parent record identifier.
     */
    @Column(name = "parent_id", nullable = false)
    UUID parentId;

    /**
     * Parent entity reference.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "parent_id",
            referencedColumnName = "id",
            insertable = false,
            nullable = false,
            updatable = false)
    PostRecord parent;

    /**
     * Database sequence identifier.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "postRecordSeq")
    @SequenceGenerator(
            name = "postRecordSeq",
            schema = "api",
            sequenceName = "post_records_seq",
            allocationSize = 1
    )
    @Column(name = "sequence_id", updatable = false, nullable = false)
    Long sequenceId;

    /**
     * Type of stored record.
     */
    @Builder.Default
    @Column(name = "type", nullable = false)
    su.svn.lib.RecordType type = su.svn.lib.RecordType.Base;

    /**
     * Record owner username.
     */
    @Column(name = "user_name", nullable = false)
    String userName;

    /**
     * Publication timestamp.
     */
    @Column(name = "post_at", updatable = false, nullable = false)
    OffsetDateTime postAt;

    /**
     * Refresh timestamp.
     */
    @Column(name = "refresh_at")
    OffsetDateTime refreshAt;

    /**
     * Entity creation timestamp.
     */
    @Column(name = "create_time", updatable = false)
    LocalDateTime createTime;

    /**
     * Last entity update timestamp.
     */
    @Column(name = "update_time")
    LocalDateTime updateTime;

    /**
     * Timestamp of the latest content modification.
     */
    @Column(name = "last_changed_time", nullable = false)
    LocalDateTime lastChangedTime;

    /**
     * Indicates whether the record is active.
     */
    @Builder.Default
    @Column(name = "enabled")
    boolean enabled = true;

    /**
     * Indicates whether the record contains unsynchronized local changes.
     */
    @Builder.Default
    @Column(name = "local_change", nullable = false)
    boolean localChange = true;

    /**
     * Visibility flag.
     */
    @Column(name = "visible")
    Boolean visible;

    /**
     * Bitmask flags associated with the record.
     */
    @Column(name = "flags", nullable = false)
    int flags;

    /**
     * Record title.
     */
    @Column(name = "title", columnDefinition = "TEXT")
    String title;

    @Column(name = "ahref", columnDefinition = "TEXT")
    String aHref;

    /**
     * Binary content.
     */
    @Column(name = "blob")
    byte[] blob;

    /**
     * JSON structured content.
     */
    @Column(name = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    Map<String, String> json;

    /**
     * Collection of textual values.
     */
    @Column(name = "texts")
    @JdbcTypeCode(SqlTypes.ARRAY)
    Set<String> texts;

    /**
     * File name value.
     */
    @Column(name = "file_name")
    String fileName;

    /**
     * HTML content.
     */
    @Column(name = "html")
    String html;

    /**
     * External link.
     */
    @Column(name = "link")
    String link;

    /**
     * Markdown content.
     */
    @Column(name = "markdown")
    String markdown;

    /**
     * Plain text value.
     */
    @Column(name = "value")
    String value;

    @Column(name = "vector", columnDefinition = "jsonb")
    @Convert(converter = FloatArrayVectorConverter.class)
    float[] vector;

    @Column(name = "xml", columnDefinition = "xml")
    String xml;

    @Column(name = "tags")
    List<String> tags;

    // Count using a query string with positional parameters
    public static Uni<Long> countEnabled() {
        return count("enabled = ?1", true);
    }

    /**
     * Finds a post record by UUID.
     *
     * @param id record identifier
     * @return reactive result containing the found record
     */
    public static Uni<PostRecord> findByUUID(@Nonnull UUID id) {
        return find("#" + FIND_BY_UUID, Map.of("id", id)).firstResult();
    }

    /**
     * Retrieves the latest changed record.
     *
     * @return reactive result containing the latest modified record
     */
    public static Uni<PostRecord> findLastChangedTimePostRecord() {
        return PostRecord.find("#" + FIND_LAST_CHANGED_TIME_POST_RECORD)
                .page(0, 1)
                .firstResult();
    }

    /**
     * Reads enabled records by identifier list.
     *
     * @param ids list of record identifiers
     * @return reactive result containing matching records
     */
    public static Uni<List<PostRecord>> readIdIn(List<UUID> ids) {
        return PostRecord.find("#" + READ_ID_IN, Map.of("ids", ids)).list();
    }

    /**
     * Reads enabled records by identifier list.
     *
     * @param ids list of record identifiers
     * @return reactive result containing matching records
     */
    @SuppressWarnings("unused")
    public static Uni<List<PostRecord>> readEnabledAndIdIn(List<UUID> ids) {
        return PostRecord.find(
                "#" + READ_ENABLED_AND_ID_IN,
                Map.of("enabled", Boolean.TRUE, "ids", ids)
        ).list();
    }

    /**
     * Reads enabled records ordered by publication and refresh timestamps.
     *
     * @return reactive query object
     */
    public static PanacheQuery<PostRecord> readEnabledOrderByPostAtAndRefreshAtDesc() {
        return PostRecord.find("#" + READ_ENABLED_ORDER_POST_REFRESH_DESC, PostRecord.ENABLED);
    }
}
