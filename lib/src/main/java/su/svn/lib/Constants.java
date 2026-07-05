/*
 * This file was last modified at 2026.05.07 14:57 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * Constants.java
 * $Id$
 */

package su.svn.lib;

import lombok.Getter;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Getter
@FieldDefaults(level = PRIVATE, makeFinal = true)
public enum Constants {
    Null(null),
    All(Constants.REQUEST_ID),
    ;

    public static final String REQUEST_ID = "REQUEST_ID";

    String value;

    Constants(String value) {
        this.value = value;
    }

    public boolean stringEquals(String other) {
        return this.value != null && this.value.equals(other) || other == null;
    }

    public static Constants lookup(String key) {

        if (null == key) {
            return Null;
        }
        for (Constants v : values()) {

            if (v.getValue() != null && v.getValue().equals(key)) {
                return v;
            }
        }
        return null;
    }
}
