package com.github.darksoulq.abyssallib.common.util;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Represents a logical condition tree that can be evaluated against a {@link Predicate}.
 * <p>
 * This interface supports three primary types of logic:
 * <ul>
 * <li><b>One:</b> A single value that must satisfy the predicate.</li>
 * <li><b>AnyOf (OR):</b> A collection of conditions where at least one must satisfy the predicate.</li>
 * <li><b>AllOf (AND):</b> A collection of conditions where all must satisfy the predicate.</li>
 * </ul>
 *
 * @param <T> The type of the value to be tested.
 */
public interface Condition<T> {

    /**
     * Evaluates this condition against the provided predicate.
     *
     * @param predicate The predicate to test the underlying values against.
     * @return {@code true} if the logical requirements of this condition are met.
     */
    boolean test(Predicate<T> predicate);

    /**
     * Creates a simple condition representing a single value.
     *
     * @param <T>   The type of the value.
     * @param value The value to be wrapped.
     * @return A new {@link One} condition.
     */
    static <T> Condition<T> one(T value) {
        return new One<>(value);
    }

    /**
     * Creates an OR-gate condition (AnyOf) from a variable number of conditions.
     *
     * @param <T>        The type of the values.
     * @param conditions The conditions to evaluate.
     * @return A new {@link AnyOf} condition.
     */
    @SafeVarargs
    static <T> Condition<T> anyOf(Condition<T>... conditions) {
        return new AnyOf<>(List.of(conditions));
    }

    /**
     * Creates an OR-gate condition (AnyOf) from a list of conditions.
     *
     * @param <T>        The type of the values.
     * @param conditions The list of conditions to evaluate.
     * @return A new {@link AnyOf} condition.
     */
    static <T> Condition<T> anyOf(List<Condition<T>> conditions) {
        return new AnyOf<>(conditions);
    }

    /**
     * Creates an AND-gate condition (AllOf) from a variable number of conditions.
     *
     * @param <T>        The type of the values.
     * @param conditions The conditions to evaluate.
     * @return A new {@link AllOf} condition.
     */
    @SafeVarargs
    static <T> Condition<T> allOf(Condition<T>... conditions) {
        return new AllOf<>(List.of(conditions));
    }

    /**
     * Creates an AND-gate condition (AllOf) from a list of conditions.
     *
     * @param <T>        The type of the values.
     * @param conditions The list of conditions to evaluate.
     * @return A new {@link AllOf} condition.
     */
    static <T> Condition<T> allOf(List<Condition<T>> conditions) {
        return new AllOf<>(conditions);
    }

    /**
     * Creates a codec for serializing and deserializing recursive condition trees.
     * <p>
     * The serialized format supports raw values (interpreted as {@code One}) or
     * objects containing {@code any_of} or {@code all_of} keys.
     *
     * @param <T>   The type of value in the condition.
     * @param codec The base codec for the type T.
     * @return A recursive {@link Codec} for {@link Condition}.
     */
    static <T> Codec<Condition<T>> codec(Codec<T> codec) {
        return new Codec<>() {
            /**
             * Decodes a condition from a serialized format.
             * * @param ops   The dynamic operations.
             * @param input The serialized input.
             * @param <D>   The serialized type.
             * @return A decoded condition tree.
             * @throws CodecException If the format is invalid.
             */
            @Override
            public <D> Condition<T> decode(DynamicOps<D> ops, D input) throws CodecException {
                try {
                    T value = codec.decode(ops, input);
                    if (value != null) return new One<>(value);
                } catch (Exception ignored) {}
                Map<D, D> map = ops.getMap(input).orElse(null);
                if (map != null) {
                    if (map.containsKey(ops.createString("any_of"))) {
                        List<Condition<T>> list = decodeList(ops, map.get(ops.createString("any_of")));
                        return new AnyOf<>(list);
                    }
                    if (map.containsKey(ops.createString("all_of"))) {
                        List<Condition<T>> list = decodeList(ops, map.get(ops.createString("all_of")));
                        return new AllOf<>(list);
                    }
                }
                throw new CodecException("Invalid Logic format");
            }

            /**
             * Internal helper to decode a list of conditions.
             *
             * @param ops   The dynamic operations.
             * @param input The serialized list.
             * @param <D>   The serialized type.
             * @return A list of decoded conditions.
             */
            private <D> List<Condition<T>> decodeList(DynamicOps<D> ops, D input) {
                return ops.getList(input).orElseThrow(() -> new RuntimeException("Expected List"))
                    .stream().map(e -> {
                        try { return decode(ops, e); }
                        catch (CodecException ex) { throw new RuntimeException(ex); }
                    }).collect(Collectors.toList());
            }

            /**
             * Encodes a condition tree into a serialized format.
             * * @param ops   The dynamic operations.
             * @param value The condition tree to encode.
             * @param <D>   The serialized type.
             * @return The serialized representation.
             * @throws CodecException If an unknown condition type is encountered.
             */
            @Override
            public <D> D encode(DynamicOps<D> ops, Condition<T> value) throws CodecException {
                if (value instanceof One<T>(T value1)) {
                    return codec.encode(ops, value1);
                } else if (value instanceof AnyOf<T>(List<Condition<T>> children)) {
                    return ops.createMap(Map.of(ops.createString("any_of"), encodeList(ops, children)));
                } else if (value instanceof AllOf<T>(List<Condition<T>> children)) {
                    return ops.createMap(Map.of(ops.createString("all_of"), encodeList(ops, children)));
                }
                throw new CodecException("Unknown Logic type");
            }

            /**
             * Internal helper to encode a list of conditions.
             *
             * @param ops  The dynamic operations.
             * @param list The list of conditions.
             * @param <D>  The serialized type.
             * @return The serialized list.
             */
            private <D> D encodeList(DynamicOps<D> ops, List<Condition<T>> list) {
                return ops.createList(list.stream().map(e -> {
                    try { return encode(ops, e); }
                    catch (CodecException ex) { throw new RuntimeException(ex); }
                }).toList());
            }
        };
    }

    /**
     * A condition implementation that wraps a single value.
     *
     * @param <T>   The type of the value.
     * @param value The value to test.
     */
    record One<T>(T value) implements Condition<T> {
        /**
         * Tests the single value against the predicate.
         * * @param predicate The predicate to apply.
         * @return The result of the predicate.
         */
        @Override public boolean test(Predicate<T> predicate) { return predicate.test(value); }
    }

    /**
     * A condition implementation that represents a logical OR.
     *
     * @param <T>      The type of the values.
     * @param children The child conditions to evaluate.
     */
    record AnyOf<T>(List<Condition<T>> children) implements Condition<T> {
        /**
         * Returns true if any child condition returns true. Short-circuits on the first success.
         * * @param predicate The predicate to apply to children.
         * @return True if at least one child matches.
         */
        @Override public boolean test(Predicate<T> predicate) {
            for (Condition<T> child : children) if (child.test(predicate)) return true;
            return false;
        }
    }

    /**
     * A condition implementation that represents a logical AND.
     *
     * @param <T>      The type of the values.
     * @param children The child conditions to evaluate.
     */
    record AllOf<T>(List<Condition<T>> children) implements Condition<T> {
        /**
         * Returns true only if all child conditions return true. Short-circuits on the first failure.
         * * @param predicate The predicate to apply to children.
         * @return True if all children match.
         */
        @Override public boolean test(Predicate<T> predicate) {
            for (Condition<T> child : children) if (!child.test(predicate)) return false;
            return true;
        }
    }
}