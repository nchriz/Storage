package org.storage.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum Action {
    UPDATE,
    DELETE;

    private static final Map<String, Action> LOOKUP_MAP;

    static {
        Map<String, Action> map = new HashMap<>();
        for (Action v : values()) {
            map.put(v.name().toUpperCase(), v);
        }
        LOOKUP_MAP = Collections.unmodifiableMap(map);
    }

    public static Action fromString(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Action cannot be null");
        }
        Action action = LOOKUP_MAP.get(value.trim().toUpperCase());
        if (action == null) {
            throw new IllegalArgumentException("Invalid action value: " + value);
        }
        return action;
    }
}
