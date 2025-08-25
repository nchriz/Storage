package org.storage.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum Visibility {
    PUBLIC,
    PRIVATE;

    private static final Map<String, Visibility> LOOKUP_MAP;

    static {
        Map<String, Visibility> map = new HashMap<>();
        for (Visibility v : values()) {
            map.put(v.name().toUpperCase(), v);
        }
        LOOKUP_MAP = Collections.unmodifiableMap(map);
    }

    public static Visibility fromString(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Visibility cannot be null");
        }
        Visibility visibility = LOOKUP_MAP.get(value.trim().toUpperCase());
        if (visibility == null) {
            throw new IllegalArgumentException("Invalid visibility value: " + value);
        }
        return visibility;
    }
}
