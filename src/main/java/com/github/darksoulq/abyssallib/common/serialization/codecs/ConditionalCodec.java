package com.github.darksoulq.abyssallib.common.serialization.codecs;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.schema.CodecVisitor;

import java.util.function.Predicate;

/**
 * Codec that selects between two codecs based on a predicate.
 *
 * @param <T> Value type.
 */
public class ConditionalCodec<T> implements Codec<T> {
    private final Predicate<T> condition;
    private final Codec<T> trueCodec;
    private final Codec<T> falseCodec;

    /**
     * Creates a conditional codec.
     *
     * @param condition  Predicate used to select the active codec.
     * @param trueCodec  Codec used when the predicate matches.
     * @param falseCodec Codec used when the predicate does not match.
     */
    public ConditionalCodec(Predicate<T> condition, Codec<T> trueCodec, Codec<T> falseCodec) {
        this.condition = condition;
        this.trueCodec = trueCodec;
        this.falseCodec = falseCodec;
    }

    @Override
    public <D> DataResult<T> decode(DynamicOps<D> ops, D input) {
        DataResult<T> res = trueCodec.decode(ops, input);
        if (res.isSuccess() && condition.test(res.getOrThrow())) return res;
        return falseCodec.decode(ops, input);
    }

    @Override
    public <D> DataResult<D> encode(DynamicOps<D> ops, T value) {
        return condition.test(value) ? trueCodec.encode(ops, value) : falseCodec.encode(ops, value);
    }

    @Override
    public String describe() {
        return "Conditional[" + trueCodec.describe() + " | " + falseCodec.describe() + "]";
    }

    @Override
    public <R> R accept(CodecVisitor<R> visitor) {
        return visitor.visitEither(trueCodec, falseCodec);
    }
}