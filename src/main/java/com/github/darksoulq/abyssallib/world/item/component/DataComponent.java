package com.github.darksoulq.abyssallib.world.item.component;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.util.Identifier;

import java.util.Objects;

public abstract class DataComponent<T> {
    private final Identifier id;
    public final Codec<DataComponent<T>> codec;
    public final T value;

    public DataComponent(Identifier id, T defaultValue, Codec<? extends DataComponent<T>> codec) {
        this.id = id;
        @SuppressWarnings("unchecked")
        Codec<DataComponent<T>> safeCodec = (Codec<DataComponent<T>>) codec;
        this.codec = safeCodec;
        this.value = defaultValue;
    }

    public Identifier getId() {
        return id;
    }

    public T getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        DataComponent<?> other = (DataComponent<?>) obj;
        if (!id.equals(other.id)) return false;

        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

}
