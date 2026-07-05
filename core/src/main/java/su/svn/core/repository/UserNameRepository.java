/*
 * This file was last modified at 2026.03.27 14:01 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * UserNameRepository.java
 * $Id$
 */

package su.svn.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import su.svn.core.domain.entities.UserName;

import java.util.Optional;

/**
 * Repository for managing {@link UserName} entities.
 *
 * <p>Provides lookup by user name.</p>
 */
@Repository
public interface UserNameRepository extends JpaRepository<UserName, String> {
    Optional<UserName> findByUserName(String userName);
}
