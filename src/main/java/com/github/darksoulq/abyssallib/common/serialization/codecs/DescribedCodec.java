package com.github.darksoulq.abyssallib.common.serialization.codecs;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.schema.CodecVisitor;

public class DescribedCodec<T> implements Codec<T> {
    private final String description;
    private final Codec<T> self;

    public DescribedCodec(Codec<T> codec, String description) {
        self = codec;
        this.description = description;
    }

    @Override
    public <D> DataResult<T> decode(DynamicOps<D> ops, D input) {
        return self.decode(ops, input);
    }

    @Override
    public <D> DataResult<D> encode(DynamicOps<D> ops, T value) {
        return self.encode(ops, value);
    }

    @Override
    public <R> R accept(CodecVisitor<R> visitor) {
        return self.accept(visitor);
    }

    @Override
    public String describe() {
        return description;
    }
}
