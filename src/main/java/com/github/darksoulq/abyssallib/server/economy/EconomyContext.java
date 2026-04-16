package com.github.darksoulq.abyssallib.server.economy;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public record EconomyContext(Map<String, String> values) {
    public static final EconomyContext GLOBAL = new EconomyContext(Collections.emptyMap());

    public static EconomyContext of(String key, String value) {
        return new EconomyContext(Collections.singletonMap(key, value));
    }

    public EconomyContext merge(EconomyContext other) {
        Map<String, String> merged = new HashMap<>(this.values);
        merged.putAll(other.values);
        return new EconomyContext(Collections.unmodifiableMap(merged));
    }
}