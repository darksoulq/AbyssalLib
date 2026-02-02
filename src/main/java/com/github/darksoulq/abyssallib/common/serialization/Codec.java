package com.github.darksoulq.abyssallib.common.serialization;

import com.github.darksoulq.abyssallib.common.util.Either;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A Codec is a bidirectional serializer/deserializer that can translate between
 * a Java object of type {@code T} and a serialized format {@code D} defined by {@link DynamicOps}.
 * @param <T> The Java type that this codec handles.
 */
public interface Codec<T> {
    /**
     * Decodes a serialized input into a Java object.
     * @param <D>   The type of the serialized data.
     * @param ops   The {@link DynamicOps} instance defining how to read data of type D.
     * @param input The serialized input to decode.
     * @return The decoded Java object of type T.
     * @throws CodecException If the input format is invalid or missing required data.
     */
    <D> T decode(DynamicOps<D> ops, D input) throws CodecException;

    /**
     * Encodes a Java object into its serialized representation.
     * @param <D>   The type of the serialized data.
     * @param ops   The {@link DynamicOps} instance defining how to create data of type D.
     * @param value The Java object to encode.
     * @return The serialized representation of the value.
     * @throws CodecException If the object contains data that cannot be serialized.
     */
    <D> D encode(DynamicOps<D> ops, T value) throws CodecException;

    /**
     * An exception thrown when a codec fails to process data.
     */
    class CodecException extends Exception {
        /** @param message The detail message. */
        public CodecException(String message) { super(message); }
        /**
         * @param message The detail message.
         * @param cause The underlying cause.
         * */
        public CodecException(String message, Throwable cause) { super(message, cause); }
    }

    /**
     * Creates a simple codec from a pair of functions.
     * @param <T>     The target Java type.
     * @param decoder Function to convert raw objects to T.
     * @param encoder Function to convert T to raw objects.
     * @return A new Codec instance.
     */
    static <T> Codec<T> of(Function<Object, T> decoder, Function<T, Object> encoder) {
        return new Codec<>() {
            @Override public <D> T decode(DynamicOps<D> ops, D input) throws CodecException {
                try { return decoder.apply(input); } catch (Exception e) { throw new CodecException("Failed to decode", e); }
            }
            @Override @SuppressWarnings("unchecked")
            public <D> D encode(DynamicOps<D> ops, T value) throws CodecException {
                try { return (D) encoder.apply(value); } catch (Exception e) { throw new CodecException("Failed to encode", e); }
            }
        };
    }

    /**
     * Maps this codec to another type via two conversion functions (Invariant mapping).
     * @param <R>      The new Java type.
     * @param forward  Function to convert from T to R.
     * @param backward Function to convert from R back to T.
     * @return A codec for type R.
     */
    default <R> Codec<R> xmap(CheckedFunction<? super T, ? extends R> forward,
                              CheckedFunction<? super R, ? extends T> backward) {
        Codec<T> self = this;
        return new Codec<>() {
            @Override
            public <D> R decode(DynamicOps<D> ops, D input) throws CodecException {
                return forward.apply(self.decode(ops, input));
            }

            @Override
            public <D> D encode(DynamicOps<D> ops, R value) throws CodecException {
                return self.encode(ops, backward.apply(value));
            }
        };
    }

    /**
     * Returns a codec that returns a default value if decoding fails or input is null.
     * @param defaultValue The value to return on failure.
     * @return A fallback-capable codec.
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

    /** @return A codec for a List of type T. */
    default Codec<List<T>> list() {
        return collection(ArrayList::new);
    }

    /** * Unsafely casts this codec to another type.
     * @param <U> The type to cast to.
     * @return The casted codec.
     */
    @SuppressWarnings("unchecked")
    default <U> Codec<U> unchecked() {
        return (Codec<U>) this;
    }

