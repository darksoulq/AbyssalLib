package com.github.darksoulq.abyssallib.common.serialization;

import com.github.darksoulq.abyssallib.common.util.Either;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A bidirectional serializer and deserializer capable of translating between Java objects
 * and various serialized formats defined by a {@link DynamicOps} provider.
 *
 * @param <T> The Java type that this codec is responsible for handling.
 */
public interface Codec<T> {

    /**
     * Decodes a serialized input of a specific data format into a Java object.
     *
     * @param <D>   The type of the serialized data (e.g., JsonElement, NBTCompound, ByteBuf).
     * @param ops   The provider defining how to navigate and read data of type D.
     * @param input The raw serialized input to be processed.
     * @return The resulting Java object of type T.
     * @throws CodecException If the input data is malformed, missing fields, or type-mismatched.
     */
    <D> T decode(DynamicOps<D> ops, D input);

    /**
     * Encodes a Java object into its serialized representation.
     *
     * @param <D>   The target type of the serialized data.
     * @param ops   The provider defining how to construct data of type D.
     * @param value The Java object instance to be serialized.
     * @return The serialized data representing the object.
     * @throws CodecException If the object contains non-serializable states or illegal values.
     */
    <D> D encode(DynamicOps<D> ops, T value);

    /**
     * An unchecked exception utilized for failures occurring during the serialization
     * or deserialization process.
     */
    class CodecException extends RuntimeException {
        /**
         * Constructs a new exception with a specific detail message.
         *
         * @param message The error description.
         */
        public CodecException(String message) {
            super(message);
        }

        /**
         * Constructs a new exception with a detail message and an underlying cause.
         *
         * @param message The error description.
         * @param cause   The throwable that triggered this exception.
         */
        public CodecException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * Represents the definition of a single field within a structured object.
     *
     * @param <P>    The parent object type containing the field.
     * @param <T>    The type of the field itself.
     * @param name   The key identifier for this field in the serialized format.
     * @param codec  The codec used to process the field data.
     * @param getter A function used to extract the field value from a parent object instance.
     */
    record Field<P, T>(String name, Codec<T> codec, Function<P, T> getter) {}

    /**
     * Internal implementation for handling the {@link Either} type, allowing for
     * bifurcated data structures.
     *
     * @param <A>   The type of the Left branch.
     * @param <B>   The type of the Right branch.
     * @param left  Codec for the Left branch.
     * @param right Codec for the Right branch.
     */
    record EitherCodec<A, B>(Codec<A> left, Codec<B> right) implements Codec<Either<A, B>> {
        /**
         * Decodes data into an Either container.
         *
         * @param <D>   The data format type.
         * @param ops   The operation provider.
         * @param input The input data.
         * @return An Either instance.
         */
        @Override
        public <D> Either<A, B> decode(DynamicOps<D> ops, D input) {
            try {
                return new Either.Left<>(left.decode(ops, input));
            } catch (CodecException ignore) {
                return new Either.Right<>(right.decode(ops, input));
            }
        }

        /**
         * Encodes an Either container into serialized data.
         *
         * @param <D>    The data format type.
         * @param ops    The operation provider.
         * @param either The Either container.
         * @return The serialized data.
         */
        @Override
        public <D> D encode(DynamicOps<D> ops, Either<A, B> either) {
            if (either instanceof Either.Left<A, B> l) {
                return left.encode(ops, l.value());
            } else {
                return right.encode(ops, ((Either.Right<A, B>) either).value());
            }
        }
    }

    /**
     * Internal implementation that iterates through a list of codecs until one
     * successfully processes the data.
     *
     * @param <T>    The common type returned by all provided codecs.
     * @param codecs The list of candidate codecs.
     */
    record OneOfCodec<T>(List<Codec<T>> codecs) implements Codec<T> {
        /**
         * Constructs a oneOf codec with an immutable copy of the provided list.
         *
         * @param codecs The codecs to attempt in order.
         */
        public OneOfCodec(List<Codec<T>> codecs) {
            this.codecs = List.copyOf(codecs);
        }

        /**
         * Decodes the input using the first codec in the list that succeeds.
         *
         * @param <D>   The data format type.
         * @param ops   The operation provider.
         * @param input The input data.
         * @return The decoded value.
         */
        @Override
        public <D> T decode(DynamicOps<D> ops, D input) {
            List<Throwable> errors = new ArrayList<>();
            for (Codec<T> c : codecs) {
                try {
                    return c.decode(ops, input);
                } catch (CodecException e) {
                    errors.add(e);
                }
            }
            throw new CodecException("No codec in OneOf matched. Errors: " + errors);
        }

