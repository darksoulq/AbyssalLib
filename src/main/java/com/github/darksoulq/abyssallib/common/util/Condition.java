package com.github.darksoulq.abyssallib.common.util;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

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
             */
            @Override
            public <D> DataResult<Condition<T>> decode(DynamicOps<D> ops, D input) {
                DataResult<T> valRes = codec.decode(ops, input);
                if (valRes.isSuccess()) {
                    return DataResult.success(new One<>(valRes.getOrThrow()));
                }

                return ops.getMap(input)
                    .map(DataResult::success)
                    .orElseGet(() -> DataResult.error("Expected map or value for condition"))
                    .flatMap(map -> {
                        if (map.containsKey(ops.createString("any_of"))) {
                            return decodeList(ops, map.get(ops.createString("any_of"))).map(AnyOf::new);
                        }
                        if (map.containsKey(ops.createString("all_of"))) {
                            return decodeList(ops, map.get(ops.createString("all_of"))).map(AllOf::new);
                        }
                        return DataResult.error("Invalid Logic format: missing any_of or all_of");
                    });
            }

            /**
             * Internal helper to decode a list of conditions.
             *
             * @param ops   The dynamic operations.
             * @param input The serialized list.
             * @param <D>   The serialized type.
             * @return A list of decoded conditions.
             */
            private <D> DataResult<List<Condition<T>>> decodeList(DynamicOps<D> ops, D input) {
                return ops.getList(input)
                    .map(DataResult::success)
                    .orElseGet(() -> DataResult.error("Expected List for logic branch"))
                    .flatMap(list -> {
                        List<Condition<T>> conditions = new ArrayList<>();
                        List<DataError> warnings = new ArrayList<>();
                        for (D e : list) {
                            DataResult<Condition<T>> res = decode(ops, e);
                            if (res.isError()) return DataResult.error(res.error().get());
                            conditions.add(res.getOrThrow());
                            if (res.isPartial()) warnings.addAll(res.warnings());
                        }
                        return warnings.isEmpty() ? DataResult.success(conditions) : DataResult.partial(conditions, warnings);
                    });
            }

            /**
             * Encodes a condition tree into a serialized format.
             * * @param ops   The dynamic operations.
             * @param value The condition tree to encode.
             * @param <D>   The serialized type.
             * @return The serialized representation.
             */
            @Override
            public <D> DataResult<D> encode(DynamicOps<D> ops, Condition<T> value) {
                if (value instanceof One<T>(T value1)) {
                    return codec.encode(ops, value1);
                } else if (value instanceof AnyOf<T>(List<Condition<T>> children)) {
                    return encodeList(ops, children).map(l -> ops.createMap(Map.of(ops.createString("any_of"), l)));
                } else if (value instanceof AllOf<T>(List<Condition<T>> children)) {
                    return encodeList(ops, children).map(l -> ops.createMap(Map.of(ops.createString("all_of"), l)));
                }
                return DataResult.error("Unknown Logic type");
            }

            /**
             * Internal helper to encode a list of conditions.
             *
             * @param ops  The dynamic operations.
             * @param list The list of conditions.
             * @param <D>  The serialized type.
             * @return The serialized list.
             */
            private <D> DataResult<D> encodeList(DynamicOps<D> ops, List<Condition<T>> list) {
                List<D> encoded = new ArrayList<>();
                List<DataError> warnings = new ArrayList<>();
                for (Condition<T> e : list) {
                    DataResult<D> res = encode(ops, e);
                    if (res.isError()) return res;
                    encoded.add(res.getOrThrow());
                    if (res.isPartial()) warnings.addAll(res.warnings());
                }
                return warnings.isEmpty() ? DataResult.success(ops.createList(encoded)) : DataResult.partial(ops.createList(encoded), warnings);
            }

            @Override
            public String describe() {
                return "Condition[" + codec.describe() + "]";
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
         *
         * @return The result of the predicate.
         */
        @Override
        public boolean test(Predicate<T> predicate) {
            return predicate.test(value);
        }
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
         *
         * @return True if at least one child matches.
         */
        @Override
        public boolean test(Predicate<T> predicate) {
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
         *
         * @return True if all children match.
         */
        @Override
        public boolean test(Predicate<T> predicate) {
            for (Condition<T> child : children) if (!child.test(predicate)) return false;
            return true;
        }
    }
}