package com.github.darksoulq.abyssallib.server.config.serializer;

public interface Serializer<T> {
    Object serialize(T value);
    T deserialize(Object value);
}