        /**
         * Encodes the value using the first codec in the list that succeeds.
         *
         * @param <D>   The data format type.
         * @param ops   The operation provider.
         * @param value The value to encode.
         * @return The encoded data.
         */
        @Override
        public <D> D encode(DynamicOps<D> ops, T value) {
            List<Throwable> errors = new ArrayList<>();
            for (Codec<T> c : codecs) {
                try {
                    return c.encode(ops, value);
                } catch (CodecException e) {
                    errors.add(e);
                }
            }
            throw new CodecException("No codec in OneOf could encode. Errors: " + errors);
        }
    }

    /**
     * Factory method to create a simple codec from mapping functions.
     *
     * @param <T>     The target Java type.
     * @param decoder Function to convert raw objects (from DynamicOps) to type T.
     * @param encoder Function to convert type T to raw objects.
     * @return A basic Codec implementation.
     */
    static <T> Codec<T> of(Function<Object, T> decoder, Function<T, Object> encoder) {
        return new Codec<>() {
            @Override public <D> T decode(DynamicOps<D> ops, D input) {
                try { return decoder.apply(input); } catch (Exception e) { throw new CodecException("Failed to decode", e); }
            }
            @Override @SuppressWarnings("unchecked")
            public <D> D encode(DynamicOps<D> ops, T value) {
                try { return (D) encoder.apply(value); } catch (Exception e) { throw new CodecException("Failed to encode", e); }
            }
        };
    }

    /**
     * Creates a codec for a Map structure.
     *
     * @param <K>        Key type.
     * @param <V>        Value type.
     * @param keyCodec   Codec for keys.
     * @param valueCodec Codec for values.
     * @return A codec handling Map types.
     */
    static <K, V> Codec<Map<K, V>> map(Codec<K> keyCodec, Codec<V> valueCodec) {
        return new Codec<>() {
            @Override public <D> Map<K, V> decode(DynamicOps<D> ops, D input) {
                Map<D, D> raw = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
                Map<K, V> result = new LinkedHashMap<>(raw.size());
                for (var e : raw.entrySet()) {
                    result.put(keyCodec.decode(ops, e.getKey()), valueCodec.decode(ops, e.getValue()));
                }
                return result;
            }

            @Override public <D> D encode(DynamicOps<D> ops, Map<K, V> value) {
                Map<D, D> result = new LinkedHashMap<>(value.size());
                for (var e : value.entrySet()) {
                    result.put(keyCodec.encode(ops, e.getKey()), valueCodec.encode(ops, e.getValue()));
                }
                return ops.createMap(result);
            }
        };
    }

    /**
     * Creates a codec for an Enum type using its string name.
     *
     * @param <E>       Enum type.
     * @param enumClass The class literal of the enum.
     * @return A codec for the specified enum.
     */
    static <E extends Enum<E>> Codec<E> enumCodec(Class<E> enumClass) {
        return new Codec<>() {
            @Override public <D> E decode(DynamicOps<D> ops, D input) {
                String name = ops.getStringValue(input).orElseThrow(() -> new CodecException("Expected enum string"));
                try { return Enum.valueOf(enumClass, name); } catch (IllegalArgumentException e) { throw new CodecException("Invalid enum value: " + name); }
            }
            @Override public <D> D encode(DynamicOps<D> ops, E value) { return ops.createString(value.name()); }
        };
    }

    /**
     * Combines two codecs such that the secondary is used if the primary fails.
     * Internally implemented as a OneOfCodec wrapper.
     *
     * @param <T>   Target type.
     * @param left  Primary codec.
     * @param right Fallback codec.
     * @return A fallback-enabled codec.
     */
    static <T> Codec<T> fallback(Codec<? extends T> left, Codec<? extends T> right) {
        return oneOf(left, right);
    }

    /**
     * Creates a codec for an {@link Either} container.
     *
     * @param <A>   Left branch type.
     * @param <B>   Right branch type.
     * @param left  Codec for type A.
     * @param right Codec for type B.
     * @return A codec for Either.
     */
    static <A, B> Codec<Either<A, B>> either(Codec<A> left, Codec<B> right) {
        return new EitherCodec<>(left, right);
    }

    /**
     * Combines multiple codecs to be tried in sequence.
     *
     * @param <T>    Target type.
     * @param codecs Array of codecs to attempt.
     * @return A multi-branch codec.
     */
    @SafeVarargs
    static <T> Codec<T> oneOf(Codec<? extends T>... codecs) {
        List<Codec<T>> list = new ArrayList<>();
        for (Codec<? extends T> c : codecs) list.add(c.unchecked());
        return new OneOfCodec<>(list);
    }

