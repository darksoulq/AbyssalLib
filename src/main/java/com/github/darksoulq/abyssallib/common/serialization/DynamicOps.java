package com.github.darksoulq.abyssallib.common.serialization;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class DynamicOps<T> {
    public abstract T createString(String value);
    public abstract T createInt(int value);
    public abstract T createLong(long value);
    public abstract T createFloat(float value);
    public abstract T createDouble(double value);
    public abstract T createBoolean(boolean value);

    public abstract T createList(List<T> elements);
    public abstract T createMap(Map<T, T> map);

    public abstract Optional<String> getStringValue(T input);
    public abstract Optional<Integer> getIntValue(T input);
    public abstract Optional<Long> getLongValue(T input);
    public abstract Optional<Float> getFloatValue(T input);
    public abstract Optional<Double> getDoubleValue(T input);
    public abstract Optional<Boolean> getBooleanValue(T input);

    public abstract Optional<List<T>> getList(T input);
    public abstract Optional<Map<T, T>> getMap(T input);

    public abstract T empty();
}
