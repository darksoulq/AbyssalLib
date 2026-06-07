package com.github.darksoulq.abyssallib.common.serialization.codecs;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.schema.CodecVisitor;

import java.util.function.Function;

/**
 * Codec wrapper supporting recursive codec definitions.
 *
 * @param <T> Value type.
 */
public class RecursiveCodec<T> implements Codec<T> {
    private final Function<Codec<T>, Codec<T>> builder;
    private Codec<T> lazy;

    /**
     * Creates a recursive codec.
     *
     * @param builder Function used to build the recursive codec.
     */
    public RecursiveCodec(Function<Codec<T>, Codec<T>> builder) {
        this.builder = builder;
    }

    private Codec<T> getCodec() {
        if (lazy == null) {
            lazy = builder.apply(this);
        }
        return lazy;
    }

    @Override
    public <D> DataResult<T> decode(DynamicOps<D> ops, D input) {
        return getCodec().decode(ops, input);
    }

    @Override
    public <D> DataResult<D> encode(DynamicOps<D> ops, T value) {
        return getCodec().encode(ops, value);
    }

    @Override
    public String describe() {
        return "Recursive";
    }

    @Override
    public <R> R accept(CodecVisitor<R> visitor) {
        return visitor.visitUnknown(describe());
    }
}