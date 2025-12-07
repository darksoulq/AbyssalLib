package com.github.darksoulq.abyssallib.server.bridge;

import com.github.darksoulq.abyssallib.common.util.Identifier;

import java.util.Map;
import java.util.Optional;

public abstract class Provider<T> {
    private final String prefix;
    public Provider(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }

    public abstract boolean belongs(T value);
    public abstract Identifier getId(T value);
    public abstract T get(Identifier id);
    public abstract Map<String, Optional<Object>> serializeData(T value);
    public abstract void deserializeData(Map<String, Optional<Object>> data, T value);
}
