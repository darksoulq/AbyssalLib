package com.github.darksoulq.abyssallib.common.serialization.codecs;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.schema.CodecVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * Codec that attempts multiple codecs until one succeeds.
 *
 * @param <T> Common value type.
 */
public record OneOfCodec<T>(List<Codec<T>> codecs) implements Codec<T> {
    /**
     * Creates a one-of codec.
     *
     * @param codecs Candidate codecs evaluated in order.
     */
    public OneOfCodec(List<Codec<T>> codecs) {
        this.codecs = List.copyOf(codecs);
    }

    /**
     * Returns the available codec branches.
     *
     * @return Immutable codec list.
     */
    @Override
    public List<Codec<T>> codecs() {
        return codecs;
    }

    @Override
    public <D> DataResult<T> decode(DynamicOps<D> ops, D input) {
        List<String> errors = new ArrayList<>();
        for (Codec<T> c : codecs) {
            DataResult<T> res = c.decode(ops, input);
            if (res.isSuccess()) return res;
            res.error().ifPresent(errors::add);
        }
        return DataResult.error(DataError.custom("No codec in OneOf matched. Errors: " + String.join(", ", errors)));
    }

    @Override
    public <D> DataResult<D> encode(DynamicOps<D> ops, T value) {
        List<String> errors = new ArrayList<>();
        for (Codec<T> c : codecs) {
            DataResult<D> res = c.encode(ops, value);
            if (res.isSuccess()) return res;
            res.error().ifPresent(errors::add);
        }
        return DataResult.error(DataError.custom("No codec in OneOf could encode. Errors: " + String.join(", ", errors)));
    }

    @Override
    public String describe() {
        List<String> descriptions = new ArrayList<>();
        for (Codec<T> codec : codecs) descriptions.add(codec.describe());
        return "OneOf[" + String.join(", ", descriptions) + "]";
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public <R> R accept(CodecVisitor<R> visitor) {
        return visitor.visitOneOf((List<Codec<?>>) (List<?>) codecs);
    }
}