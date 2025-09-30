package com.github.darksoulq.abyssallib.common.serialization;

import java.util.*;
import java.util.function.Function;

/**
 * Represents a bidirectional serialization/deserialization mechanism for a type {@code T}.
 * <p>
 * Supports encoding and decoding using abstract {@link DynamicOps} operations.
 * Provides combinators for mapping, optional values, lists, maps, enums, and fallback codecs.
 *
 * @param <T> the type handled by this codec
 */
public interface Codec<T> {
    /**
     * Decodes a value of type {@code T} from the input using the provided {@link DynamicOps}.
     *
     * @param <D>   the underlying data representation type
     * @param ops   the operations to interpret the input
     * @param input the raw input data
     * @return the decoded value of type {@code T}
     * @throws CodecException if decoding fails
     */
    <D> T decode(DynamicOps<D> ops, D input) throws CodecException;
    /**
     * Encodes a value of type {@code T} into the target representation using {@link DynamicOps}.
     *
     * @param <D>   the underlying data representation type
     * @param ops   the operations to create the encoded value
     * @param value the value to encode
     * @return the encoded representation
     * @throws CodecException if encoding fails
     */
    <D> D encode(DynamicOps<D> ops, T value) throws CodecException;

    /**
     * Exception thrown by {@link Codec} methods if encoding or decoding fails.
     */
    class CodecException extends Exception {
        public CodecException(String message) { super(message); }
        public CodecException(String message, Throwable cause) { super(message, cause); }
    }

    /**
     * Creates a simple codec from a decoder and encoder function.
     *
     * @param decoder function to decode objects
     * @param encoder function to encode objects
     * @param <T>     the type handled by the codec
     * @return a new codec
     */
    static <T> Codec<T> of(Function<Object, T> decoder, Function<T, Object> encoder) {
        return new Codec<>() {
            @Override public <D> T decode(DynamicOps<D> ops, D input) throws CodecException {
                try { return decoder.apply(input); } catch (Exception e) { throw new CodecException("Failed to decode", e); }
            }
            @Override public <D> D encode(DynamicOps<D> ops, T value) throws CodecException {
                try { return (D) encoder.apply(value); } catch (Exception e) { throw new CodecException("Failed to encode", e); }
            }
        };
    }

    /**
     * Returns a mapped codec by transforming values forward and backward.
     *
     * @param forward  maps the original type to the new type
     * @param backward maps the new type back to the original
     * @param <R>      the new type
     * @return a new codec for type {@code R}
     */
    default <R> Codec<R> xmap(CheckedFunction<T, R> forward, CheckedFunction<R, T> backward) {
        Codec<T> self = this;
        return new Codec<>() {
            @Override public <D> R decode(DynamicOps<D> ops, D input) throws CodecException { return forward.apply(self.decode(ops, input)); }
            @Override public <D> D encode(DynamicOps<D> ops, R value) throws CodecException { return self.encode(ops, backward.apply(value)); }
        };
    }
    /**
     * Returns a codec that substitutes a default value if decoding fails or produces null.
     *
     * @param defaultValue the default value to use
     * @return a new codec that never returns null
     */
    default Codec<T> orElse(T defaultValue) {
        Codec<T> self = this;
        return new Codec<>() {
            @Override public <D> T decode(DynamicOps<D> ops, D input) {
                try {
                    T result = self.decode(ops, input);
                    return result != null ? result : defaultValue;
                } catch (CodecException e) {
                    return defaultValue;
                }
            }
            @Override public <D> D encode(DynamicOps<D> ops, T value) throws CodecException {
                return self.encode(ops, value != null ? value : defaultValue);
            }
        };
    }
    /**
     * Returns a codec for {@link List} of this type.
     *
     * @return a list codec
     */
    default Codec<List<T>> list() {
        Codec<T> self = this;
        return new Codec<>() {
            @Override
            public <D> List<T> decode(DynamicOps<D> ops, D input) throws CodecException {
                List<D> rawList = ops.getList(input)
                        .orElseThrow(() -> new CodecException("Expected list"));
                List<T> result = new ArrayList<>();
                for (D elem : rawList) {
                    result.add(self.decode(ops, elem));
                }
                return result;
            }

            @Override
            public <D> D encode(DynamicOps<D> ops, List<T> value) throws CodecException {
                List<D> result = new ArrayList<>();
                for (T elem : value) {
                    result.add(self.encode(ops, elem));
                }
                return ops.createList(result);
            }
        };
    }
    /**
     * Returns a codec for {@link Optional} values of this type.
     *
     * @return an optional codec
     */
    default Codec<Optional<T>> optional() {
        Codec<T> self = this; // preserves the correct type
        return new Codec<>() {
            @Override
            public <D> Optional<T> decode(DynamicOps<D> ops, D input) throws CodecException {
                if (input == null || ops.empty().equals(input)) return Optional.empty();
                return Optional.of(self.decode(ops, input));
            }

            @Override
            public <D> D encode(DynamicOps<D> ops, Optional<T> value) throws CodecException {
                return value.map(v -> {
                    try {
                        return self.encode(ops, v);
                    } catch (CodecException e) {
                        throw new RuntimeException(e);
                    }
                }).orElse(ops.empty());
            }
        };
    }
    /**
     * Returns a codec that allows null values.
     *
     * @param <U> the type
     * @return a nullable codec
     */
    default <U> Codec<U> nullable() {
        Codec<T> self = this;
        return new Codec<>() {
            @Override public <D> U decode(DynamicOps<D> ops, D input) throws CodecException {
                if (input == null || ops.empty().equals(input)) return null;
                return ((Codec<U>) self).decode(ops, input);
            }
            @Override public <D> D encode(DynamicOps<D> ops, U value) throws CodecException {
                return value == null ? ops.empty() : ((Codec<U>) self).encode(ops, value);
            }
        };
    }

