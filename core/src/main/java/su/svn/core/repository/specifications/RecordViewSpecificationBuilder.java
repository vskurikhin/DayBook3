/*
 * This file was last modified at 2026.04.06 22:35 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * RecordViewSpecificationBuilder.java
 * $Id$
 */

package su.svn.core.repository.specifications;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import su.svn.core.domain.entities.RecordView;
import su.svn.core.domain.entities.RecordView_;
import su.svn.core.models.dto.ResourceRecordViewFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder for creating {@link org.springframework.data.jpa.domain.Specification}
 * instances for filtering {@link su.svn.core.domain.entities.RecordView}.
 *
 * <p>Supports dynamic filtering based on request parameters.</p>
 *
 * @author Victor N. Skurikhin
 */
@Component
public class RecordViewSpecificationBuilder {
    public Specification<RecordView> build(ResourceRecordViewFilter filterDto) {
        return (root, query, cb) -> {
            List<Predicate> criteriaPredicates = new ArrayList<>();
            addPredicateIfNotNull(criteriaPredicates, cb, root.get(RecordView_.title), filterDto.title());

            if (filterDto.fromDate() != null && filterDto.toDate() != null) {
                criteriaPredicates.add(
                        cb.between(
                                root.get(RecordView_.postAt),
                                filterDto.fromDate(), filterDto.toDate()
                        )
                );
            }
            if (filterDto.fromTime() != null) {
                criteriaPredicates.add(
                        cb.greaterThanOrEqualTo(
                                root.get(RecordView_.lastChangedTime),
                                filterDto.fromTime()
                        )
                );
            }
            if (filterDto.withDisabled() != null && !filterDto.withDisabled()) {
                criteriaPredicates.add(
                        cb.equal(root.get(RecordView_.ENABLED), true)
                );
            }
            return cb.and(criteriaPredicates.toArray(new Predicate[0]));
        };
    }

    private <T> void addPredicateIfNotNull(List<Predicate> predicates, CriteriaBuilder cb, Path<T> field, T value) {
        if (value != null) {
            predicates.add(cb.equal(field, value));
        }
    }
}
