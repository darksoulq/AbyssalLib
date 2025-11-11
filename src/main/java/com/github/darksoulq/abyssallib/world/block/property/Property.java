package com.github.darksoulq.abyssallib.world.block.property;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;

public class Property<T> {
    private final Codec<T> codec;
    private T value;

    public Property(Codec<T> codec, T initialValue) {
        this.codec = codec;
        this.value = initialValue;
    }

    public T get() { return value; }
    public void set(T value) { this.value = value; }

    public <D> D encode(DynamicOps<D> ops) throws Codec.CodecException {
        return codec.encode(ops, value);
    }

    public <D> void decode(DynamicOps<D> ops, D input) throws Codec.CodecException {
        this.value = codec.decode(ops, input);
    }
}
