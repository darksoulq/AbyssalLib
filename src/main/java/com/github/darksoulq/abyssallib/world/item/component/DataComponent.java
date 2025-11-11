package com.github.darksoulq.abyssallib.world.item.component;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.util.Identifier;

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
}
