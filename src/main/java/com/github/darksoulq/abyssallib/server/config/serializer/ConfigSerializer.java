package com.github.darksoulq.abyssallib.server.config.serializer;

import java.lang.reflect.Field;

public interface ConfigSerializer<T> {
    Object serialize(T value);
    T deserialize(Object in, Field field);
}
