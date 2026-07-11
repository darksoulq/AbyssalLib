package com.github.darksoulq.abyssallib.common.serialization.codecs;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.schema.CodecVisitor;

import java.util.Optional;

/**
 * Codec that decodes values from a nested path within structured data.
 *
 * @param <T> Target value type.
 */
public class QueryCodec<T> implements Codec<T> {
    private final Codec<T> codec;
    private final String path;

    /**
     * Creates a query codec.
     *
     * @param codec Codec used to decode the resolved value.
     * @param path  Path used to locate the value.
     */
    public QueryCodec(Codec<T> codec, String path) {
        this.codec = codec;
        this.path = path;
    }

    @Override
    public <D> DataResult<T> decode(DynamicOps<D> ops, D input) {
        Optional<D> target = ops.query(input, path);
        return target.isPresent() ? codec.decode(ops, target.get()).prependPath(path) : DataResult.error(DataError.missingField(path));
    }

    @Override
    public <D> DataResult<D> encode(DynamicOps<D> ops, T value) {
        return DataResult.error(DataError.unsupportedOperation("Encoding through a query path codec is not supported"));
    }

    @Override
    public String describe() {
        return "Query[" + path + " -> " + codec.describe() + "]";
    }

    @Override
    public <R> R accept(CodecVisitor<R> visitor) {
        return visitor.visitUnknown(describe());
    }
}