    /**
     * Transforms this codec to handle a new type R via two-way conversion functions.
     *
     * @param <R>      The new target Java type.
     * @param forward  Conversion function from T to R.
     * @param backward Conversion function from R to T.
     * @return A codec for type R.
     */
    default <R> Codec<R> xmap(Function<? super T, ? extends R> forward, Function<? super R, ? extends T> backward) {
        Codec<T> self = this;
        return new Codec<>() {
            @Override
            public <D> R decode(DynamicOps<D> ops, D input) {
                return forward.apply(self.decode(ops, input));
            }

            @Override
            public <D> D encode(DynamicOps<D> ops, R value) {
                return self.encode(ops, backward.apply(value));
            }
        };
    }

    /**
     * Wraps this codec with a default value to be used if decoding fails or returns null.
     *
     * @param defaultValue The value to return on failure.
     * @return A codec that provides a default result.
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
            @Override public <D> D encode(DynamicOps<D> ops, T value) {
                return self.encode(ops, value != null ? value : defaultValue);
            }
        };
    }

    /**
     * Creates a codec for a {@link List} of elements of type T.
     *
     * @return A codec for a List of T.
     */
    default Codec<List<T>> list() {
        return collection(ArrayList::new);
    }

    /**
     * Performs an unsafe cast of this codec to another type.
     *
     * @param <U> Target type.
     * @return The same codec instance cast to Codec of U.
     */
    @SuppressWarnings("unchecked")
    default <U> Codec<U> unchecked() {
        return (Codec<U>) this;
    }

    /**
     * Creates a codec for a specific collection implementation.
     * Handles complex logic to propagate list serialization through OneOf codecs.
     *
     * @param <C>     The specific collection type (e.g., Set, Queue).
     * @param factory Supplier to create a new collection instance.
     * @return A collection-specific codec.
     */
    default <C extends Collection<T>> Codec<C> collection(Supplier<C> factory) {
        Codec<T> self = this;

        if (self instanceof OneOfCodec<T> oneOf) {
            List<Codec<List<T>>> listCodecs = new ArrayList<>();
            for (Codec<T> c : oneOf.codecs()) listCodecs.add(c.list().unchecked());
            return Codec.oneOf(listCodecs.toArray(new Codec[0])).unchecked();
        }

        return new Codec<>() {
            @Override
            public <D> C decode(DynamicOps<D> ops, D input) {
                List<D> rawList = ops.getList(input)
                    .orElseThrow(() -> new CodecException("Expected list for collection"));
                C result = factory.get();
                for (D elem : rawList) {
                    result.add(self.decode(ops, elem));
                }
                return result;
            }

            @Override
            public <D> D encode(DynamicOps<D> ops, C value) {
                List<D> result = new ArrayList<>(value.size());
                for (T elem : value) {
                    result.add(self.encode(ops, elem));
                }
                return ops.createList(result);
            }
        };
    }

    /**
     * Wraps the current codec to handle Java {@link Optional} values.
     *
     * @return A codec for Optional of T.
     */
    default Codec<Optional<T>> optional() {
        Codec<T> self = this;
        return new Codec<>() {
            @Override
            public <D> Optional<T> decode(DynamicOps<D> ops, D input) {
                if (input == null || Objects.equals(input, ops.empty()))
                    return Optional.empty();
                return Optional.ofNullable(self.decode(ops, input));
            }

            @Override
            public <D> D encode(DynamicOps<D> ops, Optional<T> value) {
                if (value == null || value.isEmpty()) return ops.empty();
                return self.encode(ops, value.get());
            }
        };
    }

    /**
     * Modifies the codec to treat null Java values as empty serialized states.
     *
     * @return A null-safe version of this codec.
     */
    default Codec<T> nullable() {
        Codec<T> self = this;
        return new Codec<>() {
            @Override public <D> T decode(DynamicOps<D> ops, D input) {
                D empty = ops.empty();
                if (input == null || Objects.equals(input, empty)) return null;
                return self.decode(ops, input);
            }

            @Override public <D> D encode(DynamicOps<D> ops, T value) {
                return value == null ? ops.empty() : self.encode(ops, value);
            }
        };
    }

    /**
     * Defines this codec as a field within a larger object structure.
     *
     * @param <P>    The parent object type.
     * @param name   The name of the field.
     * @param getter The function to get the field value from the parent.
     * @return A Field definition object.
     */
    default <P> Field<P, T> fieldOf(String name, Function<P, T> getter) {
        return new Field<>(name, this, getter);
    }
}