    /**
     * Returns a codec for a {@link Map} given key and value codecs.
     *
     * @param keyCodec   codec for keys
     * @param valueCodec codec for values
     * @param <K>        key type
     * @param <V>        value type
     * @return a map codec
     */
    static <K, V> Codec<Map<K, V>> map(Codec<K> keyCodec, Codec<V> valueCodec) {
        return new Codec<>() {
            @Override public <D> Map<K, V> decode(DynamicOps<D> ops, D input) throws CodecException {
                Map<D, D> rawMap = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
                Map<K, V> result = new LinkedHashMap<>();
                for (Map.Entry<D, D> e : rawMap.entrySet()) {
                    result.put(keyCodec.decode(ops, e.getKey()), valueCodec.decode(ops, e.getValue()));
                }
                return result;
            }
            @Override public <D> D encode(DynamicOps<D> ops, Map<K, V> value) throws CodecException {
                Map<D, D> result = new LinkedHashMap<>();
                for (Map.Entry<K, V> e : value.entrySet()) {
                    result.put(keyCodec.encode(ops, e.getKey()), valueCodec.encode(ops, e.getValue()));
                }
                return ops.createMap(result);
            }
        };
    }
    /**
     * Returns a codec for an enum type by serializing its name.
     *
     * @param enumClass the enum class
     * @param <E>       the enum type
     * @return an enum codec
     */
    static <E extends Enum<E>> Codec<E> enumCodec(Class<E> enumClass) {
        return new Codec<>() {
            @Override public <D> E decode(DynamicOps<D> ops, D input) throws Codec.CodecException {
                String name = ops.getStringValue(input).orElseThrow(() -> new Codec.CodecException("Expected enum string"));
                try { return Enum.valueOf(enumClass, name); } catch (IllegalArgumentException e) { throw new Codec.CodecException("Invalid enum value: " + name); }
            }
            @Override public <D> D encode(DynamicOps<D> ops, E value) { return ops.createString(value.name()); }
        };
    }
    /**
     * Returns a codec that tries the left codec, then the right codec if the first fails.
     *
     * @param left  the first codec to try
     * @param right the fallback codec
     * @param <T>   the common type
     * @return a codec that supports either
     */
    static <T> Codec<T> either(Codec<? extends T> left, Codec<? extends T> right) {
        return new Codec<>() {
            @Override
            public <D> T decode(DynamicOps<D> ops, D input) throws CodecException {
                try {
                    return left.decode(ops, input);
                } catch (Exception ignored) {
                    return right.decode(ops, input);
                }
            }

            @Override
            public <D> D encode(DynamicOps<D> ops, T value) throws CodecException {
                try {
                    return ((Codec<T>) left).encode(ops, value);
                } catch (Exception ignored) {
                    return ((Codec<T>) right).encode(ops, value);
                }
            }
        };
    }

    /**
     * Functional interface for functions that can throw a {@link CodecException}.
     */
    @FunctionalInterface
    interface CheckedFunction<T, R> {
        R apply(T t) throws Codec.CodecException;
    }
    /**
     * Represents a named field for use with {@link RecordCodecBuilder}.
     *
     * @param name   the field name
     * @param codec  the codec for the field type
     * @param getter function to extract the field from a parent object
     * @param <T>    parent object type
     * @param <A>    field type
     */
    record Field<T, A>(String name, Codec<A> codec, Function<T, A> getter) {}
    /**
     * Creates a field definition for use in {@link RecordCodecBuilder}.
     *
     * @param name   the field name
     * @param getter function to extract the field
     * @param <P>    parent object type
     * @return a field
     */
    default <P> Field<P, T> fieldOf(String name, Function<P, T> getter) {
        return new Field<>(name, this, getter);
    }
}
