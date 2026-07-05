/*
 * This file was last modified at 2026.04.05 22:27 by Victor N. Skurikhin.
 * This is free and unencumbered software released into the public domain.
 * For more information, please refer to <http://unlicense.org>
 * ResourcePath.java
 * $Id$
 */

package su.svn.api.domain.enums;

public enum ResourcePath {
    Null(null),
    All(ResourcePath.ALL),
    Record(ResourcePath.RECORD),
    Records(ResourcePath.RECORDS),
;

    public static final String ALL = "/";
    public static final String API_PATH = "/api/v2";
    public static final String ID = "{id}";
    public static final String NONE = "";
    public static final String RECORD = API_PATH + "/record";
    public static final String RECORDS = API_PATH + "/records";

    private final String value;

    ResourcePath(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public boolean stringEquals(String other) {
        return this.value != null && this.value.equals(other) || other == null;
    }

    public static ResourcePath lookup(String key) {

        if (null == key) {
            return Null;
        }
        for (ResourcePath v : values()) {

            if (v.getValue() != null && v.getValue().equals(key)) {
                return v;
            }
        }
        return null;
    }
}
