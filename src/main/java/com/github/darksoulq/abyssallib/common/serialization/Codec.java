package com.github.darksoulq.abyssallib.common.serialization;

import com.github.darksoulq.abyssallib.common.serialization.codecs.*;
import com.github.darksoulq.abyssallib.common.serialization.fixer.DataFixerRegistry;
import com.github.darksoulq.abyssallib.common.serialization.schema.CodecVisitor;
import com.github.darksoulq.abyssallib.common.serialization.schema.SchemaGenerator;
import com.github.darksoulq.abyssallib.common.serialization.schema.SchemaNode;
import com.github.darksoulq.abyssallib.common.serialization.schema.SchemaValidator;
import com.github.darksoulq.abyssallib.common.util.Either;
import net.kyori.adventure.key.Key;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
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
     * @param <D>   The type of the serialized data.
     * @param ops   The provider defining how to navigate and read data of type D.
     * @param input The raw serialized input to be processed.
     * @return A DataResult containing the decoded object or an error state.
     */
    <D> DataResult<T> decode(DynamicOps<D> ops, D input);

    /**
     * Encodes a Java object into its serialized representation.
     *
     * @param <D>   The target type of the serialized data.
     * @param ops   The provider defining how to construct data of type D.
     * @param value The Java object instance to be serialized.
     * @return A DataResult containing the serialized data or an error state.
     */
    <D> DataResult<D> encode(DynamicOps<D> ops, T value);

    /**
     * Routes this instance into the visitor API to construct contextual schema layouts.
     *
     * @param visitor The evaluation logic instance.
     * @param <R>     The layout target natively returning explicitly mapped variants.
     * @return Evaluated variant functionally extracting outputs.
     */
    default <R> R accept(CodecVisitor<R> visitor) {
        return visitor.visitUnknown(describe());
    }

    /**
     * Constructs a hierarchical structured object graph defining serialization limits explicitly gracefully completely safely resolving configurations.
     *
     * @return Evaluated schema tree structurally identical properly parsing limits safely.
     */
    default SchemaNode schema() {
        return accept(new SchemaGenerator());
    }

    /**
     * Asynchronously decodes serialized data without blocking the primary thread execution context.
     *
     * @param <D>   The type of the serialized data format.
     * @param ops   The provider defining operations layout boundaries.
     * @param input The target structured format representing object mapping targets.
     * @return A CompletableFuture housing the localized data result.
     */
    default <D> CompletableFuture<DataResult<T>> decodeAsync(DynamicOps<D> ops, D input) {
        return CompletableFuture.supplyAsync(() -> decode(ops, input));
    }

    /**
     * Asynchronously encodes runtime objects into mapped format bounds.
     *
     * @param <D>   The target format mapping representation type.
     * @param ops   The operational binding interface interpreting physical output rules.
     * @param value The physical target structure mapping variables.
     * @return A CompletableFuture housing the target data layout.
     */
    default <D> CompletableFuture<DataResult<D>> encodeAsync(DynamicOps<D> ops, T value) {
        return CompletableFuture.supplyAsync(() -> encode(ops, value));
    }

    /**
     * Outputs a structural description of the expected format for this codec.
     * Useful for schema generation and validation debugging.
     *
     * @return The schema definition representing the target type.
     */
    default String describe() {
        return "Unknown";
    }

    /**
     * Attaches a custom description overriding the default schema output.
     *
     * @param description The new schema description.
     * @return A structurally identical Codec containing the customized description.
     */
    default Codec<T> describe(String description) {
        Codec<T> self = this;
        return new Codec<>() {
            @Override public <D> DataResult<T> decode(DynamicOps<D> ops, D input) { return self.decode(ops, input); }
            @Override public <D> DataResult<D> encode(DynamicOps<D> ops, T value) { return self.encode(ops, value); }
            @Override public String describe() { return description; }
            @Override public <R> R accept(CodecVisitor<R> visitor) { return self.accept(visitor); }
        };
    }

    /**
     * Computes structural equality between two decoded values by encoding and comparing their serialized variants.
     * Useful when the target object lacks a proper {@link Object#equals(Object)} definition.
     *
     * @param <D> The dynamic operations context.
     * @param ops The target serialization provider.
     * @param a   The primary value to compare.
     * @param b   The secondary value to compare against.
     * @return True if both values yield the same serialized form.
     */
    default <D> boolean structuralEquals(DynamicOps<D> ops, T a, T b) {
        DataResult<D> resA = encode(ops, a);
        DataResult<D> resB = encode(ops, b);
        if (resA.isError() || resB.isError()) return false;
        return Objects.equals(resA.getOrThrow(), resB.getOrThrow());
    }

    /**
     * Initiates the definition of a required field for a RecordBuilder.
     *
     * @param name The key identifier for this field in the serialized format.
     * @return A builder to bind the field to a getter function.
     */
    default FieldBuilder<T> fieldOf(String name) {
        return new FieldBuilder<>(name, this, false, null);
    }

    /**
     * Initiates the definition of an optional field for a RecordBuilder.
     * If the field is missing during decoding, the codec resolves to Optional.empty().
     *
     * @param name The key identifier for this field in the serialized format.
     * @return A builder to bind the field to a getter function.
     */
    default FieldBuilder<Optional<T>> optionalFieldOf(String name) {
        return new FieldBuilder<>(name, this.optional(), true, null);
    }

    /**
     * Initiates the definition of an optional field with a default fallback value.
     *
     * @param name         The key identifier for this field in the serialized format.
     * @param defaultValue The value to yield if the key is missing.
     * @return A builder to bind the field to a getter function.
     */
    default FieldBuilder<T> optionalFieldOf(String name, T defaultValue) {
        return new FieldBuilder<>(name, this.orElse(defaultValue), true, defaultValue);
    }

    /**
     * Translates mapping layouts matching structurally explicit tagged unions.
     * Utilized identically to dispatch formats but enforces static identifier locations targeting enum discriminators.
     *
     * @param <K>          Discriminating enum marker instance mapping variants.
     * @param <V>          Supertype encompassing targeted variants.
     * @param typeKey      The node storing variant context variables.
     * @param keyCodec     The codec mapping marker key variants.
     * @param typeGetter   The lambda identifying the variant enum context node attached to the layout.
     * @param codecGetter  Function defining codec mappings linked directly to matched enum discriminators.
     * @return A Codec mapping statically structured variant hierarchies correctly natively executing tag identification.
     */
    static <K extends Enum<K>, V> Codec<V> taggedUnion(String typeKey, Codec<K> keyCodec, Function<? super V, ? extends K> typeGetter, Function<? super K, ? extends Codec<? extends V>> codecGetter) {
        Codec<V> base = dispatch(typeKey, keyCodec, typeGetter, codecGetter);
        return new Codec<>() {
            @Override public <D> DataResult<V> decode(DynamicOps<D> ops, D input) { return base.decode(ops, input); }
            @Override public <D> DataResult<D> encode(DynamicOps<D> ops, V value) { return base.encode(ops, value); }
            @Override public String describe() { return "TaggedUnion[" + typeKey + "]"; }
            @Override public <R> R accept(CodecVisitor<R> visitor) { return visitor.visitDispatch(typeKey); }
        };
    }

    /**
     * Translates mapping layouts matching structurally explicit tagged unions, explicitly anchoring the type.
     *
     * @param type         The class literal representing the base type hierarchy boundary.
     * @param <K>          Discriminating enum marker instance mapping variants.
     * @param <V>          Supertype encompassing targeted variants.
     * @param typeKey      The node storing variant context variables.
     * @param keyCodec     The codec mapping marker key variants.
     * @param typeGetter   The lambda identifying the variant enum context node attached to the layout.
     * @param codecGetter  Function defining codec mappings linked directly to matched enum discriminators.
     * @return A Codec mapping statically structured variant hierarchies correctly natively executing tag identification.
     */
    static <K extends Enum<K>, V> Codec<V> taggedUnion(Class<V> type, String typeKey, Codec<K> keyCodec, Function<? super V, ? extends K> typeGetter, Function<? super K, ? extends Codec<? extends V>> codecGetter) {
        return taggedUnion(typeKey, keyCodec, typeGetter, codecGetter);
    }

    /**
     * Internal definition for an immutable pair of values.
     *
     * @param <A> The type of the first element.
     * @param <B> The type of the second element.
     */
    record Pair<A, B>(A first, B second) {}

    /**
     * Internal definition for an immutable sequence of three values.
     *
     * @param <A> Type of the first element.
     * @param <B> Type of the second element.
     * @param <C> Type of the third element.
     */
    record Tuple3<A, B, C>(A first, B second, C third) {}

    /**
     * Internal definition for an immutable sequence of four values.
     *
     * @param <A> Type of the first element.
     * @param <B> Type of the second element.
     * @param <C> Type of the third element.
     * @param <D> Type of the fourth element.
     */
    record Tuple4<A, B, C, D>(A first, B second, C third, D fourth) {}

    /**
     * Creates a codec capable of handling a two-element ordered sequence.
     *
     * @param <A>    The type of the first value.
     * @param <B>    The type of the second value.
     * @param first  Codec for the first branch.
     * @param second Codec for the second branch.
     * @return A codec for a paired structure.
     */
    static <A, B> Codec<Pair<A, B>> pair(Codec<A> first, Codec<B> second) {
        return new TupleCodecs.PairCodec<>(first, second);
    }

    /**
     * Creates a codec handling a three-element ordered sequence.
     *
     * @param <A>    The type of the first value.
     * @param <B>    The type of the second value.
     * @param <C>    The type of the third value.
     * @param first  Codec for the first branch.
     * @param second Codec for the second branch.
     * @param third  Codec for the third branch.
     * @return A codec for a Tuple3 structure.
     */
    static <A, B, C> Codec<Tuple3<A, B, C>> tuple(Codec<A> first, Codec<B> second, Codec<C> third) {
        return new TupleCodecs.Tuple3Codec<>(first, second, third);
    }

    /**
     * Creates a codec handling a four-element ordered sequence.
     *
     * @param <A>    The type of the first value.
     * @param <B>    The type of the second value.
     * @param <C>    The type of the third value.
     * @param <D_TYPE>    The type of the fourth value.
     * @param first  Codec for the first branch.
     * @param second Codec for the second branch.
     * @param third  Codec for the third branch.
     * @param fourth Codec for the fourth branch.
     * @return A codec for a Tuple4 structure.
     */
    static <A, B, C, D_TYPE> Codec<Tuple4<A, B, C, D_TYPE>> tuple(Codec<A> first, Codec<B> second, Codec<C> third, Codec<D_TYPE> fourth) {
        return new TupleCodecs.Tuple4Codec<>(first, second, third, fourth);
    }

    /**
     * Creates a codec that reliably yields the same predefined value during decoding,
     * and maps to an empty representation during encoding.
     *
     * @param <T>   The type of the value.
     * @param value The supplier yielding the constant value.
     * @return A unit codec.
     */
    static <T> Codec<T> unit(Supplier<T> value) {
        return new UnitCodec<>(value);
    }

    /**
     * Creates a codec that reliably yields the same predefined value during decoding.
     *
     * @param <T>   The type of the value.
     * @param value The constant value.
     * @return A unit codec.
     */
    static <T> Codec<T> unit(T value) {
        return unit(() -> value);
    }

    /**
     * Isolates operations onto a specific query path directly inside the current object structure.
     *
     * @param <T>   The target type matching the child node.
     * @param codec The codec responsible for interpreting the resolved query data.
     * @param path  The literal target nested segment.
     * @return A constrained reading codec targeting an explicit structure.
     */
    static <T> Codec<T> query(Codec<T> codec, String path) {
        return new QueryCodec<>(codec, path);
    }

    /**
     * Evaluates a structural condition on a target object to select an underlying codec sequence.
     * Allows dependent serialization behaviors that hinge on preceding state context.
     *
     * @param <T>        Target resolution type.
     * @param condition  The predicate deciding execution flow.
     * @param trueCodec  Executing codec when condition evaluates positively.
     * @param falseCodec Executing codec when condition evaluates negatively.
     * @return A context-sensitive conditional executing codec.
     */
    static <T> Codec<T> conditional(Predicate<T> condition, Codec<T> trueCodec, Codec<T> falseCodec) {
        return new ConditionalCodec<>(condition, trueCodec, falseCodec);
    }

    /**
     * Functional interface defining the decoding logic of a codec.
     *
     * @param <T> The target Java type.
     */
    interface Decoder<T> {
        /**
         * Decodes the input data.
         *
         * @param <D>   The serialized data type.
         * @param ops   The dynamic operations instance.
         * @param input The raw serialized input.
         * @return The decoded result.
         */
        <D> DataResult<T> decode(DynamicOps<D> ops, D input);
    }

    /**
     * Functional interface defining the encoding logic of a codec.
     *
     * @param <T> The target Java type.
     */
    interface Encoder<T> {
        /**
         * Encodes the Java object.
         *
         * @param <D>   The serialized data type.
         * @param ops   The dynamic operations instance.
         * @param value The value to encode.
         * @return The encoded result.
         */
        <D> DataResult<D> encode(DynamicOps<D> ops, T value);
    }

    /**
     * Creates a codec that immediately returns an error state upon any encoding or decoding operation.
     *
     * @param <T>     The target Java type.
     * @param message The specific error reason.
     * @return A failing Codec implementation.
     */
    static <T> Codec<T> error(String message) {
        return new Codec<>() {
            @Override public <D> DataResult<T> decode(DynamicOps<D> ops, D input) { return DataResult.error(message); }
            @Override public <D> DataResult<D> encode(DynamicOps<D> ops, T value) { return DataResult.error(message); }
            @Override public String describe() { return "Error[" + message + "]"; }
            @Override public <R> R accept(CodecVisitor<R> visitor) { return visitor.visitPrimitive("Error"); }
        };
    }

    /**
     * Factory method to create a codec using explicit context-aware decoders and encoders.
     *
     * @param <T>     The target Java type.
     * @param decoder Function to read data utilizing the provided DynamicOps.
     * @param encoder Function to write data utilizing the provided DynamicOps.
     * @return A custom Codec implementation.
     */
    static <T> Codec<T> of(Decoder<T> decoder, Encoder<T> encoder) {
        return new Codec<>() {
            @Override
            public <D> DataResult<T> decode(DynamicOps<D> ops, D input) {
                return decoder.decode(ops, input);
            }

            @Override
            public <D> DataResult<D> encode(DynamicOps<D> ops, T value) {
                return encoder.encode(ops, value);
            }

            @Override
            public <R> R accept(CodecVisitor<R> visitor) {
                return visitor.visitPrimitive("Object");
            }
        };
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
            @Override public <D> DataResult<T> decode(DynamicOps<D> ops, D input) {
                try {
                    return DataResult.success(decoder.apply(input));
                } catch (Exception e) {
                    return DataResult.error(DataError.custom("Failed to decode: " + e.getMessage()));
                }
            }
            @Override @SuppressWarnings("unchecked")
            public <D> DataResult<D> encode(DynamicOps<D> ops, T value) {
                try {
                    return DataResult.success((D) encoder.apply(value));
                } catch (Exception e) {
                    return DataResult.error(DataError.custom("Failed to encode: " + e.getMessage()));
                }
            }

            @Override
            public <R> R accept(CodecVisitor<R> visitor) {
                return visitor.visitPrimitive("Object");
            }
        };
    }

    /**
     * Creates a mutable map codec structure mapping identical type variants.
     *
     * @param <K>        Key type.
     * @param <V>        Value type.
     * @param keyCodec   Codec for keys.
     * @param valueCodec Codec for values.
     * @return A codec handling map derivations natively.
     */
    static <K, V> Codec<Map<K, V>> map(Codec<K> keyCodec, Codec<V> valueCodec) {
        return new MapCodec<>(keyCodec, valueCodec, LinkedHashMap::new, Function.identity());
    }

    /**
     * Creates an immutable map codec structure mapping rigid configuration values safely.
     *
     * @param <K>        Key type.
     * @param <V>        Value type.
     * @param keyCodec   Codec for keys.
     * @param valueCodec Codec for values.
     * @return An immutable codec handling map derivations natively.
     */
    static <K, V> Codec<Map<K, V>> immutableMap(Codec<K> keyCodec, Codec<V> valueCodec) {
        return new MapCodec<>(keyCodec, valueCodec, LinkedHashMap::new, Map::copyOf);
    }

    /**
     * Creates a codec for an Enum type using its string name.
     *
     * @param <E>       Enum type.
     * @param enumClass The class literal of the enum.
     * @return A codec for the specified enum.
     */
    static <E extends Enum<E>> Codec<E> enumCodec(Class<E> enumClass) {
        return new EnumCodec<>(enumClass);
    }

    /**
     * Combines two codecs such that the secondary is used if the primary fails.
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
     * Combines multiple codecs to be tried in sequence, explicitly anchoring the type.
     *
     * @param type   The class literal representing the target type.
     * @param <T>    Target type.
     * @param codecs Array of codecs to attempt.
     * @return A multi-branch codec.
     */
    @SafeVarargs
    static <T> Codec<T> oneOf(Class<T> type, Codec<? extends T>... codecs) {
        return oneOf(codecs);
    }

    /**
     * Constructs a codec capable of resolving cyclic dependencies correctly.
     *
     * @param <T>     The target Java type.
     * @param builder A function yielding the codec upon execution.
     * @return A proper recursive Codec utilizing lazy evaluation.
     */
    static <T> Codec<T> recursive(Function<Codec<T>, Codec<T>> builder) {
        return new RecursiveCodec<>(builder);
    }

    /**
     * Constructs a codec capable of resolving cyclic dependencies correctly, explicitly anchoring the type.
     *
     * @param type    The class literal representing the target type.
     * @param <T>     The target Java type.
     * @param builder A function yielding the codec upon execution.
     * @return A proper recursive Codec utilizing lazy evaluation explicitly bound.
     */
    static <T> Codec<T> recursive(Class<T> type, Function<Codec<T>, Codec<T>> builder) {
        return new RecursiveCodec<>(builder);
    }

    /**
     * Generates a polymorphic codec mapping diverse implementations via an explicit identifier field.
     *
     * @param <K>          The type of the key used to distinguish implementations.
     * @param <V>          The base type shared by all implementations.
     * @param typeKey      The structural field name representing the target implementation identifier.
     * @param keyCodec     The codec governing the identifier key.
     * @param typeGetter   Function isolating the key from a functional instance.
     * @param codecGetter  Function yielding the specific target codec associated with the key.
     * @return A functional dispatch codec dynamically managing implementation variants.
     */
    static <K, V> Codec<V> dispatch(String typeKey, Codec<K> keyCodec, Function<? super V, ? extends K> typeGetter, Function<? super K, ? extends Codec<? extends V>> codecGetter) {
        return new DispatchCodec<>(typeKey, keyCodec, typeGetter, codecGetter);
    }

    /**
     * Generates a polymorphic codec mapping diverse implementations, explicitly anchoring the base type context.
     *
     * @param type         The class literal representing the base type hierarchy boundary.
     * @param <K>          The type of the key used to distinguish implementations.
     * @param <V>          The base type shared by all implementations.
     * @param typeKey      The structural field name representing the target implementation identifier.
     * @param keyCodec     The codec governing the identifier key.
     * @param typeGetter   Function isolating the key from a functional instance.
     * @param codecGetter  Function yielding the specific target codec associated with the key.
     * @return A functional dispatch codec dynamically managing implementation variants explicitly bound.
     */
    static <K, V> Codec<V> dispatch(Class<V> type, String typeKey, Codec<K> keyCodec, Function<? super V, ? extends K> typeGetter, Function<? super K, ? extends Codec<? extends V>> codecGetter) {
        return new DispatchCodec<>(typeKey, keyCodec, typeGetter, codecGetter);
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
            public <D> DataResult<R> decode(DynamicOps<D> ops, D input) {
                return self.decode(ops, input).map(forward);
            }

            @Override
            public <D> DataResult<D> encode(DynamicOps<D> ops, R value) {
                try {
                    return self.encode(ops, backward.apply(value));
                } catch (Exception e) {
                    return DataResult.error(DataError.custom("xmap encoding mapping failed: " + e.getMessage()));
                }
            }

            @Override
            public String describe() {
                return self.describe();
            }

            @Override
            public <V> V accept(CodecVisitor<V> visitor) {
                return self.accept(visitor);
            }
        };
    }

    /**
     * Transforms this codec to handle a new type R, where both decoding and encoding can fail.
     *
     * @param <R>  The new target Java type.
     * @param to   Conversion function from T to DataResult R.
     * @param from Conversion function from R to DataResult T.
     * @return A flat-mapped codec returning contextual results.
     */
    default <R> Codec<R> flatXmap(Function<? super T, ? extends DataResult<? extends R>> to, Function<? super R, ? extends DataResult<? extends T>> from) {
        Codec<T> self = this;
        return new Codec<>() {
            @Override
            public <D> DataResult<R> decode(DynamicOps<D> ops, D input) {
                return self.decode(ops, input).flatMap(to);
            }

            @SuppressWarnings("unchecked")
            @Override
            public <D> DataResult<D> encode(DynamicOps<D> ops, R value) {
                return ((DataResult<T>) from.apply(value)).flatMap(t -> self.encode(ops, t));
            }

            @Override
            public String describe() {
                return self.describe();
            }

            @Override
            public <V> V accept(CodecVisitor<V> visitor) {
                return self.accept(visitor);
            }
        };
    }

    /**
     * Transforms this codec to handle a new type R, where decoding can fail but encoding cannot.
     *
     * @param <R>  The new target Java type.
     * @param to   Conversion function from T to DataResult R.
     * @param from Conversion function from R to T.
     * @return A codec for type R validated during decoding.
     */
    default <R> Codec<R> comapFlatMap(Function<? super T, ? extends DataResult<? extends R>> to, Function<? super R, ? extends T> from) {
        Codec<T> self = this;
        return new Codec<>() {
            @Override
            public <D> DataResult<R> decode(DynamicOps<D> ops, D input) {
                return self.decode(ops, input).flatMap(to);
            }

            @Override
            public <D> DataResult<D> encode(DynamicOps<D> ops, R value) {
                try {
                    return self.encode(ops, from.apply(value));
                } catch (Exception e) {
                    return DataResult.error(DataError.custom("comapFlatMap encoding mapping failed: " + e.getMessage()));
                }
            }

            @Override
            public String describe() {
                return self.describe();
            }

            @Override
            public <V> V accept(CodecVisitor<V> visitor) {
                return self.accept(visitor);
            }
        };
    }

    /**
     * Transforms this codec to handle a new type R, where decoding cannot fail but encoding can.
     *
     * @param <R>  The new target Java type.
     * @param to   Conversion function from T to R.
     * @param from Conversion function from R to DataResult T.
     * @return A codec for type R validated during encoding.
     */
    default <R> Codec<R> flatComapMap(Function<? super T, ? extends R> to, Function<? super R, ? extends DataResult<? extends T>> from) {
        Codec<T> self = this;
        return new Codec<>() {
            @Override
            public <D> DataResult<R> decode(DynamicOps<D> ops, D input) {
                return self.decode(ops, input).map(to);
            }

            @SuppressWarnings("unchecked")
            @Override
            public <D> DataResult<D> encode(DynamicOps<D> ops, R value) {
                return ((DataResult<T>) from.apply(value)).flatMap(t -> self.encode(ops, t));
            }

            @Override
            public String describe() {
                return self.describe();
            }

            @Override
            public <V> V accept(CodecVisitor<V> visitor) {
                return self.accept(visitor);
            }
        };
    }

    /**
     * Simplifies mapping the value via an identical forward and backward translation.
     *
     * @param mapper The mapping function ensuring type isolation.
     * @return A transformed codec reflecting isomorphic states.
     */
    default Codec<T> transform(Function<T, T> mapper) {
        return xmap(mapper, mapper);
    }

    /**
     * Validates decoded and encoded objects structurally via explicit schema construction limits.
     *
     * @param configurator The consumer formulating schema boundaries targeting implicit paths.
     * @return A validating codec evaluating against map hierarchies directly.
     */
    default Codec<T> restrict(Consumer<SchemaValidator> configurator) {
        Codec<T> self = this;
        return new Codec<>() {
            @Override
            public <D> DataResult<T> decode(DynamicOps<D> ops, D input) {
                DataResult<D> schemaRes = applySchema(ops, input);
                if (schemaRes.isError()) return DataResult.<T>error(schemaRes.dataError().get()).prependPath(schemaRes.error().map(s -> s.split(":")[0].trim()).orElse(""));
                return self.decode(ops, input);
            }

            @Override
            public <D> DataResult<D> encode(DynamicOps<D> ops, T value) {
                DataResult<D> encodedRes = self.encode(ops, value);
                if (encodedRes.isError()) return encodedRes;
                DataResult<D> schemaRes = applySchema(ops, encodedRes.getOrThrow());
                if (schemaRes.isError()) return DataResult.<D>error(schemaRes.dataError().get()).prependPath(schemaRes.error().map(s -> s.split(":")[0].trim()).orElse(""));
                return encodedRes;
            }

            private <D> DataResult<D> applySchema(DynamicOps<D> ops, D input) {
                SchemaValidator validator = new SchemaValidator();
                configurator.accept(validator);
                for (SchemaValidator.Rule rule : validator.getRules()) {
                    DataResult<D> r = rule.check(ops, input);
                    if (r.isError()) return r;
                }
                return DataResult.success(input);
            }

            @Override
            public String describe() {
                return self.describe() + "[schema_validated]";
            }

            @Override
            public <R> R accept(CodecVisitor<R> visitor) {
                return self.accept(visitor);
            }
        };
    }

    /**
     * Validates decoded and encoded objects using a singular validation block.
     *
     * @param validator The validation logic yielding a DataResult.
     * @return A validated codec preventing illegal structural states.
     */
    default Codec<T> validate(Function<? super T, ? extends DataResult<T>> validator) {
        Codec<T> self = this;
        return new Codec<>() {
            @Override
            public <D> DataResult<T> decode(DynamicOps<D> ops, D input) {
                return self.decode(ops, input).flatMap(validator);
            }

            @SuppressWarnings("unchecked")
            @Override
            public <D> DataResult<D> encode(DynamicOps<D> ops, T value) {
                return ((DataResult<T>) validator.apply(value)).flatMap(v -> self.encode(ops, v));
            }

            @Override
            public String describe() {
                return self.describe() + "[validated]";
            }

            @Override
            public <R> R accept(CodecVisitor<R> visitor) {
                return self.accept(visitor);
            }
        };
    }

    /**
     * Conditionally validates inputs ensuring they fulfill programmatic rules.
     *
     * @param condition    The required constraint.
     * @param errorMessage The contextual reason for constraint failures.
     * @return A validating codec enforcing behavioral restrictions.
     */
    default Codec<T> validate(Predicate<T> condition, Function<T, String> errorMessage) {
        return validate(value -> condition.test(value) ? DataResult.success(value) : DataResult.error(DataError.custom(errorMessage.apply(value))));
    }

    /**
     * Shorthand restricting a string or collection codec to a minimum elemental length.
     *
     * @param length The lower limit element bounds.
     * @return A restricted schema variant.
     */
    default Codec<T> minLength(int length) {
        return restrict(c -> c.field("").minLength(length));
    }

    /**
     * Shorthand restricting a string or collection codec to a maximum elemental length.
     *
     * @param length The upper limit element bounds.
     * @return A restricted schema variant.
     */
    default Codec<T> maxLength(int length) {
        return restrict(c -> c.field("").maxLength(length));
    }

    /**
     * Shorthand checking string codecs against arbitrary explicit formatting configurations.
     *
     * @param pattern The required mapping sequence matching strings precisely.
     * @return A restricted schema variant.
     */
    default Codec<T> regex(String pattern) {
        return restrict(c -> c.field("").regex(pattern));
    }

    /**
     * Shorthand trapping generic numerical values inside deterministic operational ranges structurally.
     *
     * @param min The explicit minimum value configuration limit.
     * @param max The explicit maximum value configuration limit.
     * @return A restricted schema variant.
     */
    default Codec<T> range(double min, double max) {
        return restrict(c -> c.field("").range(min, max));
    }

    /**
     * Shorthand requiring numerical mapping bounds to be universally structured above zero.
     *
     * @return A restricted schema variant.
     */
    default Codec<T> positive() {
        return restrict(c -> c.field("").positive());
    }

    /**
     * Shorthand confining valid interpretation explicitly to arbitrary hardcoded structural objects.
     *
     * @param values The deterministic array defining possible resolutions.
     * @return A restricted schema variant.
     */
    @SuppressWarnings("unchecked")
    default Codec<T> oneOf(T... values) {
        return restrict(c -> c.field("").oneOf((Object[]) values));
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
            @Override public <D> DataResult<T> decode(DynamicOps<D> ops, D input) {
                if (input == null || input.equals(ops.empty())) {
                    return DataResult.success(defaultValue);
                }
                DataResult<T> res = self.decode(ops, input);
                return res.isSuccess() ? res : DataResult.success(defaultValue);
            }
            @Override public <D> DataResult<D> encode(DynamicOps<D> ops, T value) {
                return self.encode(ops, value != null ? value : defaultValue);
            }
            @Override public String describe() {
                return self.describe() + "[defaulted]";
            }
            @Override public <R> R accept(CodecVisitor<R> visitor) {
                return self.accept(visitor);
            }
        };
    }

    /**
     * Creates a codec for a mutable {@link List} of elements of type T.
     *
     * @return A codec for a List of T.
     */
    default Codec<List<T>> list() {
        return new CollectionCodec<>(ArrayList::new, this, Function.identity());
    }

    /**
     * Creates a codec for an immutable {@link List} of elements of type T.
     *
     * @return An immutable List codec.
     */
    default Codec<List<T>> immutableList() {
        return new CollectionCodec<>(ArrayList::new, this, List::copyOf);
    }

    /**
     * Creates a codec for a mutable {@link Set} of elements of type T.
     *
     * @return A codec for a Set of T.
     */
    default Codec<Set<T>> set() {
        return new CollectionCodec<>(HashSet::new, this, Function.identity());
    }

    /**
     * Creates a codec for an immutable {@link Set} of elements of type T.
     *
     * @return An immutable Set codec.
     */
    default Codec<Set<T>> immutableSet() {
        return new CollectionCodec<>(HashSet::new, this, Set::copyOf);
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
     *
     * @param <C>     The specific collection type.
     * @param factory Supplier to create a new collection instance.
     * @return A collection-specific codec.
     */
    default <C extends Collection<T>> Codec<C> collection(Supplier<C> factory) {
        return new CollectionCodec<>(factory, this, Function.identity());
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
            public <D> DataResult<Optional<T>> decode(DynamicOps<D> ops, D input) {
                if (input == null || Objects.equals(input, ops.empty()))
                    return DataResult.success(Optional.empty());

                return self.decode(ops, input).map(Optional::ofNullable);
            }

            @Override
            public <D> DataResult<D> encode(DynamicOps<D> ops, Optional<T> value) {
                if (value.isEmpty()) return DataResult.success(ops.empty());
                return self.encode(ops, value.get());
            }

            @Override
            public String describe() {
                return "Optional[" + self.describe() + "]";
            }

            @Override
            public <R> R accept(CodecVisitor<R> visitor) {
                return visitor.visitOptional(self);
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
            @Override public <D> DataResult<T> decode(DynamicOps<D> ops, D input) {
                D empty = ops.empty();
                if (input == null || Objects.equals(input, empty)) return DataResult.success(null);
                return self.decode(ops, input);
            }

            @Override public <D> DataResult<D> encode(DynamicOps<D> ops, T value) {
                return value == null ? DataResult.success(ops.empty()) : self.encode(ops, value);
            }

            @Override
            public String describe() {
                return self.describe() + "?";
            }

            @Override
            public <R> R accept(CodecVisitor<R> visitor) {
                return self.accept(visitor);
            }
        };
    }

    /**
     * Wraps this codec in a versioned container format.
     * Automatically applies migrations via the DataFixerRegistry before decoding.
     *
     * @param id       The unique identifier for the data fixer.
     * @param registry The orchestrator that handles the migration logic.
     * @return A version-aware codec.
     */
    default Codec<T> versioned(Key id, DataFixerRegistry registry) {
        Codec<T> self = this;
        return new Codec<>() {
            @Override
            public <D> DataResult<T> decode(DynamicOps<D> ops, D input) {
                int version = 0;
                D payload = input;

                Optional<Map<D, D>> optionalMap = ops.getMap(input);
                if (optionalMap.isPresent()) {
                    Map<D, D> map = new HashMap<>(optionalMap.get());
                    D versionKey = ops.createString("data_version");
                    D versionNode = map.get(versionKey);

                    if (versionNode != null) {
                        version = ops.getIntValue(versionNode).orElse(0);
                        map.remove(versionKey);

                        if (map.size() == 1 && map.containsKey(ops.createString("value"))) {
                            payload = map.get(ops.createString("value"));
                        } else {
                            payload = ops.createMap(map);
                        }
                    }
                }

                D upgradedPayload = registry.update(ops, id, version, payload);
                return self.decode(ops, upgradedPayload);
            }

            @Override
            public <D> DataResult<D> encode(DynamicOps<D> ops, T value) {
                return self.encode(ops, value).map(encoded -> {
                    Optional<Map<D, D>> optionalMap = ops.getMap(encoded);
                    Map<D, D> targetMap;

                    if (optionalMap.isPresent()) {
                        targetMap = new LinkedHashMap<>(optionalMap.get());
                    } else {
                        targetMap = new LinkedHashMap<>();
                        targetMap.put(ops.createString("value"), encoded);
                    }

                    targetMap.put(ops.createString("data_version"), ops.createInt(registry.getTargetVersion()));
                    return ops.createMap(targetMap);
                });
            }

            @Override
            public String describe() {
                return "Versioned[" + self.describe() + "]";
            }

            @Override
            public <R> R accept(CodecVisitor<R> visitor) {
                return self.accept(visitor);
            }
        };
    }
}