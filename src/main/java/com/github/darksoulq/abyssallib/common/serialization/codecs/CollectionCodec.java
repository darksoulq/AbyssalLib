package com.github.darksoulq.abyssallib.common.serialization.codecs;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.schema.CodecVisitor;
import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Codec implementation for collection types.
 *
 * @param <T> Element type.
 * @param <C> Collection implementation type.
 */
public class CollectionCodec<T, C extends Collection<T>> implements Codec<C> {
    private final Supplier<C> factory;
    private final Codec<T> elementCodec;
    private final Function<C, C> wrapper;

    /**
     * Creates a collection codec.
     *
     * @param factory Supplier used to create collection instances.
     * @param elementCodec Codec used for collection elements.
     * @param wrapper Function applied before returning decoded collections.
     */
    public CollectionCodec(Supplier<C> factory, Codec<T> elementCodec, Function<C, C> wrapper) {
        this.factory = factory;
        this.elementCodec = elementCodec;
        this.wrapper = wrapper;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <D> DataResult<C> decode(DynamicOps<D> ops, D input) {
        if (elementCodec instanceof OneOfCodec<T>(List<Codec<T>> codecs)) {
            List<Codec<C>> options = new ArrayList<>();
            for (Codec<T> c : codecs) {
                options.add(new CollectionCodec<>(factory, c, wrapper));
            }
            Codec<C>[] arr = options.toArray(new Codec[0]);
            return Codec.oneOf(arr).decode(ops, input);
        }

        return ops.getList(input)
            .map(DataResult::success)
            .orElseGet(() -> DataResult.error(DataError.typeMismatch("list", "unknown")))
            .flatMap(rawList -> {
                C result = factory.get();
                int index = 0;
                List<DataError> combinedWarnings = new ArrayList<>();
                for (D elem : rawList) {
                    DataResult<T> dec = elementCodec.decode(ops, elem).prependPath("[" + index + "]");
                    if (dec.isError()) return DataResult.<C>error(dec.dataError().get()).prependPath("[" + index + "]");
                    if (dec.isPartial()) combinedWarnings.addAll(dec.warnings());
                    result.add(dec.getOrThrow());
                    index++;
                }
                return combinedWarnings.isEmpty() ? DataResult.success(wrapper.apply(result)) : DataResult.partial(wrapper.apply(result), combinedWarnings);
            });
    }

    @Override
    @SuppressWarnings("unchecked")
    public <D> DataResult<D> encode(DynamicOps<D> ops, C value) {
        if (elementCodec instanceof OneOfCodec<T>(List<Codec<T>> codecs)) {
            List<Codec<C>> options = new ArrayList<>();
            for (Codec<T> c : codecs) {
                options.add(new CollectionCodec<>(factory, c, wrapper));
            }
            Codec<C>[] arr = options.toArray(new Codec[0]);
            return Codec.oneOf(arr).encode(ops, value);
        }

        List<D> result = new ArrayList<>(value.size());
        int index = 0;
        List<DataError> combinedWarnings = new ArrayList<>();
        for (T elem : value) {
            DataResult<D> enc = elementCodec.encode(ops, elem).prependPath("[" + index + "]");
            if (enc.isError()) return DataResult.<D>error(enc.dataError().get()).prependPath("[" + index + "]");
            if (enc.isPartial()) combinedWarnings.addAll(enc.warnings());
            result.add(enc.getOrThrow());
            index++;
        }
        return combinedWarnings.isEmpty() ? DataResult.success(ops.createList(result)) : DataResult.partial(ops.createList(result), combinedWarnings);
    }

    @Override
    public String describe() {
        return "Collection[" + elementCodec.describe() + "]";
    }

    @Override
    public <R> R accept(CodecVisitor<R> visitor) {
        return visitor.visitList(elementCodec);
    }
}