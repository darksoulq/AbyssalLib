package com.github.darksoulq.abyssallib.common.util;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public interface Condition<T> {
    boolean test(Predicate<T> predicate);

    static <T> Condition<T> one(T value) {
        return new One<>(value);
    }
    @SafeVarargs
    static <T> Condition<T> anyOf(Condition<T>... conditions) {
        return new AnyOf<>(List.of(conditions));
    }
    static <T> Condition<T> anyOf(List<Condition<T>> conditions) {
        return new AnyOf<>(conditions);
    }
    @SafeVarargs
    static <T> Condition<T> allOf(Condition<T>... conditions) {
        return new AllOf<>(List.of(conditions));
    }
    static <T> Condition<T> allOf(List<Condition<T>> conditions) {
        return new AllOf<>(conditions);
    }

    static <T> Codec<Condition<T>> codec(Codec<T> codec) {
        return new Codec<>() {
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

            private <D> List<Condition<T>> decodeList(DynamicOps<D> ops, D input) {
                return ops.getList(input).orElseThrow(() -> new RuntimeException("Expected List"))
                        .stream().map(e -> {
                            try { return decode(ops, e); } 
                            catch (CodecException ex) { throw new RuntimeException(ex); }
                        }).collect(Collectors.toList());
            }

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

            private <D> D encodeList(DynamicOps<D> ops, List<Condition<T>> list) {
                return ops.createList(list.stream().map(e -> {
                    try { return encode(ops, e); } 
                    catch (CodecException ex) { throw new RuntimeException(ex); }
                }).toList());
            }
        };
    }

    record One<T>(T value) implements Condition<T> {
        @Override public boolean test(Predicate<T> predicate) { return predicate.test(value); }
    }
    record AnyOf<T>(List<Condition<T>> children) implements Condition<T> {
        @Override public boolean test(Predicate<T> predicate) {
            for (Condition<T> child : children) if (child.test(predicate)) return true;
            return false;
        }
    }
    record AllOf<T>(List<Condition<T>> children) implements Condition<T> {
        @Override public boolean test(Predicate<T> predicate) {
            for (Condition<T> child : children) if (!child.test(predicate)) return false;
            return true;
        }
    }
}