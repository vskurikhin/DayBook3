/*
 * This file was last modified at 2026.05.21 23:42 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * RecordServiceHelper.java
 * $Id$
 */

package su.svn.core.services.domain;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import su.svn.core.domain.entities.BaseRecord;
import su.svn.core.domain.entities.Tag;
import su.svn.core.domain.entities.UserName;
import su.svn.core.repository.TagRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
@Slf4j
public class RecordServiceHelper {
    /**
     * Имя пользователя по умолчанию для неавторизованных пользователей.
     */
    public static final String GUEST = "guest";

    TagRepository tagRepository;

    /**
     * Обновляет список тегов записи.
     * <p>
     * Существующие теги загружаются из БД,
     * отсутствующие создаются автоматически.
     * </p>
     *
     * @param baseRecord запись
     * @param tags       набор тегов
     * @param username   пользователь
     */
    public void upTagsInBaseRecordFromDB(BaseRecord baseRecord, Set<String> tags, final String username) {
        final List<Tag> existingTags = tagRepository.findByTagIn(tags);
        var existingTagNames = existingTags.stream()
                .map(Tag::tag)
                .collect(Collectors.toSet());
        var newTags = tags.stream()
                .filter(tag -> !existingTagNames.contains(tag))
                .map(tag -> Tag.builder()
                        .tag(tag)
                        .userName(username)
                        .build())
                .toList();
        var savedNewTags = tagRepository.saveAll(newTags);
        List<Tag> allTags = new ArrayList<>();
        allTags.addAll(existingTags);
        allTags.addAll(savedNewTags);
        baseRecord.tags(allTags);
    }

    /**
     * Возвращает имя текущего пользователя.
     *
     * @return имя пользователя или {@value #GUEST}
     */
    public String getUserName() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return switch (principal) {
            case UserName userName -> userName.userName();
            case User user -> user.getUsername();
            default -> GUEST;
        };
    }
}
