/*
 * This file was last modified at 2026.04.23 20:14 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * UserNameServiceImpl.java
 * $Id$
 */

package su.svn.core.services.domain;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import su.svn.core.domain.entities.UserName;
import su.svn.core.models.dto.NewUserName;
import su.svn.core.repository.UserNameRepository;
import su.svn.core.services.mappers.UserNameMapper;

import java.util.Set;
import java.util.function.Function;

import static lombok.AccessLevel.PRIVATE;

/**
 * Implementation of {@link UserNameService}.
 *
 * <p>Handles persistence and validation logic for user names.</p>
 *
 * @author Victor N. Skurikhin
 */
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
@Slf4j
public class UserNameServiceImpl implements UserNameService {

    UserNameRepository userNameRepository;
    UserNameMapper userNameMapper;

    /**
     * Получение пользователя по имени пользователя
     *
     * @return пользователь
     */
    public User getByUsername(String username) {
        return userNameRepository.findByUserName(username)
                .map(userName -> new User(userName.userName(), null, Set.of(new SimpleGrantedAuthority("GUEST"))))
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

    }

    @Override
    public UserName findByUserName(String userName) throws ChangeSetPersister.NotFoundException {
        return userNameRepository.findByUserName(userName)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
    }

    @Override
    @Transactional
    public UserName save(NewUserName newUserName) {
        return userNameRepository.save(userNameMapper.toEntity(newUserName));
    }

    /**
     * Получение пользователя по имени пользователя
     * <p>
     * Нужен для Spring Security
     *
     * @return пользователь
     */
    public UserDetailsService userDetailsService() {
        return this::getByUsername;
    }
}
