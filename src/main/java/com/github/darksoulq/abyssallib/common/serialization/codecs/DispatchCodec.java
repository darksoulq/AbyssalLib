package com.github.darksoulq.abyssallib.common.serialization.codecs;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.schema.CodecVisitor;
import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Codec supporting polymorphic serialization through a discriminator field.
 *
 * @param <K> Discriminator key type.
 * @param <V> Polymorphic value type.
 */
public class DispatchCodec<K, V> implements Codec<V> {
    private final String typeKey;
    private final Codec<K> keyCodec;
    private final Function<? super V, ? extends K> typeGetter;
    private final Function<? super K, ? extends Codec<? extends V>> codecGetter;

    /**
     * Creates a dispatch codec.
     *
     * @param typeKey Name of the discriminator field.
     * @param keyCodec Codec used for discriminator values.
     * @param typeGetter Extracts the discriminator from a value.
     * @param codecGetter Resolves a codec from a discriminator value.
     */
    public DispatchCodec(String typeKey, Codec<K> keyCodec, Function<? super V, ? extends K> typeGetter, Function<? super K, ? extends Codec<? extends V>> codecGetter) {
        this.typeKey = typeKey;
        this.keyCodec = keyCodec;
        this.typeGetter = typeGetter;
        this.codecGetter = codecGetter;
    }

    @Override
    public <D> DataResult<V> decode(DynamicOps<D> ops, D input) {
        return ops.getMap(input)
            .map(DataResult::success)
            .orElseGet(() -> DataResult.error(DataError.typeMismatch("map", "unknown")))
            .flatMap(map -> {
                D typeNode = map.get(ops.createString(typeKey));
                if (typeNode == null) return DataResult.error(DataError.missingField(typeKey));

                return keyCodec.decode(ops, typeNode).prependPath(typeKey).flatMap(key -> {
                    Codec<? extends V> codec = codecGetter.apply(key);
                    if (codec == null) return DataResult.error(DataError.custom("Unknown polymorphic type: " + key));
                    return codec.decode(ops, input).map(v -> (V) v);
                });
            });
    }

    @Override
    @SuppressWarnings("unchecked")
    public <D> DataResult<D> encode(DynamicOps<D> ops, V value) {
        K key = typeGetter.apply(value);
        Codec<V> codec = (Codec<V>) codecGetter.apply(key);
        if (codec == null) return DataResult.error(DataError.custom("Unregistered dispatch type: " + key));

        return codec.encode(ops, value).flatMap(encoded ->
            ops.getMap(encoded)
                .map(DataResult::success)
                .orElseGet(() -> DataResult.error(DataError.typeMismatch("map", "unknown")))
                .flatMap(map -> keyCodec.encode(ops, key).prependPath(typeKey).flatMap(encodedKey -> {
                    Map<D, D> newMap = new LinkedHashMap<>(map);
                    newMap.put(ops.createString(typeKey), encodedKey);
                    return DataResult.success(ops.createMap(newMap));
                }))
        );
    }

    @Override
    public String describe() {
        return "Dispatch[" + typeKey + "]";
    }

    @Override
    public <R> R accept(CodecVisitor<R> visitor) {
        return visitor.visitDispatch(typeKey);
    }
}