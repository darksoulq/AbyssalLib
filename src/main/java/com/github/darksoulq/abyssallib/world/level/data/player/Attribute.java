package com.github.darksoulq.abyssallib.world.level.data.player;

public final class Attribute<T> {
    private final String key;
    private final Class<T> type;
    private final T defaultValue;

    public Attribute(String key, Class<T> type, T defaultValue) {
        this.key = key;
        this.type = type;
        this.defaultValue = defaultValue;
    }

    public String key() {
        return key;
    }

    public Class<T> type() {
        return type;
    }

    public T defaultValue() {
        return defaultValue;
    }
}