    /**
     * Creates a codec for a specific collection implementation.
     * Also handles recursive unwrapping for Fallback and OneOf codecs to ensure
     * lists are handled correctly across all branches.
     * @param <C>     The collection type.
     * @param factory Supplier for the collection instance.
     * @return A collection-aware codec.
     */
    default <C extends Collection<T>> Codec<C> collection(Supplier<C> factory) {
        Codec<T> self = this;

        if (self instanceof FallbackCodec<T>(Codec<T> left, Codec<T> right)) {
            Codec<List<T>> leftList  = left.list().unchecked();
            Codec<List<T>> rightList = right.list().unchecked();
            return new FallbackCodec<>(leftList, rightList).unchecked();
        }

        if (self instanceof OneOfCodec<T>(List<Codec<T>> codecs)) {
            List<Codec<List<T>>> listCodecs = new ArrayList<>();
            for (Codec<T> c : codecs) listCodecs.add(c.list().unchecked());
            return new OneOfCodec<>(listCodecs).unchecked();
        }

        return new Codec<>() {
            @Override
            public <D> C decode(DynamicOps<D> ops, D input) throws CodecException {
                List<D> rawList = ops.getList(input)
                    .orElseThrow(() -> new CodecException("Expected list for collection"));
                C result = factory.get();
                for (D elem : rawList) {
                    result.add(self.decode(ops, elem));
                }
                return result;
            }

            @Override
            public <D> D encode(DynamicOps<D> ops, C value) throws CodecException {
                List<D> result = new ArrayList<>(value.size());
                for (T elem : value) {
                    result.add(self.encode(ops, elem));
                }
                return ops.createList(result);
            }
        };
    }

    /** @return A codec wrapping type T in an Optional. */
    default Codec<Optional<T>> optional() {
        Codec<T> self = this;
        return new Codec<>() {
            @Override
            public <D> Optional<T> decode(DynamicOps<D> ops, D input) throws CodecException {
                if (input == null || Objects.equals(input, ops.empty()))
                    return Optional.empty();
                return Optional.ofNullable(self.decode(ops, input));
            }

            @Override
            public <D> D encode(DynamicOps<D> ops, Optional<T> value) throws CodecException {
                if (value == null || value.isEmpty()) return ops.empty();
                return self.encode(ops, value.get());
            }
        };
    }

    /** @return A codec that handles null Java values by encoding/decoding as empty/null. */
    default Codec<T> nullable() {
        Codec<T> self = this;
        return new Codec<>() {
            @Override public <D> T decode(DynamicOps<D> ops, D input) throws CodecException {
                D empty = ops.empty();
                if (input == null || Objects.equals(input, empty)) return null;
                return self.decode(ops, input);
            }

            @Override public <D> D encode(DynamicOps<D> ops, T value) throws CodecException {
                return value == null ? ops.empty() : self.encode(ops, value);
            }
        };
    }

    /**
     * Creates a codec for a Map.
     * @param <K>        Key type.
     * @param <V>        Value type.
     * @param keyCodec   Codec for keys.
     * @param valueCodec Codec for values.
     * @return A map codec.
     */
    static <K, V> Codec<Map<K, V>> map(Codec<K> keyCodec, Codec<V> valueCodec) {
        return new Codec<>() {
            @Override public <D> Map<K, V> decode(DynamicOps<D> ops, D input) throws CodecException {
                Map<D, D> raw = ops.getMap(input)
                    .orElseThrow(() -> new CodecException("Expected map"));
                Map<K, V> result = new LinkedHashMap<>(raw.size());
                for (var e : raw.entrySet()) {
                    K k = keyCodec.decode(ops, e.getKey());
                    V v = valueCodec.decode(ops, e.getValue());
                    result.put(k, v);
                }
                return result;
            }

            @Override public <D> D encode(DynamicOps<D> ops, Map<K, V> value) throws CodecException {
                Map<D, D> result = new LinkedHashMap<>(value.size());
                for (var e : value.entrySet()) {
                    result.put(
                        keyCodec.encode(ops, e.getKey()),
                        valueCodec.encode(ops, e.getValue())
                    );
                }
                return ops.createMap(result);
            }
        };
    }

    /**
     * Creates a codec for an Enum using its name.
     * @param <E>       Enum type.
     * @param enumClass The Class of the enum.
     * @return An enum codec.
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
     * Combines two codecs. Tries the left one first; if it fails, tries the right.
     * @param <T>   The target type.
     * @param left  Primary codec.
     * @param right Secondary codec.
     * @return A fallback codec.
     */
    static <T> Codec<T> fallback(Codec<? extends T> left, Codec<? extends T> right) {
        return new FallbackCodec<>(
            left.unchecked(),
            right.unchecked()
        );
    }

