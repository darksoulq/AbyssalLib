package com.github.darksoulq.abyssallib.common.serialization.codecs;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.schema.CodecVisitor;

import java.util.function.Supplier;

/**
 * Codec that always decodes to a predefined value and encodes to an empty representation.
 *
 * @param <T> Value type.
 */
public class UnitCodec<T> implements Codec<T> {
    private final Supplier<T> value;

    /**
     * Creates a unit codec.
     *
     * @param value Supplier providing the constant value.
     */
    public UnitCodec(Supplier<T> value) {
        this.value = value;
    }

    @Override
    public <D> DataResult<T> decode(DynamicOps<D> ops, D input) {
        return DataResult.success(value.get());
    }

    @Override
    public <D> DataResult<D> encode(DynamicOps<D> ops, T val) {
        return DataResult.success(ops.empty());
    }

    @Override
    public String describe() {
        return "Unit";
    }

    @Override
    public <R> R accept(CodecVisitor<R> visitor) {
        return visitor.visitUnknown(describe());
    }
}