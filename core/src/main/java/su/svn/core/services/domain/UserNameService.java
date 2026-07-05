/*
 * This file was last modified at 2026.04.23 20:14 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * UserNameService.java
 * $Id$
 */

package su.svn.core.services.domain;

import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.core.userdetails.UserDetailsService;
import su.svn.core.domain.entities.UserName;
import su.svn.core.models.dto.NewUserName;

/**
 * Service interface for managing {@link UserName}.
 *
 * <p>Provides methods for lookup and creation of user names.</p>
 */
public interface UserNameService {

    UserName findByUserName(String userName) throws ChangeSetPersister.NotFoundException;

    UserName save(NewUserName userName);

    UserDetailsService userDetailsService();
}