    /**
     * Creates a codec for an {@link Either} type.
     * @param <A>   Left type.
     * @param <B>   Right type.
     * @param left  Left codec.
     * @param right Right codec.
     * @return An either codec.
     */
    static <A, B> Codec<Either<A, B>> either(Codec<A> left, Codec<B> right) {
        return new EitherCodec<>(left, right);
    }

    /**
     * Attempts multiple codecs in order until one succeeds.
     * @param <T>     The target type.
     * @param codecs  The codecs to attempt.
     * @return A one-of-many codec.
     */
    @SafeVarargs
    static <T> Codec<T> oneOf(Codec<? extends T>... codecs) {
        List<Codec<T>> list = new ArrayList<>();
        for (Codec<? extends T> c : codecs) list.add(c.unchecked());
        return new OneOfCodec<>(list);
    }

    /**
     * A function that is allowed to throw a CodecException.
     * @param <T> Input type.
     * @param <R> Return type.
     */
    @FunctionalInterface
    interface CheckedFunction<T, R> {
        /** @throws Codec.CodecException if the function logic fails. */
        R apply(T t) throws Codec.CodecException;
    }

    /**
     * Represents a single field within a larger object structure.
     * @param <T> The parent object type.
     * @param <A> The field's type.
     */
    record Field<T, A>(String name, Codec<A> codec, Function<T, A> getter) {}

    /**
     * Internal implementation of a codec that tries two branches.
     * @param <T> Target type.
     */
    record FallbackCodec<T>(Codec<T> left, Codec<T> right) implements Codec<T> {
        @Override
        public <D> T decode(DynamicOps<D> ops, D input) throws CodecException {
            try { return left.decode(ops, input); }
            catch (Exception ignored) { return right.decode(ops, input); }
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, T value) throws CodecException {
            try { return left.encode(ops, value); }
            catch (Exception ignored) { return right.encode(ops, value); }
        }
    }

    /**
     * Internal implementation for the {@link Either} type.
     * @param <A> Left type.
     * @param <B> Right type.
     */
    record EitherCodec<A, B>(Codec<A> left, Codec<B> right) implements Codec<Either<A, B>> {
        @Override
        public <D> Either<A, B> decode(DynamicOps<D> ops, D input) throws CodecException {
            try {
                return new Either.Left<>(left.decode(ops, input));
            } catch (Exception ignore) {
                return new Either.Right<>(right.decode(ops, input));
            }
        }

        @Override
        @SuppressWarnings("unchecked")
        public <D> D encode(DynamicOps<D> ops, Either<A, B> either) throws CodecException {
            if (either instanceof Either.Left<A, B> l) {
                return left.encode(ops, l.value());
            } else {
                return right.encode(ops, ((Either.Right<A, B>) either).value());
            }
        }
    }

    /**
     * Internal implementation that attempts a list of codecs sequentially.
     * @param <T> Target type.
     */
    record OneOfCodec<T>(List<Codec<T>> codecs) implements Codec<T> {
        /** @param codecs The immutable list of codecs. */
        public OneOfCodec(List<Codec<T>> codecs) {
            this.codecs = List.copyOf(codecs);
        }

        @Override
        public <D> T decode(DynamicOps<D> ops, D input) throws CodecException {
            CodecException last = null;
            for (Codec<T> c : codecs) {
                try {
                    return c.decode(ops, input);
                } catch (Exception e) {
                    last = new CodecException(e.getMessage(), e);
                }
            }
            throw last != null ? last : new CodecException("No codec in OneOf matched");
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, T value) throws CodecException {
            CodecException last = null;
            for (Codec<T> c : codecs) {
                try {
                    return c.encode(ops, value);
                } catch (Exception e) {
                    last = new CodecException(e.getMessage(), e);
                }
            }
            throw last != null ? last : new CodecException("No codec in OneOf could encode");
        }
    }

    /**
     * Creates a field definition used for building complex object codecs.
     * * @param <P>    Parent object type.
     * @param name   The field name in the serialized format.
     * @param getter Function to extract this field from the parent.
     * @return A field definition.
     */
    default <P> Field<P, T> fieldOf(String name, Function<P, T> getter) {
        return new Field<>(name, this, getter);
    }
}