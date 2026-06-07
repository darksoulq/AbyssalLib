package com.github.darksoulq.abyssallib.common.serialization.codecs;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.schema.CodecVisitor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Codec implementation for map types.
 *
 * @param <K> Key type.
 * @param <V> Value type.
 * @param <M> Map implementation type.
 */
public class MapCodec<K, V, M extends Map<K, V>> implements Codec<M> {
    private final Codec<K> keyCodec;
    private final Codec<V> valueCodec;
    private final Supplier<M> factory;
    private final Function<M, M> wrapper;

    /**
     * Creates a map codec.
     *
     * @param keyCodec Codec used for keys.
     * @param valueCodec Codec used for values.
     * @param factory Supplier used to create map instances.
     * @param wrapper Function applied before returning decoded maps.
     */
    public MapCodec(Codec<K> keyCodec, Codec<V> valueCodec, Supplier<M> factory, Function<M, M> wrapper) {
        this.keyCodec = keyCodec;
        this.valueCodec = valueCodec;
        this.factory = factory;
        this.wrapper = wrapper;
    }

    /**
     * Creates a mutable map codec.
     *
     * @param keyCodec Codec used for keys.
     * @param valueCodec Codec used for values.
     * @param factory Supplier used to create map instances.
     */
    public MapCodec(Codec<K> keyCodec, Codec<V> valueCodec, Supplier<M> factory) {
        this(keyCodec, valueCodec, factory, Function.identity());
    }

    @Override
    public <D> DataResult<M> decode(DynamicOps<D> ops, D input) {
        return ops.getMap(input)
            .map(DataResult::success)
            .orElseGet(() -> DataResult.error(DataError.typeMismatch("map", "unknown")))
            .flatMap(raw -> {
                M result = factory.get();
                List<DataError> warnings = new ArrayList<>();
                for (var e : raw.entrySet()) {
                    DataResult<K> keyRes = keyCodec.decode(ops, e.getKey()).prependPath("[key]");
                    if (keyRes.isError()) return DataResult.<M>error(keyRes.dataError().get()).prependPath("[key]");
                    if (keyRes.isPartial()) warnings.addAll(keyRes.warnings());

                    DataResult<V> valRes = valueCodec.decode(ops, e.getValue()).prependPath("[" + keyRes.getOrThrow() + "]");
                    if (valRes.isError()) return DataResult.<M>error(valRes.dataError().get()).prependPath("[" + keyRes.getOrThrow() + "]");
                    if (valRes.isPartial()) warnings.addAll(valRes.warnings());

                    result.put(keyRes.getOrThrow(), valRes.getOrThrow());
                }
                return warnings.isEmpty() ? DataResult.success(wrapper.apply(result)) : DataResult.partial(wrapper.apply(result), warnings);
            });
    }

    @Override
    public <D> DataResult<D> encode(DynamicOps<D> ops, M value) {
        Map<D, D> result = new LinkedHashMap<>(value.size());
        List<DataError> warnings = new ArrayList<>();
        for (var e : value.entrySet()) {
            DataResult<D> keyRes = keyCodec.encode(ops, e.getKey()).prependPath("[key]");
            if (keyRes.isError()) return DataResult.<D>error(keyRes.dataError().get()).prependPath("[key]");
            if (keyRes.isPartial()) warnings.addAll(keyRes.warnings());

            DataResult<D> valRes = valueCodec.encode(ops, e.getValue()).prependPath("[" + e.getKey() + "]");
            if (valRes.isError()) return DataResult.<D>error(valRes.dataError().get()).prependPath("[" + e.getKey() + "]");
            if (valRes.isPartial()) warnings.addAll(valRes.warnings());

            result.put(keyRes.getOrThrow(), valRes.getOrThrow());
        }
        return warnings.isEmpty() ? DataResult.success(ops.createMap(result)) : DataResult.partial(ops.createMap(result), warnings);
    }

    @Override
    public String describe() {
        return "Map<" + keyCodec.describe() + ", " + valueCodec.describe() + ">";
    }

    @Override
    public <R> R accept(CodecVisitor<R> visitor) {
        return visitor.visitMap(keyCodec, valueCodec);
    }
}