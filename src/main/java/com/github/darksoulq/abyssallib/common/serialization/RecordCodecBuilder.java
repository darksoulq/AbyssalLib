package com.github.darksoulq.abyssallib.common.serialization;

import java.util.LinkedHashMap;
import java.util.Map;

public final class RecordCodecBuilder {
    private RecordCodecBuilder() {}

    public static <A1, A2, T> Codec<T> create(
            Codec.Field<T, A1> f1, Codec.Field<T, A2> f2,
            Function2<A1, A2, T> builder) {
        return new Codec<>() {
            @Override
            public <D> T decode(DynamicOps<D> ops, D input) throws Codec.CodecException {
                Map<D, D> raw = ops.getMap(input).orElseThrow(() -> new Codec.CodecException("Expected map"));
                A1 v1 = f1.codec().decode(ops, raw.get(ops.createString(f1.name())));
                A2 v2 = f2.codec().decode(ops, raw.get(ops.createString(f2.name())));
                return builder.apply(v1, v2);
            }

            @Override
            public <D> D encode(DynamicOps<D> ops, T value) throws Codec.CodecException {
                Map<D, D> result = new LinkedHashMap<>();
                result.put(ops.createString(f1.name()), f1.codec().encode(ops, f1.getter().apply(value)));
                result.put(ops.createString(f2.name()), f2.codec().encode(ops, f2.getter().apply(value)));
                return ops.createMap(result);
            }
        };
    }

    public static <A1, A2, A3, T> Codec<T> create(
            Codec.Field<T, A1> f1, Codec.Field<T, A2> f2, Codec.Field<T, A3> f3,
            Function3<A1, A2, A3, T> builder) {
        return new Codec<>() {
            @Override
            public <D> T decode(DynamicOps<D> ops, D input) throws Codec.CodecException {
                Map<D, D> raw = ops.getMap(input).orElseThrow(() -> new Codec.CodecException("Expected map"));
                A1 v1 = f1.codec().decode(ops, raw.get(ops.createString(f1.name())));
                A2 v2 = f2.codec().decode(ops, raw.get(ops.createString(f2.name())));
                A3 v3 = f3.codec().decode(ops, raw.get(ops.createString(f3.name())));
                return builder.apply(v1, v2, v3);
            }

            @Override
            public <D> D encode(DynamicOps<D> ops, T value) throws Codec.CodecException {
                Map<D, D> result = new LinkedHashMap<>();
                result.put(ops.createString(f1.name()), f1.codec().encode(ops, f1.getter().apply(value)));
                result.put(ops.createString(f2.name()), f2.codec().encode(ops, f2.getter().apply(value)));
                result.put(ops.createString(f3.name()), f3.codec().encode(ops, f3.getter().apply(value)));
                return ops.createMap(result);
            }
        };
    }

    public static <A1, A2, A3, A4, T> Codec<T> create(
            Codec.Field<T, A1> f1, Codec.Field<T, A2> f2, Codec.Field<T, A3> f3, Codec.Field<T, A4> f4,
            Function4<A1, A2, A3, A4, T> builder) {
        return new Codec<>() {
            @Override
            public <D> T decode(DynamicOps<D> ops, D input) throws Codec.CodecException {
                Map<D, D> raw = ops.getMap(input).orElseThrow(() -> new Codec.CodecException("Expected map"));
                A1 v1 = f1.codec().decode(ops, raw.get(ops.createString(f1.name())));
                A2 v2 = f2.codec().decode(ops, raw.get(ops.createString(f2.name())));
                A3 v3 = f3.codec().decode(ops, raw.get(ops.createString(f3.name())));
                A4 v4 = f4.codec().decode(ops, raw.get(ops.createString(f4.name())));
                return builder.apply(v1, v2, v3, v4);
            }

            @Override
            public <D> D encode(DynamicOps<D> ops, T value) throws Codec.CodecException {
                Map<D, D> result = new LinkedHashMap<>();
                result.put(ops.createString(f1.name()), f1.codec().encode(ops, f1.getter().apply(value)));
                result.put(ops.createString(f2.name()), f2.codec().encode(ops, f2.getter().apply(value)));
                result.put(ops.createString(f3.name()), f3.codec().encode(ops, f3.getter().apply(value)));
                result.put(ops.createString(f4.name()), f4.codec().encode(ops, f4.getter().apply(value)));
                return ops.createMap(result);
            }
        };
    }

    public static <A1, A2, A3, A4, A5, T> Codec<T> create(
            Codec.Field<T, A1> f1, Codec.Field<T, A2> f2, Codec.Field<T, A3> f3, Codec.Field<T, A4> f4, Codec.Field<T, A5> f5,
            Function5<A1, A2, A3, A4, A5, T> builder) {
        return new Codec<>() {
            @Override
            public <D> T decode(DynamicOps<D> ops, D input) throws Codec.CodecException {
                Map<D, D> raw = ops.getMap(input).orElseThrow(() -> new Codec.CodecException("Expected map"));
                A1 v1 = f1.codec().decode(ops, raw.get(ops.createString(f1.name())));
                A2 v2 = f2.codec().decode(ops, raw.get(ops.createString(f2.name())));
                A3 v3 = f3.codec().decode(ops, raw.get(ops.createString(f3.name())));
                A4 v4 = f4.codec().decode(ops, raw.get(ops.createString(f4.name())));
                A5 v5 = f5.codec().decode(ops, raw.get(ops.createString(f5.name())));
                return builder.apply(v1, v2, v3, v4, v5);
            }

            @Override
            public <D> D encode(DynamicOps<D> ops, T value) throws Codec.CodecException {
                Map<D, D> result = new LinkedHashMap<>();
                result.put(ops.createString(f1.name()), f1.codec().encode(ops, f1.getter().apply(value)));
                result.put(ops.createString(f2.name()), f2.codec().encode(ops, f2.getter().apply(value)));
                result.put(ops.createString(f3.name()), f3.codec().encode(ops, f3.getter().apply(value)));
                result.put(ops.createString(f4.name()), f4.codec().encode(ops, f4.getter().apply(value)));
                result.put(ops.createString(f5.name()), f5.codec().encode(ops, f5.getter().apply(value)));
                return ops.createMap(result);
            }
        };
    }

    public static <A1, A2, A3, A4, A5, A6, T> Codec<T> create(
            Codec.Field<T, A1> f1, Codec.Field<T, A2> f2, Codec.Field<T, A3> f3, Codec.Field<T, A4> f4, Codec.Field<T, A5> f5, Codec.Field<T, A6> f6,
            Function6<A1, A2, A3, A4, A5, A6, T> builder) {
        return new Codec<>() {
            @Override
            public <D> T decode(DynamicOps<D> ops, D input) throws Codec.CodecException {
                Map<D, D> raw = ops.getMap(input).orElseThrow(() -> new Codec.CodecException("Expected map"));
                A1 v1 = f1.codec().decode(ops, raw.get(ops.createString(f1.name())));
                A2 v2 = f2.codec().decode(ops, raw.get(ops.createString(f2.name())));
                A3 v3 = f3.codec().decode(ops, raw.get(ops.createString(f3.name())));
                A4 v4 = f4.codec().decode(ops, raw.get(ops.createString(f4.name())));
                A5 v5 = f5.codec().decode(ops, raw.get(ops.createString(f5.name())));
                A6 v6 = f6.codec().decode(ops, raw.get(ops.createString(f6.name())));
                return builder.apply(v1, v2, v3, v4, v5, v6);
            }

            @Override
            public <D> D encode(DynamicOps<D> ops, T value) throws Codec.CodecException {
                Map<D, D> result = new LinkedHashMap<>();
                result.put(ops.createString(f1.name()), f1.codec().encode(ops, f1.getter().apply(value)));
                result.put(ops.createString(f2.name()), f2.codec().encode(ops, f2.getter().apply(value)));
                result.put(ops.createString(f3.name()), f3.codec().encode(ops, f3.getter().apply(value)));
                result.put(ops.createString(f4.name()), f4.codec().encode(ops, f4.getter().apply(value)));
                result.put(ops.createString(f5.name()), f5.codec().encode(ops, f5.getter().apply(value)));
                result.put(ops.createString(f6.name()), f6.codec().encode(ops, f6.getter().apply(value)));
                return ops.createMap(result);
            }
        };
    }

    public static <A1, A2, A3, A4, A5, A6, A7, T> Codec<T> create(
            Codec.Field<T, A1> f1, Codec.Field<T, A2> f2, Codec.Field<T, A3> f3, Codec.Field<T, A4> f4, Codec.Field<T, A5> f5, Codec.Field<T, A6> f6, Codec.Field<T, A7> f7,
            Function7<A1, A2, A3, A4, A5, A6, A7, T> builder) {
        return new Codec<>() {
            @Override
            public <D> T decode(DynamicOps<D> ops, D input) throws Codec.CodecException {
                Map<D, D> raw = ops.getMap(input).orElseThrow(() -> new Codec.CodecException("Expected map"));
                A1 v1 = f1.codec().decode(ops, raw.get(ops.createString(f1.name())));
                A2 v2 = f2.codec().decode(ops, raw.get(ops.createString(f2.name())));
                A3 v3 = f3.codec().decode(ops, raw.get(ops.createString(f3.name())));
                A4 v4 = f4.codec().decode(ops, raw.get(ops.createString(f4.name())));
                A5 v5 = f5.codec().decode(ops, raw.get(ops.createString(f5.name())));
                A6 v6 = f6.codec().decode(ops, raw.get(ops.createString(f6.name())));
                A7 v7 = f7.codec().decode(ops, raw.get(ops.createString(f7.name())));
                return builder.apply(v1, v2, v3, v4, v5, v6, v7);
            }

            @Override
            public <D> D encode(DynamicOps<D> ops, T value) throws Codec.CodecException {
                Map<D, D> result = new LinkedHashMap<>();
                result.put(ops.createString(f1.name()), f1.codec().encode(ops, f1.getter().apply(value)));
                result.put(ops.createString(f2.name()), f2.codec().encode(ops, f2.getter().apply(value)));
                result.put(ops.createString(f3.name()), f3.codec().encode(ops, f3.getter().apply(value)));
                result.put(ops.createString(f4.name()), f4.codec().encode(ops, f4.getter().apply(value)));
                result.put(ops.createString(f5.name()), f5.codec().encode(ops, f5.getter().apply(value)));
                result.put(ops.createString(f6.name()), f6.codec().encode(ops, f6.getter().apply(value)));
                result.put(ops.createString(f7.name()), f7.codec().encode(ops, f7.getter().apply(value)));
                return ops.createMap(result);
            }
        };
    }

    public static <A1, A2, A3, A4, A5, A6, A7, A8, T> Codec<T> create(
            Codec.Field<T, A1> f1, Codec.Field<T, A2> f2, Codec.Field<T, A3> f3, Codec.Field<T, A4> f4, Codec.Field<T, A5> f5, Codec.Field<T, A6> f6, Codec.Field<T, A7> f7, Codec.Field<T, A8> f8,
            Function8<A1, A2, A3, A4, A5, A6, A7, A8, T> builder) {
        return new Codec<>() {
            @Override
            public <D> T decode(DynamicOps<D> ops, D input) throws Codec.CodecException {
                Map<D, D> raw = ops.getMap(input).orElseThrow(() -> new Codec.CodecException("Expected map"));
                A1 v1 = f1.codec().decode(ops, raw.get(ops.createString(f1.name())));
                A2 v2 = f2.codec().decode(ops, raw.get(ops.createString(f2.name())));
                A3 v3 = f3.codec().decode(ops, raw.get(ops.createString(f3.name())));
                A4 v4 = f4.codec().decode(ops, raw.get(ops.createString(f4.name())));
                A5 v5 = f5.codec().decode(ops, raw.get(ops.createString(f5.name())));
                A6 v6 = f6.codec().decode(ops, raw.get(ops.createString(f6.name())));
                A7 v7 = f7.codec().decode(ops, raw.get(ops.createString(f7.name())));
                A8 v8 = f8.codec().decode(ops, raw.get(ops.createString(f8.name())));
                return builder.apply(v1, v2, v3, v4, v5, v6, v7, v8);
            }

            @Override
            public <D> D encode(DynamicOps<D> ops, T value) throws Codec.CodecException {
                Map<D, D> result = new LinkedHashMap<>();
                result.put(ops.createString(f1.name()), f1.codec().encode(ops, f1.getter().apply(value)));
                result.put(ops.createString(f2.name()), f2.codec().encode(ops, f2.getter().apply(value)));
                result.put(ops.createString(f3.name()), f3.codec().encode(ops, f3.getter().apply(value)));
                result.put(ops.createString(f4.name()), f4.codec().encode(ops, f4.getter().apply(value)));
                result.put(ops.createString(f5.name()), f5.codec().encode(ops, f5.getter().apply(value)));
                result.put(ops.createString(f6.name()), f6.codec().encode(ops, f6.getter().apply(value)));
                result.put(ops.createString(f7.name()), f7.codec().encode(ops, f7.getter().apply(value)));
                result.put(ops.createString(f8.name()), f8.codec().encode(ops, f8.getter().apply(value)));
                return ops.createMap(result);
            }
        };
    }

    public static <A1, A2, A3, A4, A5, A6, A7, A8, A9, T> Codec<T> create(
            Codec.Field<T, A1> f1, Codec.Field<T, A2> f2, Codec.Field<T, A3> f3, Codec.Field<T, A4> f4, Codec.Field<T, A5> f5, Codec.Field<T, A6> f6, Codec.Field<T, A7> f7, Codec.Field<T, A8> f8, Codec.Field<T, A9> f9,
            Function9<A1, A2, A3, A4, A5, A6, A7, A8, A9, T> builder) {
        return new Codec<>() {
            @Override
            public <D> T decode(DynamicOps<D> ops, D input) throws Codec.CodecException {
                Map<D, D> raw = ops.getMap(input).orElseThrow(() -> new Codec.CodecException("Expected map"));
                A1 v1 = f1.codec().decode(ops, raw.get(ops.createString(f1.name())));
                A2 v2 = f2.codec().decode(ops, raw.get(ops.createString(f2.name())));
                A3 v3 = f3.codec().decode(ops, raw.get(ops.createString(f3.name())));
                A4 v4 = f4.codec().decode(ops, raw.get(ops.createString(f4.name())));
                A5 v5 = f5.codec().decode(ops, raw.get(ops.createString(f5.name())));
                A6 v6 = f6.codec().decode(ops, raw.get(ops.createString(f6.name())));
                A7 v7 = f7.codec().decode(ops, raw.get(ops.createString(f7.name())));
                A8 v8 = f8.codec().decode(ops, raw.get(ops.createString(f8.name())));
                A9 v9 = f9.codec().decode(ops, raw.get(ops.createString(f9.name())));
                return builder.apply(v1, v2, v3, v4, v5, v6, v7, v8, v9);
            }

            @Override
            public <D> D encode(DynamicOps<D> ops, T value) throws Codec.CodecException {
                Map<D, D> result = new LinkedHashMap<>();
                result.put(ops.createString(f1.name()), f1.codec().encode(ops, f1.getter().apply(value)));
                result.put(ops.createString(f2.name()), f2.codec().encode(ops, f2.getter().apply(value)));
                result.put(ops.createString(f3.name()), f3.codec().encode(ops, f3.getter().apply(value)));
                result.put(ops.createString(f4.name()), f4.codec().encode(ops, f4.getter().apply(value)));
                result.put(ops.createString(f5.name()), f5.codec().encode(ops, f5.getter().apply(value)));
                result.put(ops.createString(f6.name()), f6.codec().encode(ops, f6.getter().apply(value)));
                result.put(ops.createString(f7.name()), f7.codec().encode(ops, f7.getter().apply(value)));
                result.put(ops.createString(f8.name()), f8.codec().encode(ops, f8.getter().apply(value)));
                result.put(ops.createString(f9.name()), f9.codec().encode(ops, f9.getter().apply(value)));
                return ops.createMap(result);
            }
        };
    }

    public static <A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, T> Codec<T> create(
            Codec.Field<T, A1> f1, Codec.Field<T, A2> f2, Codec.Field<T, A3> f3, Codec.Field<T, A4> f4, Codec.Field<T, A5> f5, Codec.Field<T, A6> f6, Codec.Field<T, A7> f7, Codec.Field<T, A8> f8, Codec.Field<T, A9> f9, Codec.Field<T, A10> f10,
            Function10<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, T> builder) {
        return new Codec<>() {
            @Override
            public <D> T decode(DynamicOps<D> ops, D input) throws Codec.CodecException {
                Map<D, D> raw = ops.getMap(input).orElseThrow(() -> new Codec.CodecException("Expected map"));
                A1 v1 = f1.codec().decode(ops, raw.get(ops.createString(f1.name())));
                A2 v2 = f2.codec().decode(ops, raw.get(ops.createString(f2.name())));
                A3 v3 = f3.codec().decode(ops, raw.get(ops.createString(f3.name())));
                A4 v4 = f4.codec().decode(ops, raw.get(ops.createString(f4.name())));
                A5 v5 = f5.codec().decode(ops, raw.get(ops.createString(f5.name())));
                A6 v6 = f6.codec().decode(ops, raw.get(ops.createString(f6.name())));
                A7 v7 = f7.codec().decode(ops, raw.get(ops.createString(f7.name())));
                A8 v8 = f8.codec().decode(ops, raw.get(ops.createString(f8.name())));
                A9 v9 = f9.codec().decode(ops, raw.get(ops.createString(f9.name())));
                A10 v10 = f10.codec().decode(ops, raw.get(ops.createString(f10.name())));
                return builder.apply(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10);
            }

            @Override
            public <D> D encode(DynamicOps<D> ops, T value) throws Codec.CodecException {
                Map<D, D> result = new LinkedHashMap<>();
                result.put(ops.createString(f1.name()), f1.codec().encode(ops, f1.getter().apply(value)));
                result.put(ops.createString(f2.name()), f2.codec().encode(ops, f2.getter().apply(value)));
                result.put(ops.createString(f3.name()), f3.codec().encode(ops, f3.getter().apply(value)));
                result.put(ops.createString(f4.name()), f4.codec().encode(ops, f4.getter().apply(value)));
                result.put(ops.createString(f5.name()), f5.codec().encode(ops, f5.getter().apply(value)));
                result.put(ops.createString(f6.name()), f6.codec().encode(ops, f6.getter().apply(value)));
                result.put(ops.createString(f7.name()), f7.codec().encode(ops, f7.getter().apply(value)));
                result.put(ops.createString(f8.name()), f8.codec().encode(ops, f8.getter().apply(value)));
                result.put(ops.createString(f9.name()), f9.codec().encode(ops, f9.getter().apply(value)));
                result.put(ops.createString(f10.name()), f10.codec().encode(ops, f10.getter().apply(value)));
                return ops.createMap(result);
            }
        };
    }

    public static <A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, T> Codec<T> create(
            Codec.Field<T, A1> f1, Codec.Field<T, A2> f2, Codec.Field<T, A3> f3, Codec.Field<T, A4> f4, Codec.Field<T, A5> f5, Codec.Field<T, A6> f6, Codec.Field<T, A7> f7, Codec.Field<T, A8> f8, Codec.Field<T, A9> f9, Codec.Field<T, A10> f10, Codec.Field<T, A11> f11,
            Function11<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, T> builder) {
        return new Codec<>() {
            @Override
            public <D> T decode(DynamicOps<D> ops, D input) throws Codec.CodecException {
                Map<D, D> raw = ops.getMap(input).orElseThrow(() -> new Codec.CodecException("Expected map"));
                A1 v1 = f1.codec().decode(ops, raw.get(ops.createString(f1.name())));
                A2 v2 = f2.codec().decode(ops, raw.get(ops.createString(f2.name())));
                A3 v3 = f3.codec().decode(ops, raw.get(ops.createString(f3.name())));
                A4 v4 = f4.codec().decode(ops, raw.get(ops.createString(f4.name())));
                A5 v5 = f5.codec().decode(ops, raw.get(ops.createString(f5.name())));
                A6 v6 = f6.codec().decode(ops, raw.get(ops.createString(f6.name())));
                A7 v7 = f7.codec().decode(ops, raw.get(ops.createString(f7.name())));
                A8 v8 = f8.codec().decode(ops, raw.get(ops.createString(f8.name())));
                A9 v9 = f9.codec().decode(ops, raw.get(ops.createString(f9.name())));
                A10 v10 = f10.codec().decode(ops, raw.get(ops.createString(f10.name())));
                A11 v11 = f11.codec().decode(ops, raw.get(ops.createString(f11.name())));
                return builder.apply(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11);
            }

            @Override
            public <D> D encode(DynamicOps<D> ops, T value) throws Codec.CodecException {
                Map<D, D> result = new LinkedHashMap<>();
                result.put(ops.createString(f1.name()), f1.codec().encode(ops, f1.getter().apply(value)));
                result.put(ops.createString(f2.name()), f2.codec().encode(ops, f2.getter().apply(value)));
                result.put(ops.createString(f3.name()), f3.codec().encode(ops, f3.getter().apply(value)));
                result.put(ops.createString(f4.name()), f4.codec().encode(ops, f4.getter().apply(value)));
                result.put(ops.createString(f5.name()), f5.codec().encode(ops, f5.getter().apply(value)));
                result.put(ops.createString(f6.name()), f6.codec().encode(ops, f6.getter().apply(value)));
                result.put(ops.createString(f7.name()), f7.codec().encode(ops, f7.getter().apply(value)));
                result.put(ops.createString(f8.name()), f8.codec().encode(ops, f8.getter().apply(value)));
                result.put(ops.createString(f9.name()), f9.codec().encode(ops, f9.getter().apply(value)));
                result.put(ops.createString(f10.name()), f10.codec().encode(ops, f10.getter().apply(value)));
                result.put(ops.createString(f11.name()), f11.codec().encode(ops, f11.getter().apply(value)));
                return ops.createMap(result);
            }
        };
    }

    public static <A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, T> Codec<T> create(
            Codec.Field<T, A1> f1, Codec.Field<T, A2> f2, Codec.Field<T, A3> f3, Codec.Field<T, A4> f4, Codec.Field<T, A5> f5, Codec.Field<T, A6> f6, Codec.Field<T, A7> f7, Codec.Field<T, A8> f8, Codec.Field<T, A9> f9, Codec.Field<T, A10> f10, Codec.Field<T, A11> f11, Codec.Field<T, A12> f12,
            Function12<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, T> builder) {
        return new Codec<>() {
            @Override
            public <D> T decode(DynamicOps<D> ops, D input) throws Codec.CodecException {
                Map<D, D> raw = ops.getMap(input).orElseThrow(() -> new Codec.CodecException("Expected map"));
                A1 v1 = f1.codec().decode(ops, raw.get(ops.createString(f1.name())));
                A2 v2 = f2.codec().decode(ops, raw.get(ops.createString(f2.name())));
                A3 v3 = f3.codec().decode(ops, raw.get(ops.createString(f3.name())));
                A4 v4 = f4.codec().decode(ops, raw.get(ops.createString(f4.name())));
                A5 v5 = f5.codec().decode(ops, raw.get(ops.createString(f5.name())));
                A6 v6 = f6.codec().decode(ops, raw.get(ops.createString(f6.name())));
                A7 v7 = f7.codec().decode(ops, raw.get(ops.createString(f7.name())));
                A8 v8 = f8.codec().decode(ops, raw.get(ops.createString(f8.name())));
                A9 v9 = f9.codec().decode(ops, raw.get(ops.createString(f9.name())));
                A10 v10 = f10.codec().decode(ops, raw.get(ops.createString(f10.name())));
                A11 v11 = f11.codec().decode(ops, raw.get(ops.createString(f11.name())));
                A12 v12 = f12.codec().decode(ops, raw.get(ops.createString(f12.name())));
                return builder.apply(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12);
            }

            @Override
            public <D> D encode(DynamicOps<D> ops, T value) throws Codec.CodecException {
                Map<D, D> result = new LinkedHashMap<>();
                result.put(ops.createString(f1.name()), f1.codec().encode(ops, f1.getter().apply(value)));
                result.put(ops.createString(f2.name()), f2.codec().encode(ops, f2.getter().apply(value)));
                result.put(ops.createString(f3.name()), f3.codec().encode(ops, f3.getter().apply(value)));
                result.put(ops.createString(f4.name()), f4.codec().encode(ops, f4.getter().apply(value)));
                result.put(ops.createString(f5.name()), f5.codec().encode(ops, f5.getter().apply(value)));
                result.put(ops.createString(f6.name()), f6.codec().encode(ops, f6.getter().apply(value)));
                result.put(ops.createString(f7.name()), f7.codec().encode(ops, f7.getter().apply(value)));
                result.put(ops.createString(f8.name()), f8.codec().encode(ops, f8.getter().apply(value)));
                result.put(ops.createString(f9.name()), f9.codec().encode(ops, f9.getter().apply(value)));
                result.put(ops.createString(f10.name()), f10.codec().encode(ops, f10.getter().apply(value)));
                result.put(ops.createString(f11.name()), f11.codec().encode(ops, f11.getter().apply(value)));
                result.put(ops.createString(f12.name()), f12.codec().encode(ops, f12.getter().apply(value)));
                return ops.createMap(result);
            }
        };
    }

    public static <A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, T> Codec<T> create(
            Codec.Field<T, A1> f1, Codec.Field<T, A2> f2, Codec.Field<T, A3> f3, Codec.Field<T, A4> f4, Codec.Field<T, A5> f5, Codec.Field<T, A6> f6, Codec.Field<T, A7> f7, Codec.Field<T, A8> f8, Codec.Field<T, A9> f9, Codec.Field<T, A10> f10, Codec.Field<T, A11> f11, Codec.Field<T, A12> f12, Codec.Field<T, A13> f13,
            Function13<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, T> builder) {
        return new Codec<>() {
            @Override
            public <D> T decode(DynamicOps<D> ops, D input) throws Codec.CodecException {
                Map<D, D> raw = ops.getMap(input).orElseThrow(() -> new Codec.CodecException("Expected map"));
                A1 v1 = f1.codec().decode(ops, raw.get(ops.createString(f1.name())));
                A2 v2 = f2.codec().decode(ops, raw.get(ops.createString(f2.name())));
                A3 v3 = f3.codec().decode(ops, raw.get(ops.createString(f3.name())));
                A4 v4 = f4.codec().decode(ops, raw.get(ops.createString(f4.name())));
                A5 v5 = f5.codec().decode(ops, raw.get(ops.createString(f5.name())));
                A6 v6 = f6.codec().decode(ops, raw.get(ops.createString(f6.name())));
                A7 v7 = f7.codec().decode(ops, raw.get(ops.createString(f7.name())));
                A8 v8 = f8.codec().decode(ops, raw.get(ops.createString(f8.name())));
                A9 v9 = f9.codec().decode(ops, raw.get(ops.createString(f9.name())));
                A10 v10 = f10.codec().decode(ops, raw.get(ops.createString(f10.name())));
                A11 v11 = f11.codec().decode(ops, raw.get(ops.createString(f11.name())));
                A12 v12 = f12.codec().decode(ops, raw.get(ops.createString(f12.name())));
                A13 v13 = f13.codec().decode(ops, raw.get(ops.createString(f13.name())));
                return builder.apply(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13);
            }

            @Override
            public <D> D encode(DynamicOps<D> ops, T value) throws Codec.CodecException {
                Map<D, D> result = new LinkedHashMap<>();
                result.put(ops.createString(f1.name()), f1.codec().encode(ops, f1.getter().apply(value)));
                result.put(ops.createString(f2.name()), f2.codec().encode(ops, f2.getter().apply(value)));
                result.put(ops.createString(f3.name()), f3.codec().encode(ops, f3.getter().apply(value)));
                result.put(ops.createString(f4.name()), f4.codec().encode(ops, f4.getter().apply(value)));
                result.put(ops.createString(f5.name()), f5.codec().encode(ops, f5.getter().apply(value)));
                result.put(ops.createString(f6.name()), f6.codec().encode(ops, f6.getter().apply(value)));
                result.put(ops.createString(f7.name()), f7.codec().encode(ops, f7.getter().apply(value)));
                result.put(ops.createString(f8.name()), f8.codec().encode(ops, f8.getter().apply(value)));
                result.put(ops.createString(f9.name()), f9.codec().encode(ops, f9.getter().apply(value)));
                result.put(ops.createString(f10.name()), f10.codec().encode(ops, f10.getter().apply(value)));
                result.put(ops.createString(f11.name()), f11.codec().encode(ops, f11.getter().apply(value)));
                result.put(ops.createString(f12.name()), f12.codec().encode(ops, f12.getter().apply(value)));
                result.put(ops.createString(f13.name()), f13.codec().encode(ops, f13.getter().apply(value)));
                return ops.createMap(result);
            }
        };
    }

    public static <A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, T> Codec<T> create(
            Codec.Field<T, A1> f1, Codec.Field<T, A2> f2, Codec.Field<T, A3> f3, Codec.Field<T, A4> f4, Codec.Field<T, A5> f5, Codec.Field<T, A6> f6, Codec.Field<T, A7> f7, Codec.Field<T, A8> f8, Codec.Field<T, A9> f9, Codec.Field<T, A10> f10, Codec.Field<T, A11> f11, Codec.Field<T, A12> f12, Codec.Field<T, A13> f13, Codec.Field<T, A14> f14,
            Function14<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, T> builder) {
        return new Codec<>() {
            @Override
            public <D> T decode(DynamicOps<D> ops, D input) throws Codec.CodecException {
                Map<D, D> raw = ops.getMap(input).orElseThrow(() -> new Codec.CodecException("Expected map"));
                A1 v1 = f1.codec().decode(ops, raw.get(ops.createString(f1.name())));
                A2 v2 = f2.codec().decode(ops, raw.get(ops.createString(f2.name())));
                A3 v3 = f3.codec().decode(ops, raw.get(ops.createString(f3.name())));
                A4 v4 = f4.codec().decode(ops, raw.get(ops.createString(f4.name())));
                A5 v5 = f5.codec().decode(ops, raw.get(ops.createString(f5.name())));
                A6 v6 = f6.codec().decode(ops, raw.get(ops.createString(f6.name())));
                A7 v7 = f7.codec().decode(ops, raw.get(ops.createString(f7.name())));
                A8 v8 = f8.codec().decode(ops, raw.get(ops.createString(f8.name())));
                A9 v9 = f9.codec().decode(ops, raw.get(ops.createString(f9.name())));
                A10 v10 = f10.codec().decode(ops, raw.get(ops.createString(f10.name())));
                A11 v11 = f11.codec().decode(ops, raw.get(ops.createString(f11.name())));
                A12 v12 = f12.codec().decode(ops, raw.get(ops.createString(f12.name())));
                A13 v13 = f13.codec().decode(ops, raw.get(ops.createString(f13.name())));
                A14 v14 = f14.codec().decode(ops, raw.get(ops.createString(f14.name())));
                return builder.apply(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14);
            }

            @Override
            public <D> D encode(DynamicOps<D> ops, T value) throws Codec.CodecException {
                Map<D, D> result = new LinkedHashMap<>();
                result.put(ops.createString(f1.name()), f1.codec().encode(ops, f1.getter().apply(value)));
                result.put(ops.createString(f2.name()), f2.codec().encode(ops, f2.getter().apply(value)));
                result.put(ops.createString(f3.name()), f3.codec().encode(ops, f3.getter().apply(value)));
                result.put(ops.createString(f4.name()), f4.codec().encode(ops, f4.getter().apply(value)));
                result.put(ops.createString(f5.name()), f5.codec().encode(ops, f5.getter().apply(value)));
                result.put(ops.createString(f6.name()), f6.codec().encode(ops, f6.getter().apply(value)));
                result.put(ops.createString(f7.name()), f7.codec().encode(ops, f7.getter().apply(value)));
                result.put(ops.createString(f8.name()), f8.codec().encode(ops, f8.getter().apply(value)));
                result.put(ops.createString(f9.name()), f9.codec().encode(ops, f9.getter().apply(value)));
                result.put(ops.createString(f10.name()), f10.codec().encode(ops, f10.getter().apply(value)));
                result.put(ops.createString(f11.name()), f11.codec().encode(ops, f11.getter().apply(value)));
                result.put(ops.createString(f12.name()), f12.codec().encode(ops, f12.getter().apply(value)));
                result.put(ops.createString(f13.name()), f13.codec().encode(ops, f13.getter().apply(value)));
                result.put(ops.createString(f14.name()), f14.codec().encode(ops, f14.getter().apply(value)));
                return ops.createMap(result);
            }
        };
    }

    public static <A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, T> Codec<T> create(
            Codec.Field<T, A1> f1, Codec.Field<T, A2> f2, Codec.Field<T, A3> f3, Codec.Field<T, A4> f4, Codec.Field<T, A5> f5, Codec.Field<T, A6> f6, Codec.Field<T, A7> f7, Codec.Field<T, A8> f8, Codec.Field<T, A9> f9, Codec.Field<T, A10> f10, Codec.Field<T, A11> f11, Codec.Field<T, A12> f12, Codec.Field<T, A13> f13, Codec.Field<T, A14> f14, Codec.Field<T, A15> f15,
            Function15<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, T> builder) {
        return new Codec<>() {
            @Override
            public <D> T decode(DynamicOps<D> ops, D input) throws Codec.CodecException {
                Map<D, D> raw = ops.getMap(input).orElseThrow(() -> new Codec.CodecException("Expected map"));
                A1 v1 = f1.codec().decode(ops, raw.get(ops.createString(f1.name())));
                A2 v2 = f2.codec().decode(ops, raw.get(ops.createString(f2.name())));
                A3 v3 = f3.codec().decode(ops, raw.get(ops.createString(f3.name())));
                A4 v4 = f4.codec().decode(ops, raw.get(ops.createString(f4.name())));
                A5 v5 = f5.codec().decode(ops, raw.get(ops.createString(f5.name())));
                A6 v6 = f6.codec().decode(ops, raw.get(ops.createString(f6.name())));
                A7 v7 = f7.codec().decode(ops, raw.get(ops.createString(f7.name())));
                A8 v8 = f8.codec().decode(ops, raw.get(ops.createString(f8.name())));
                A9 v9 = f9.codec().decode(ops, raw.get(ops.createString(f9.name())));
                A10 v10 = f10.codec().decode(ops, raw.get(ops.createString(f10.name())));
                A11 v11 = f11.codec().decode(ops, raw.get(ops.createString(f11.name())));
                A12 v12 = f12.codec().decode(ops, raw.get(ops.createString(f12.name())));
                A13 v13 = f13.codec().decode(ops, raw.get(ops.createString(f13.name())));
                A14 v14 = f14.codec().decode(ops, raw.get(ops.createString(f14.name())));
                A15 v15 = f15.codec().decode(ops, raw.get(ops.createString(f15.name())));
                return builder.apply(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15);
            }

            @Override
            public <D> D encode(DynamicOps<D> ops, T value) throws Codec.CodecException {
                Map<D, D> result = new LinkedHashMap<>();
                result.put(ops.createString(f1.name()), f1.codec().encode(ops, f1.getter().apply(value)));
                result.put(ops.createString(f2.name()), f2.codec().encode(ops, f2.getter().apply(value)));
                result.put(ops.createString(f3.name()), f3.codec().encode(ops, f3.getter().apply(value)));
                result.put(ops.createString(f4.name()), f4.codec().encode(ops, f4.getter().apply(value)));
                result.put(ops.createString(f5.name()), f5.codec().encode(ops, f5.getter().apply(value)));
                result.put(ops.createString(f6.name()), f6.codec().encode(ops, f6.getter().apply(value)));
                result.put(ops.createString(f7.name()), f7.codec().encode(ops, f7.getter().apply(value)));
                result.put(ops.createString(f8.name()), f8.codec().encode(ops, f8.getter().apply(value)));
                result.put(ops.createString(f9.name()), f9.codec().encode(ops, f9.getter().apply(value)));
                result.put(ops.createString(f10.name()), f10.codec().encode(ops, f10.getter().apply(value)));
                result.put(ops.createString(f11.name()), f11.codec().encode(ops, f11.getter().apply(value)));
                result.put(ops.createString(f12.name()), f12.codec().encode(ops, f12.getter().apply(value)));
                result.put(ops.createString(f13.name()), f13.codec().encode(ops, f13.getter().apply(value)));
                result.put(ops.createString(f14.name()), f14.codec().encode(ops, f14.getter().apply(value)));
                result.put(ops.createString(f15.name()), f15.codec().encode(ops, f15.getter().apply(value)));
                return ops.createMap(result);
            }
        };
    }

    public static <A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, T> Codec<T> create(
            Codec.Field<T, A1> f1, Codec.Field<T, A2> f2, Codec.Field<T, A3> f3, Codec.Field<T, A4> f4, Codec.Field<T, A5> f5, Codec.Field<T, A6> f6, Codec.Field<T, A7> f7, Codec.Field<T, A8> f8, Codec.Field<T, A9> f9, Codec.Field<T, A10> f10, Codec.Field<T, A11> f11, Codec.Field<T, A12> f12, Codec.Field<T, A13> f13, Codec.Field<T, A14> f14, Codec.Field<T, A15> f15, Codec.Field<T, A16> f16,
            Function16<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, T> builder) {
        return new Codec<>() {
            @Override
            public <D> T decode(DynamicOps<D> ops, D input) throws Codec.CodecException {
                Map<D, D> raw = ops.getMap(input).orElseThrow(() -> new Codec.CodecException("Expected map"));
                A1 v1 = f1.codec().decode(ops, raw.get(ops.createString(f1.name())));
                A2 v2 = f2.codec().decode(ops, raw.get(ops.createString(f2.name())));
                A3 v3 = f3.codec().decode(ops, raw.get(ops.createString(f3.name())));
                A4 v4 = f4.codec().decode(ops, raw.get(ops.createString(f4.name())));
                A5 v5 = f5.codec().decode(ops, raw.get(ops.createString(f5.name())));
                A6 v6 = f6.codec().decode(ops, raw.get(ops.createString(f6.name())));
                A7 v7 = f7.codec().decode(ops, raw.get(ops.createString(f7.name())));
                A8 v8 = f8.codec().decode(ops, raw.get(ops.createString(f8.name())));
                A9 v9 = f9.codec().decode(ops, raw.get(ops.createString(f9.name())));
                A10 v10 = f10.codec().decode(ops, raw.get(ops.createString(f10.name())));
                A11 v11 = f11.codec().decode(ops, raw.get(ops.createString(f11.name())));
                A12 v12 = f12.codec().decode(ops, raw.get(ops.createString(f12.name())));
                A13 v13 = f13.codec().decode(ops, raw.get(ops.createString(f13.name())));
                A14 v14 = f14.codec().decode(ops, raw.get(ops.createString(f14.name())));
                A15 v15 = f15.codec().decode(ops, raw.get(ops.createString(f15.name())));
                A16 v16 = f16.codec().decode(ops, raw.get(ops.createString(f16.name())));
                return builder.apply(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15, v16);
            }

            @Override
            public <D> D encode(DynamicOps<D> ops, T value) throws Codec.CodecException {
                Map<D, D> result = new LinkedHashMap<>();
                result.put(ops.createString(f1.name()), f1.codec().encode(ops, f1.getter().apply(value)));
                result.put(ops.createString(f2.name()), f2.codec().encode(ops, f2.getter().apply(value)));
                result.put(ops.createString(f3.name()), f3.codec().encode(ops, f3.getter().apply(value)));
                result.put(ops.createString(f4.name()), f4.codec().encode(ops, f4.getter().apply(value)));
                result.put(ops.createString(f5.name()), f5.codec().encode(ops, f5.getter().apply(value)));
                result.put(ops.createString(f6.name()), f6.codec().encode(ops, f6.getter().apply(value)));
                result.put(ops.createString(f7.name()), f7.codec().encode(ops, f7.getter().apply(value)));
                result.put(ops.createString(f8.name()), f8.codec().encode(ops, f8.getter().apply(value)));
                result.put(ops.createString(f9.name()), f9.codec().encode(ops, f9.getter().apply(value)));
                result.put(ops.createString(f10.name()), f10.codec().encode(ops, f10.getter().apply(value)));
                result.put(ops.createString(f11.name()), f11.codec().encode(ops, f11.getter().apply(value)));
                result.put(ops.createString(f12.name()), f12.codec().encode(ops, f12.getter().apply(value)));
                result.put(ops.createString(f13.name()), f13.codec().encode(ops, f13.getter().apply(value)));
                result.put(ops.createString(f14.name()), f14.codec().encode(ops, f14.getter().apply(value)));
                result.put(ops.createString(f15.name()), f15.codec().encode(ops, f15.getter().apply(value)));
                result.put(ops.createString(f16.name()), f16.codec().encode(ops, f16.getter().apply(value)));
                return ops.createMap(result);
            }
        };
    }

    public static <A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, T> Codec<T> create(
            Codec.Field<T, A1> f1, Codec.Field<T, A2> f2, Codec.Field<T, A3> f3, Codec.Field<T, A4> f4, Codec.Field<T, A5> f5, Codec.Field<T, A6> f6, Codec.Field<T, A7> f7, Codec.Field<T, A8> f8, Codec.Field<T, A9> f9, Codec.Field<T, A10> f10, Codec.Field<T, A11> f11, Codec.Field<T, A12> f12, Codec.Field<T, A13> f13, Codec.Field<T, A14> f14, Codec.Field<T, A15> f15, Codec.Field<T, A16> f16, Codec.Field<T, A17> f17,
            Function17<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, T> builder) {
        return new Codec<>() {
            @Override
            public <D> T decode(DynamicOps<D> ops, D input) throws Codec.CodecException {
                Map<D, D> raw = ops.getMap(input).orElseThrow(() -> new Codec.CodecException("Expected map"));
                A1 v1 = f1.codec().decode(ops, raw.get(ops.createString(f1.name())));
                A2 v2 = f2.codec().decode(ops, raw.get(ops.createString(f2.name())));
                A3 v3 = f3.codec().decode(ops, raw.get(ops.createString(f3.name())));
                A4 v4 = f4.codec().decode(ops, raw.get(ops.createString(f4.name())));
                A5 v5 = f5.codec().decode(ops, raw.get(ops.createString(f5.name())));
                A6 v6 = f6.codec().decode(ops, raw.get(ops.createString(f6.name())));
                A7 v7 = f7.codec().decode(ops, raw.get(ops.createString(f7.name())));
                A8 v8 = f8.codec().decode(ops, raw.get(ops.createString(f8.name())));
                A9 v9 = f9.codec().decode(ops, raw.get(ops.createString(f9.name())));
                A10 v10 = f10.codec().decode(ops, raw.get(ops.createString(f10.name())));
                A11 v11 = f11.codec().decode(ops, raw.get(ops.createString(f11.name())));
                A12 v12 = f12.codec().decode(ops, raw.get(ops.createString(f12.name())));
                A13 v13 = f13.codec().decode(ops, raw.get(ops.createString(f13.name())));
                A14 v14 = f14.codec().decode(ops, raw.get(ops.createString(f14.name())));
                A15 v15 = f15.codec().decode(ops, raw.get(ops.createString(f15.name())));
                A16 v16 = f16.codec().decode(ops, raw.get(ops.createString(f16.name())));
                A17 v17 = f17.codec().decode(ops, raw.get(ops.createString(f17.name())));
                return builder.apply(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15, v16, v17);
            }

            @Override
            public <D> D encode(DynamicOps<D> ops, T value) throws Codec.CodecException {
                Map<D, D> result = new LinkedHashMap<>();
                result.put(ops.createString(f1.name()), f1.codec().encode(ops, f1.getter().apply(value)));
                result.put(ops.createString(f2.name()), f2.codec().encode(ops, f2.getter().apply(value)));
                result.put(ops.createString(f3.name()), f3.codec().encode(ops, f3.getter().apply(value)));
                result.put(ops.createString(f4.name()), f4.codec().encode(ops, f4.getter().apply(value)));
                result.put(ops.createString(f5.name()), f5.codec().encode(ops, f5.getter().apply(value)));
                result.put(ops.createString(f6.name()), f6.codec().encode(ops, f6.getter().apply(value)));
                result.put(ops.createString(f7.name()), f7.codec().encode(ops, f7.getter().apply(value)));
                result.put(ops.createString(f8.name()), f8.codec().encode(ops, f8.getter().apply(value)));
                result.put(ops.createString(f9.name()), f9.codec().encode(ops, f9.getter().apply(value)));
                result.put(ops.createString(f10.name()), f10.codec().encode(ops, f10.getter().apply(value)));
                result.put(ops.createString(f11.name()), f11.codec().encode(ops, f11.getter().apply(value)));
                result.put(ops.createString(f12.name()), f12.codec().encode(ops, f12.getter().apply(value)));
                result.put(ops.createString(f13.name()), f13.codec().encode(ops, f13.getter().apply(value)));
                result.put(ops.createString(f14.name()), f14.codec().encode(ops, f14.getter().apply(value)));
                result.put(ops.createString(f15.name()), f15.codec().encode(ops, f15.getter().apply(value)));
                result.put(ops.createString(f16.name()), f16.codec().encode(ops, f16.getter().apply(value)));
                result.put(ops.createString(f17.name()), f17.codec().encode(ops, f17.getter().apply(value)));
                return ops.createMap(result);
            }
        };
    }

    public static <A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, T> Codec<T> create(
            Codec.Field<T, A1> f1, Codec.Field<T, A2> f2, Codec.Field<T, A3> f3, Codec.Field<T, A4> f4, Codec.Field<T, A5> f5, Codec.Field<T, A6> f6, Codec.Field<T, A7> f7, Codec.Field<T, A8> f8, Codec.Field<T, A9> f9, Codec.Field<T, A10> f10, Codec.Field<T, A11> f11, Codec.Field<T, A12> f12, Codec.Field<T, A13> f13, Codec.Field<T, A14> f14, Codec.Field<T, A15> f15, Codec.Field<T, A16> f16, Codec.Field<T, A17> f17, Codec.Field<T, A18> f18,
            Function18<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, T> builder) {
        return new Codec<>() {
            @Override
            public <D> T decode(DynamicOps<D> ops, D input) throws Codec.CodecException {
                Map<D, D> raw = ops.getMap(input).orElseThrow(() -> new Codec.CodecException("Expected map"));
                A1 v1 = f1.codec().decode(ops, raw.get(ops.createString(f1.name())));
                A2 v2 = f2.codec().decode(ops, raw.get(ops.createString(f2.name())));
                A3 v3 = f3.codec().decode(ops, raw.get(ops.createString(f3.name())));
                A4 v4 = f4.codec().decode(ops, raw.get(ops.createString(f4.name())));
                A5 v5 = f5.codec().decode(ops, raw.get(ops.createString(f5.name())));
                A6 v6 = f6.codec().decode(ops, raw.get(ops.createString(f6.name())));
                A7 v7 = f7.codec().decode(ops, raw.get(ops.createString(f7.name())));
                A8 v8 = f8.codec().decode(ops, raw.get(ops.createString(f8.name())));
                A9 v9 = f9.codec().decode(ops, raw.get(ops.createString(f9.name())));
                A10 v10 = f10.codec().decode(ops, raw.get(ops.createString(f10.name())));
                A11 v11 = f11.codec().decode(ops, raw.get(ops.createString(f11.name())));
                A12 v12 = f12.codec().decode(ops, raw.get(ops.createString(f12.name())));
                A13 v13 = f13.codec().decode(ops, raw.get(ops.createString(f13.name())));
                A14 v14 = f14.codec().decode(ops, raw.get(ops.createString(f14.name())));
                A15 v15 = f15.codec().decode(ops, raw.get(ops.createString(f15.name())));
                A16 v16 = f16.codec().decode(ops, raw.get(ops.createString(f16.name())));
                A17 v17 = f17.codec().decode(ops, raw.get(ops.createString(f17.name())));
                A18 v18 = f18.codec().decode(ops, raw.get(ops.createString(f18.name())));
                return builder.apply(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15, v16, v17, v18);
            }

            @Override
            public <D> D encode(DynamicOps<D> ops, T value) throws Codec.CodecException {
                Map<D, D> result = new LinkedHashMap<>();
                result.put(ops.createString(f1.name()), f1.codec().encode(ops, f1.getter().apply(value)));
                result.put(ops.createString(f2.name()), f2.codec().encode(ops, f2.getter().apply(value)));
                result.put(ops.createString(f3.name()), f3.codec().encode(ops, f3.getter().apply(value)));
                result.put(ops.createString(f4.name()), f4.codec().encode(ops, f4.getter().apply(value)));
                result.put(ops.createString(f5.name()), f5.codec().encode(ops, f5.getter().apply(value)));
                result.put(ops.createString(f6.name()), f6.codec().encode(ops, f6.getter().apply(value)));
                result.put(ops.createString(f7.name()), f7.codec().encode(ops, f7.getter().apply(value)));
                result.put(ops.createString(f8.name()), f8.codec().encode(ops, f8.getter().apply(value)));
                result.put(ops.createString(f9.name()), f9.codec().encode(ops, f9.getter().apply(value)));
                result.put(ops.createString(f10.name()), f10.codec().encode(ops, f10.getter().apply(value)));
                result.put(ops.createString(f11.name()), f11.codec().encode(ops, f11.getter().apply(value)));
                result.put(ops.createString(f12.name()), f12.codec().encode(ops, f12.getter().apply(value)));
                result.put(ops.createString(f13.name()), f13.codec().encode(ops, f13.getter().apply(value)));
                result.put(ops.createString(f14.name()), f14.codec().encode(ops, f14.getter().apply(value)));
                result.put(ops.createString(f15.name()), f15.codec().encode(ops, f15.getter().apply(value)));
                result.put(ops.createString(f16.name()), f16.codec().encode(ops, f16.getter().apply(value)));
                result.put(ops.createString(f17.name()), f17.codec().encode(ops, f17.getter().apply(value)));
                result.put(ops.createString(f18.name()), f18.codec().encode(ops, f18.getter().apply(value)));
                return ops.createMap(result);
            }
        };
    }

    public static <A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, T> Codec<T> create(
            Codec.Field<T, A1> f1, Codec.Field<T, A2> f2, Codec.Field<T, A3> f3, Codec.Field<T, A4> f4, Codec.Field<T, A5> f5, Codec.Field<T, A6> f6, Codec.Field<T, A7> f7, Codec.Field<T, A8> f8, Codec.Field<T, A9> f9, Codec.Field<T, A10> f10, Codec.Field<T, A11> f11, Codec.Field<T, A12> f12, Codec.Field<T, A13> f13, Codec.Field<T, A14> f14, Codec.Field<T, A15> f15, Codec.Field<T, A16> f16, Codec.Field<T, A17> f17, Codec.Field<T, A18> f18, Codec.Field<T, A19> f19,
            Function19<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, T> builder) {
        return new Codec<>() {
            @Override
            public <D> T decode(DynamicOps<D> ops, D input) throws Codec.CodecException {
                Map<D, D> raw = ops.getMap(input).orElseThrow(() -> new Codec.CodecException("Expected map"));
                A1 v1 = f1.codec().decode(ops, raw.get(ops.createString(f1.name())));
                A2 v2 = f2.codec().decode(ops, raw.get(ops.createString(f2.name())));
                A3 v3 = f3.codec().decode(ops, raw.get(ops.createString(f3.name())));
                A4 v4 = f4.codec().decode(ops, raw.get(ops.createString(f4.name())));
                A5 v5 = f5.codec().decode(ops, raw.get(ops.createString(f5.name())));
                A6 v6 = f6.codec().decode(ops, raw.get(ops.createString(f6.name())));
                A7 v7 = f7.codec().decode(ops, raw.get(ops.createString(f7.name())));
                A8 v8 = f8.codec().decode(ops, raw.get(ops.createString(f8.name())));
                A9 v9 = f9.codec().decode(ops, raw.get(ops.createString(f9.name())));
                A10 v10 = f10.codec().decode(ops, raw.get(ops.createString(f10.name())));
                A11 v11 = f11.codec().decode(ops, raw.get(ops.createString(f11.name())));
                A12 v12 = f12.codec().decode(ops, raw.get(ops.createString(f12.name())));
                A13 v13 = f13.codec().decode(ops, raw.get(ops.createString(f13.name())));
                A14 v14 = f14.codec().decode(ops, raw.get(ops.createString(f14.name())));
                A15 v15 = f15.codec().decode(ops, raw.get(ops.createString(f15.name())));
                A16 v16 = f16.codec().decode(ops, raw.get(ops.createString(f16.name())));
                A17 v17 = f17.codec().decode(ops, raw.get(ops.createString(f17.name())));
                A18 v18 = f18.codec().decode(ops, raw.get(ops.createString(f18.name())));
                A19 v19 = f19.codec().decode(ops, raw.get(ops.createString(f19.name())));
                return builder.apply(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15, v16, v17, v18, v19);
            }

            @Override
            public <D> D encode(DynamicOps<D> ops, T value) throws Codec.CodecException {
                Map<D, D> result = new LinkedHashMap<>();
                result.put(ops.createString(f1.name()), f1.codec().encode(ops, f1.getter().apply(value)));
                result.put(ops.createString(f2.name()), f2.codec().encode(ops, f2.getter().apply(value)));
                result.put(ops.createString(f3.name()), f3.codec().encode(ops, f3.getter().apply(value)));
                result.put(ops.createString(f4.name()), f4.codec().encode(ops, f4.getter().apply(value)));
                result.put(ops.createString(f5.name()), f5.codec().encode(ops, f5.getter().apply(value)));
                result.put(ops.createString(f6.name()), f6.codec().encode(ops, f6.getter().apply(value)));
                result.put(ops.createString(f7.name()), f7.codec().encode(ops, f7.getter().apply(value)));
                result.put(ops.createString(f8.name()), f8.codec().encode(ops, f8.getter().apply(value)));
                result.put(ops.createString(f9.name()), f9.codec().encode(ops, f9.getter().apply(value)));
                result.put(ops.createString(f10.name()), f10.codec().encode(ops, f10.getter().apply(value)));
                result.put(ops.createString(f11.name()), f11.codec().encode(ops, f11.getter().apply(value)));
                result.put(ops.createString(f12.name()), f12.codec().encode(ops, f12.getter().apply(value)));
                result.put(ops.createString(f13.name()), f13.codec().encode(ops, f13.getter().apply(value)));
                result.put(ops.createString(f14.name()), f14.codec().encode(ops, f14.getter().apply(value)));
                result.put(ops.createString(f15.name()), f15.codec().encode(ops, f15.getter().apply(value)));
                result.put(ops.createString(f16.name()), f16.codec().encode(ops, f16.getter().apply(value)));
                result.put(ops.createString(f17.name()), f17.codec().encode(ops, f17.getter().apply(value)));
                result.put(ops.createString(f18.name()), f18.codec().encode(ops, f18.getter().apply(value)));
                result.put(ops.createString(f19.name()), f19.codec().encode(ops, f19.getter().apply(value)));
                return ops.createMap(result);
            }
        };
    }

    public static <A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, T> Codec<T> create(
            Codec.Field<T, A1> f1, Codec.Field<T, A2> f2, Codec.Field<T, A3> f3, Codec.Field<T, A4> f4, Codec.Field<T, A5> f5, Codec.Field<T, A6> f6, Codec.Field<T, A7> f7, Codec.Field<T, A8> f8, Codec.Field<T, A9> f9, Codec.Field<T, A10> f10, Codec.Field<T, A11> f11, Codec.Field<T, A12> f12, Codec.Field<T, A13> f13, Codec.Field<T, A14> f14, Codec.Field<T, A15> f15, Codec.Field<T, A16> f16, Codec.Field<T, A17> f17, Codec.Field<T, A18> f18, Codec.Field<T, A19> f19, Codec.Field<T, A20> f20,
            Function20<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, T> builder) {
        return new Codec<>() {
            @Override
            public <D> T decode(DynamicOps<D> ops, D input) throws Codec.CodecException {
                Map<D, D> raw = ops.getMap(input).orElseThrow(() -> new Codec.CodecException("Expected map"));
                A1 v1 = f1.codec().decode(ops, raw.get(ops.createString(f1.name())));
                A2 v2 = f2.codec().decode(ops, raw.get(ops.createString(f2.name())));
                A3 v3 = f3.codec().decode(ops, raw.get(ops.createString(f3.name())));
                A4 v4 = f4.codec().decode(ops, raw.get(ops.createString(f4.name())));
                A5 v5 = f5.codec().decode(ops, raw.get(ops.createString(f5.name())));
                A6 v6 = f6.codec().decode(ops, raw.get(ops.createString(f6.name())));
                A7 v7 = f7.codec().decode(ops, raw.get(ops.createString(f7.name())));
                A8 v8 = f8.codec().decode(ops, raw.get(ops.createString(f8.name())));
                A9 v9 = f9.codec().decode(ops, raw.get(ops.createString(f9.name())));
                A10 v10 = f10.codec().decode(ops, raw.get(ops.createString(f10.name())));
                A11 v11 = f11.codec().decode(ops, raw.get(ops.createString(f11.name())));
                A12 v12 = f12.codec().decode(ops, raw.get(ops.createString(f12.name())));
                A13 v13 = f13.codec().decode(ops, raw.get(ops.createString(f13.name())));
                A14 v14 = f14.codec().decode(ops, raw.get(ops.createString(f14.name())));
                A15 v15 = f15.codec().decode(ops, raw.get(ops.createString(f15.name())));
                A16 v16 = f16.codec().decode(ops, raw.get(ops.createString(f16.name())));
                A17 v17 = f17.codec().decode(ops, raw.get(ops.createString(f17.name())));
                A18 v18 = f18.codec().decode(ops, raw.get(ops.createString(f18.name())));
                A19 v19 = f19.codec().decode(ops, raw.get(ops.createString(f19.name())));
                A20 v20 = f20.codec().decode(ops, raw.get(ops.createString(f20.name())));
                return builder.apply(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15, v16, v17, v18, v19, v20);
            }

            @Override
            public <D> D encode(DynamicOps<D> ops, T value) throws Codec.CodecException {
                Map<D, D> result = new LinkedHashMap<>();
                result.put(ops.createString(f1.name()), f1.codec().encode(ops, f1.getter().apply(value)));
                result.put(ops.createString(f2.name()), f2.codec().encode(ops, f2.getter().apply(value)));
                result.put(ops.createString(f3.name()), f3.codec().encode(ops, f3.getter().apply(value)));
                result.put(ops.createString(f4.name()), f4.codec().encode(ops, f4.getter().apply(value)));
                result.put(ops.createString(f5.name()), f5.codec().encode(ops, f5.getter().apply(value)));
                result.put(ops.createString(f6.name()), f6.codec().encode(ops, f6.getter().apply(value)));
                result.put(ops.createString(f7.name()), f7.codec().encode(ops, f7.getter().apply(value)));
                result.put(ops.createString(f8.name()), f8.codec().encode(ops, f8.getter().apply(value)));
                result.put(ops.createString(f9.name()), f9.codec().encode(ops, f9.getter().apply(value)));
                result.put(ops.createString(f10.name()), f10.codec().encode(ops, f10.getter().apply(value)));
                result.put(ops.createString(f11.name()), f11.codec().encode(ops, f11.getter().apply(value)));
                result.put(ops.createString(f12.name()), f12.codec().encode(ops, f12.getter().apply(value)));
                result.put(ops.createString(f13.name()), f13.codec().encode(ops, f13.getter().apply(value)));
                result.put(ops.createString(f14.name()), f14.codec().encode(ops, f14.getter().apply(value)));
                result.put(ops.createString(f15.name()), f15.codec().encode(ops, f15.getter().apply(value)));
                result.put(ops.createString(f16.name()), f16.codec().encode(ops, f16.getter().apply(value)));
                result.put(ops.createString(f17.name()), f17.codec().encode(ops, f17.getter().apply(value)));
                result.put(ops.createString(f18.name()), f18.codec().encode(ops, f18.getter().apply(value)));
                result.put(ops.createString(f19.name()), f19.codec().encode(ops, f19.getter().apply(value)));
                result.put(ops.createString(f20.name()), f20.codec().encode(ops, f20.getter().apply(value)));
                return ops.createMap(result);
            }
        };
    }

    public static <A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21, T> Codec<T> create(
            Codec.Field<T, A1> f1, Codec.Field<T, A2> f2, Codec.Field<T, A3> f3, Codec.Field<T, A4> f4, Codec.Field<T, A5> f5, Codec.Field<T, A6> f6, Codec.Field<T, A7> f7, Codec.Field<T, A8> f8, Codec.Field<T, A9> f9, Codec.Field<T, A10> f10, Codec.Field<T, A11> f11, Codec.Field<T, A12> f12, Codec.Field<T, A13> f13, Codec.Field<T, A14> f14, Codec.Field<T, A15> f15, Codec.Field<T, A16> f16, Codec.Field<T, A17> f17, Codec.Field<T, A18> f18, Codec.Field<T, A19> f19, Codec.Field<T, A20> f20, Codec.Field<T, A21> f21,
            Function21<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21, T> builder) {
        return new Codec<>() {
            @Override
            public <D> T decode(DynamicOps<D> ops, D input) throws Codec.CodecException {
                Map<D, D> raw = ops.getMap(input).orElseThrow(() -> new Codec.CodecException("Expected map"));
                A1 v1 = f1.codec().decode(ops, raw.get(ops.createString(f1.name())));
                A2 v2 = f2.codec().decode(ops, raw.get(ops.createString(f2.name())));
                A3 v3 = f3.codec().decode(ops, raw.get(ops.createString(f3.name())));
                A4 v4 = f4.codec().decode(ops, raw.get(ops.createString(f4.name())));
                A5 v5 = f5.codec().decode(ops, raw.get(ops.createString(f5.name())));
                A6 v6 = f6.codec().decode(ops, raw.get(ops.createString(f6.name())));
                A7 v7 = f7.codec().decode(ops, raw.get(ops.createString(f7.name())));
                A8 v8 = f8.codec().decode(ops, raw.get(ops.createString(f8.name())));
                A9 v9 = f9.codec().decode(ops, raw.get(ops.createString(f9.name())));
                A10 v10 = f10.codec().decode(ops, raw.get(ops.createString(f10.name())));
                A11 v11 = f11.codec().decode(ops, raw.get(ops.createString(f11.name())));
                A12 v12 = f12.codec().decode(ops, raw.get(ops.createString(f12.name())));
                A13 v13 = f13.codec().decode(ops, raw.get(ops.createString(f13.name())));
                A14 v14 = f14.codec().decode(ops, raw.get(ops.createString(f14.name())));
                A15 v15 = f15.codec().decode(ops, raw.get(ops.createString(f15.name())));
                A16 v16 = f16.codec().decode(ops, raw.get(ops.createString(f16.name())));
                A17 v17 = f17.codec().decode(ops, raw.get(ops.createString(f17.name())));
                A18 v18 = f18.codec().decode(ops, raw.get(ops.createString(f18.name())));
                A19 v19 = f19.codec().decode(ops, raw.get(ops.createString(f19.name())));
                A20 v20 = f20.codec().decode(ops, raw.get(ops.createString(f20.name())));
                A21 v21 = f21.codec().decode(ops, raw.get(ops.createString(f21.name())));
                return builder.apply(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15, v16, v17, v18, v19, v20, v21);
            }

            @Override
            public <D> D encode(DynamicOps<D> ops, T value) throws Codec.CodecException {
                Map<D, D> result = new LinkedHashMap<>();
                result.put(ops.createString(f1.name()), f1.codec().encode(ops, f1.getter().apply(value)));
                result.put(ops.createString(f2.name()), f2.codec().encode(ops, f2.getter().apply(value)));
                result.put(ops.createString(f3.name()), f3.codec().encode(ops, f3.getter().apply(value)));
                result.put(ops.createString(f4.name()), f4.codec().encode(ops, f4.getter().apply(value)));
                result.put(ops.createString(f5.name()), f5.codec().encode(ops, f5.getter().apply(value)));
                result.put(ops.createString(f6.name()), f6.codec().encode(ops, f6.getter().apply(value)));
                result.put(ops.createString(f7.name()), f7.codec().encode(ops, f7.getter().apply(value)));
                result.put(ops.createString(f8.name()), f8.codec().encode(ops, f8.getter().apply(value)));
                result.put(ops.createString(f9.name()), f9.codec().encode(ops, f9.getter().apply(value)));
                result.put(ops.createString(f10.name()), f10.codec().encode(ops, f10.getter().apply(value)));
                result.put(ops.createString(f11.name()), f11.codec().encode(ops, f11.getter().apply(value)));
                result.put(ops.createString(f12.name()), f12.codec().encode(ops, f12.getter().apply(value)));
                result.put(ops.createString(f13.name()), f13.codec().encode(ops, f13.getter().apply(value)));
                result.put(ops.createString(f14.name()), f14.codec().encode(ops, f14.getter().apply(value)));
                result.put(ops.createString(f15.name()), f15.codec().encode(ops, f15.getter().apply(value)));
                result.put(ops.createString(f16.name()), f16.codec().encode(ops, f16.getter().apply(value)));
                result.put(ops.createString(f17.name()), f17.codec().encode(ops, f17.getter().apply(value)));
                result.put(ops.createString(f18.name()), f18.codec().encode(ops, f18.getter().apply(value)));
                result.put(ops.createString(f19.name()), f19.codec().encode(ops, f19.getter().apply(value)));
                result.put(ops.createString(f20.name()), f20.codec().encode(ops, f20.getter().apply(value)));
                result.put(ops.createString(f21.name()), f21.codec().encode(ops, f21.getter().apply(value)));
                return ops.createMap(result);
            }
        };
    }

    public static <A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21, A22, T> Codec<T> create(
            Codec.Field<T, A1> f1, Codec.Field<T, A2> f2, Codec.Field<T, A3> f3, Codec.Field<T, A4> f4, Codec.Field<T, A5> f5, Codec.Field<T, A6> f6, Codec.Field<T, A7> f7, Codec.Field<T, A8> f8, Codec.Field<T, A9> f9, Codec.Field<T, A10> f10, Codec.Field<T, A11> f11, Codec.Field<T, A12> f12, Codec.Field<T, A13> f13, Codec.Field<T, A14> f14, Codec.Field<T, A15> f15, Codec.Field<T, A16> f16, Codec.Field<T, A17> f17, Codec.Field<T, A18> f18, Codec.Field<T, A19> f19, Codec.Field<T, A20> f20, Codec.Field<T, A21> f21, Codec.Field<T, A22> f22,
            Function22<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21, A22, T> builder) {
        return new Codec<>() {
            @Override
            public <D> T decode(DynamicOps<D> ops, D input) throws Codec.CodecException {
                Map<D, D> raw = ops.getMap(input).orElseThrow(() -> new Codec.CodecException("Expected map"));
                A1 v1 = f1.codec().decode(ops, raw.get(ops.createString(f1.name())));
                A2 v2 = f2.codec().decode(ops, raw.get(ops.createString(f2.name())));
                A3 v3 = f3.codec().decode(ops, raw.get(ops.createString(f3.name())));
                A4 v4 = f4.codec().decode(ops, raw.get(ops.createString(f4.name())));
                A5 v5 = f5.codec().decode(ops, raw.get(ops.createString(f5.name())));
                A6 v6 = f6.codec().decode(ops, raw.get(ops.createString(f6.name())));
                A7 v7 = f7.codec().decode(ops, raw.get(ops.createString(f7.name())));
                A8 v8 = f8.codec().decode(ops, raw.get(ops.createString(f8.name())));
                A9 v9 = f9.codec().decode(ops, raw.get(ops.createString(f9.name())));
                A10 v10 = f10.codec().decode(ops, raw.get(ops.createString(f10.name())));
                A11 v11 = f11.codec().decode(ops, raw.get(ops.createString(f11.name())));
                A12 v12 = f12.codec().decode(ops, raw.get(ops.createString(f12.name())));
                A13 v13 = f13.codec().decode(ops, raw.get(ops.createString(f13.name())));
                A14 v14 = f14.codec().decode(ops, raw.get(ops.createString(f14.name())));
                A15 v15 = f15.codec().decode(ops, raw.get(ops.createString(f15.name())));
                A16 v16 = f16.codec().decode(ops, raw.get(ops.createString(f16.name())));
                A17 v17 = f17.codec().decode(ops, raw.get(ops.createString(f17.name())));
                A18 v18 = f18.codec().decode(ops, raw.get(ops.createString(f18.name())));
                A19 v19 = f19.codec().decode(ops, raw.get(ops.createString(f19.name())));
                A20 v20 = f20.codec().decode(ops, raw.get(ops.createString(f20.name())));
                A21 v21 = f21.codec().decode(ops, raw.get(ops.createString(f21.name())));
                A22 v22 = f22.codec().decode(ops, raw.get(ops.createString(f22.name())));
                return builder.apply(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15, v16, v17, v18, v19, v20, v21, v22);
            }

            @Override
            public <D> D encode(DynamicOps<D> ops, T value) throws Codec.CodecException {
                Map<D, D> result = new LinkedHashMap<>();
                result.put(ops.createString(f1.name()), f1.codec().encode(ops, f1.getter().apply(value)));
                result.put(ops.createString(f2.name()), f2.codec().encode(ops, f2.getter().apply(value)));
                result.put(ops.createString(f3.name()), f3.codec().encode(ops, f3.getter().apply(value)));
                result.put(ops.createString(f4.name()), f4.codec().encode(ops, f4.getter().apply(value)));
                result.put(ops.createString(f5.name()), f5.codec().encode(ops, f5.getter().apply(value)));
                result.put(ops.createString(f6.name()), f6.codec().encode(ops, f6.getter().apply(value)));
                result.put(ops.createString(f7.name()), f7.codec().encode(ops, f7.getter().apply(value)));
                result.put(ops.createString(f8.name()), f8.codec().encode(ops, f8.getter().apply(value)));
                result.put(ops.createString(f9.name()), f9.codec().encode(ops, f9.getter().apply(value)));
                result.put(ops.createString(f10.name()), f10.codec().encode(ops, f10.getter().apply(value)));
                result.put(ops.createString(f11.name()), f11.codec().encode(ops, f11.getter().apply(value)));
                result.put(ops.createString(f12.name()), f12.codec().encode(ops, f12.getter().apply(value)));
                result.put(ops.createString(f13.name()), f13.codec().encode(ops, f13.getter().apply(value)));
                result.put(ops.createString(f14.name()), f14.codec().encode(ops, f14.getter().apply(value)));
                result.put(ops.createString(f15.name()), f15.codec().encode(ops, f15.getter().apply(value)));
                result.put(ops.createString(f16.name()), f16.codec().encode(ops, f16.getter().apply(value)));
                result.put(ops.createString(f17.name()), f17.codec().encode(ops, f17.getter().apply(value)));
                result.put(ops.createString(f18.name()), f18.codec().encode(ops, f18.getter().apply(value)));
                result.put(ops.createString(f19.name()), f19.codec().encode(ops, f19.getter().apply(value)));
                result.put(ops.createString(f20.name()), f20.codec().encode(ops, f20.getter().apply(value)));
                result.put(ops.createString(f21.name()), f21.codec().encode(ops, f21.getter().apply(value)));
                result.put(ops.createString(f22.name()), f22.codec().encode(ops, f22.getter().apply(value)));
                return ops.createMap(result);
            }
        };
    }

    public static <A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21, A22, A23, T> Codec<T> create(
            Codec.Field<T, A1> f1, Codec.Field<T, A2> f2, Codec.Field<T, A3> f3, Codec.Field<T, A4> f4, Codec.Field<T, A5> f5, Codec.Field<T, A6> f6, Codec.Field<T, A7> f7, Codec.Field<T, A8> f8, Codec.Field<T, A9> f9, Codec.Field<T, A10> f10, Codec.Field<T, A11> f11, Codec.Field<T, A12> f12, Codec.Field<T, A13> f13, Codec.Field<T, A14> f14, Codec.Field<T, A15> f15, Codec.Field<T, A16> f16, Codec.Field<T, A17> f17, Codec.Field<T, A18> f18, Codec.Field<T, A19> f19, Codec.Field<T, A20> f20, Codec.Field<T, A21> f21, Codec.Field<T, A22> f22, Codec.Field<T, A23> f23,
            Function23<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21, A22, A23, T> builder) {
        return new Codec<>() {
            @Override
            public <D> T decode(DynamicOps<D> ops, D input) throws Codec.CodecException {
                Map<D, D> raw = ops.getMap(input).orElseThrow(() -> new Codec.CodecException("Expected map"));
                A1 v1 = f1.codec().decode(ops, raw.get(ops.createString(f1.name())));
                A2 v2 = f2.codec().decode(ops, raw.get(ops.createString(f2.name())));
                A3 v3 = f3.codec().decode(ops, raw.get(ops.createString(f3.name())));
                A4 v4 = f4.codec().decode(ops, raw.get(ops.createString(f4.name())));
                A5 v5 = f5.codec().decode(ops, raw.get(ops.createString(f5.name())));
                A6 v6 = f6.codec().decode(ops, raw.get(ops.createString(f6.name())));
                A7 v7 = f7.codec().decode(ops, raw.get(ops.createString(f7.name())));
                A8 v8 = f8.codec().decode(ops, raw.get(ops.createString(f8.name())));
                A9 v9 = f9.codec().decode(ops, raw.get(ops.createString(f9.name())));
                A10 v10 = f10.codec().decode(ops, raw.get(ops.createString(f10.name())));
                A11 v11 = f11.codec().decode(ops, raw.get(ops.createString(f11.name())));
                A12 v12 = f12.codec().decode(ops, raw.get(ops.createString(f12.name())));
                A13 v13 = f13.codec().decode(ops, raw.get(ops.createString(f13.name())));
                A14 v14 = f14.codec().decode(ops, raw.get(ops.createString(f14.name())));
                A15 v15 = f15.codec().decode(ops, raw.get(ops.createString(f15.name())));
                A16 v16 = f16.codec().decode(ops, raw.get(ops.createString(f16.name())));
                A17 v17 = f17.codec().decode(ops, raw.get(ops.createString(f17.name())));
                A18 v18 = f18.codec().decode(ops, raw.get(ops.createString(f18.name())));
                A19 v19 = f19.codec().decode(ops, raw.get(ops.createString(f19.name())));
                A20 v20 = f20.codec().decode(ops, raw.get(ops.createString(f20.name())));
                A21 v21 = f21.codec().decode(ops, raw.get(ops.createString(f21.name())));
                A22 v22 = f22.codec().decode(ops, raw.get(ops.createString(f22.name())));
                A23 v23 = f23.codec().decode(ops, raw.get(ops.createString(f23.name())));
                return builder.apply(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15, v16, v17, v18, v19, v20, v21, v22, v23);
            }

            @Override
            public <D> D encode(DynamicOps<D> ops, T value) throws Codec.CodecException {
                Map<D, D> result = new LinkedHashMap<>();
                result.put(ops.createString(f1.name()), f1.codec().encode(ops, f1.getter().apply(value)));
                result.put(ops.createString(f2.name()), f2.codec().encode(ops, f2.getter().apply(value)));
                result.put(ops.createString(f3.name()), f3.codec().encode(ops, f3.getter().apply(value)));
                result.put(ops.createString(f4.name()), f4.codec().encode(ops, f4.getter().apply(value)));
                result.put(ops.createString(f5.name()), f5.codec().encode(ops, f5.getter().apply(value)));
                result.put(ops.createString(f6.name()), f6.codec().encode(ops, f6.getter().apply(value)));
                result.put(ops.createString(f7.name()), f7.codec().encode(ops, f7.getter().apply(value)));
                result.put(ops.createString(f8.name()), f8.codec().encode(ops, f8.getter().apply(value)));
                result.put(ops.createString(f9.name()), f9.codec().encode(ops, f9.getter().apply(value)));
                result.put(ops.createString(f10.name()), f10.codec().encode(ops, f10.getter().apply(value)));
                result.put(ops.createString(f11.name()), f11.codec().encode(ops, f11.getter().apply(value)));
                result.put(ops.createString(f12.name()), f12.codec().encode(ops, f12.getter().apply(value)));
                result.put(ops.createString(f13.name()), f13.codec().encode(ops, f13.getter().apply(value)));
                result.put(ops.createString(f14.name()), f14.codec().encode(ops, f14.getter().apply(value)));
                result.put(ops.createString(f15.name()), f15.codec().encode(ops, f15.getter().apply(value)));
                result.put(ops.createString(f16.name()), f16.codec().encode(ops, f16.getter().apply(value)));
                result.put(ops.createString(f17.name()), f17.codec().encode(ops, f17.getter().apply(value)));
                result.put(ops.createString(f18.name()), f18.codec().encode(ops, f18.getter().apply(value)));
                result.put(ops.createString(f19.name()), f19.codec().encode(ops, f19.getter().apply(value)));
                result.put(ops.createString(f20.name()), f20.codec().encode(ops, f20.getter().apply(value)));
                result.put(ops.createString(f21.name()), f21.codec().encode(ops, f21.getter().apply(value)));
                result.put(ops.createString(f22.name()), f22.codec().encode(ops, f22.getter().apply(value)));
                result.put(ops.createString(f23.name()), f23.codec().encode(ops, f23.getter().apply(value)));
                return ops.createMap(result);
            }
        };
    }

    public static <A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21, A22, A23, A24, T> Codec<T> create(
            Codec.Field<T, A1> f1, Codec.Field<T, A2> f2, Codec.Field<T, A3> f3, Codec.Field<T, A4> f4, Codec.Field<T, A5> f5, Codec.Field<T, A6> f6, Codec.Field<T, A7> f7, Codec.Field<T, A8> f8, Codec.Field<T, A9> f9, Codec.Field<T, A10> f10, Codec.Field<T, A11> f11, Codec.Field<T, A12> f12, Codec.Field<T, A13> f13, Codec.Field<T, A14> f14, Codec.Field<T, A15> f15, Codec.Field<T, A16> f16, Codec.Field<T, A17> f17, Codec.Field<T, A18> f18, Codec.Field<T, A19> f19, Codec.Field<T, A20> f20, Codec.Field<T, A21> f21, Codec.Field<T, A22> f22, Codec.Field<T, A23> f23, Codec.Field<T, A24> f24,
            Function24<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21, A22, A23, A24, T> builder) {
        return new Codec<>() {
            @Override
            public <D> T decode(DynamicOps<D> ops, D input) throws Codec.CodecException {
                Map<D, D> raw = ops.getMap(input).orElseThrow(() -> new Codec.CodecException("Expected map"));
                A1 v1 = f1.codec().decode(ops, raw.get(ops.createString(f1.name())));
                A2 v2 = f2.codec().decode(ops, raw.get(ops.createString(f2.name())));
                A3 v3 = f3.codec().decode(ops, raw.get(ops.createString(f3.name())));
                A4 v4 = f4.codec().decode(ops, raw.get(ops.createString(f4.name())));
                A5 v5 = f5.codec().decode(ops, raw.get(ops.createString(f5.name())));
                A6 v6 = f6.codec().decode(ops, raw.get(ops.createString(f6.name())));
                A7 v7 = f7.codec().decode(ops, raw.get(ops.createString(f7.name())));
                A8 v8 = f8.codec().decode(ops, raw.get(ops.createString(f8.name())));
                A9 v9 = f9.codec().decode(ops, raw.get(ops.createString(f9.name())));
                A10 v10 = f10.codec().decode(ops, raw.get(ops.createString(f10.name())));
                A11 v11 = f11.codec().decode(ops, raw.get(ops.createString(f11.name())));
                A12 v12 = f12.codec().decode(ops, raw.get(ops.createString(f12.name())));
                A13 v13 = f13.codec().decode(ops, raw.get(ops.createString(f13.name())));
                A14 v14 = f14.codec().decode(ops, raw.get(ops.createString(f14.name())));
                A15 v15 = f15.codec().decode(ops, raw.get(ops.createString(f15.name())));
                A16 v16 = f16.codec().decode(ops, raw.get(ops.createString(f16.name())));
                A17 v17 = f17.codec().decode(ops, raw.get(ops.createString(f17.name())));
                A18 v18 = f18.codec().decode(ops, raw.get(ops.createString(f18.name())));
                A19 v19 = f19.codec().decode(ops, raw.get(ops.createString(f19.name())));
                A20 v20 = f20.codec().decode(ops, raw.get(ops.createString(f20.name())));
                A21 v21 = f21.codec().decode(ops, raw.get(ops.createString(f21.name())));
                A22 v22 = f22.codec().decode(ops, raw.get(ops.createString(f22.name())));
                A23 v23 = f23.codec().decode(ops, raw.get(ops.createString(f23.name())));
                A24 v24 = f24.codec().decode(ops, raw.get(ops.createString(f24.name())));
                return builder.apply(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15, v16, v17, v18, v19, v20, v21, v22, v23, v24);
            }

            @Override
            public <D> D encode(DynamicOps<D> ops, T value) throws Codec.CodecException {
                Map<D, D> result = new LinkedHashMap<>();
                result.put(ops.createString(f1.name()), f1.codec().encode(ops, f1.getter().apply(value)));
                result.put(ops.createString(f2.name()), f2.codec().encode(ops, f2.getter().apply(value)));
                result.put(ops.createString(f3.name()), f3.codec().encode(ops, f3.getter().apply(value)));
                result.put(ops.createString(f4.name()), f4.codec().encode(ops, f4.getter().apply(value)));
                result.put(ops.createString(f5.name()), f5.codec().encode(ops, f5.getter().apply(value)));
                result.put(ops.createString(f6.name()), f6.codec().encode(ops, f6.getter().apply(value)));
                result.put(ops.createString(f7.name()), f7.codec().encode(ops, f7.getter().apply(value)));
                result.put(ops.createString(f8.name()), f8.codec().encode(ops, f8.getter().apply(value)));
                result.put(ops.createString(f9.name()), f9.codec().encode(ops, f9.getter().apply(value)));
                result.put(ops.createString(f10.name()), f10.codec().encode(ops, f10.getter().apply(value)));
                result.put(ops.createString(f11.name()), f11.codec().encode(ops, f11.getter().apply(value)));
                result.put(ops.createString(f12.name()), f12.codec().encode(ops, f12.getter().apply(value)));
                result.put(ops.createString(f13.name()), f13.codec().encode(ops, f13.getter().apply(value)));
                result.put(ops.createString(f14.name()), f14.codec().encode(ops, f14.getter().apply(value)));
                result.put(ops.createString(f15.name()), f15.codec().encode(ops, f15.getter().apply(value)));
                result.put(ops.createString(f16.name()), f16.codec().encode(ops, f16.getter().apply(value)));
                result.put(ops.createString(f17.name()), f17.codec().encode(ops, f17.getter().apply(value)));
                result.put(ops.createString(f18.name()), f18.codec().encode(ops, f18.getter().apply(value)));
                result.put(ops.createString(f19.name()), f19.codec().encode(ops, f19.getter().apply(value)));
                result.put(ops.createString(f20.name()), f20.codec().encode(ops, f20.getter().apply(value)));
                result.put(ops.createString(f21.name()), f21.codec().encode(ops, f21.getter().apply(value)));
                result.put(ops.createString(f22.name()), f22.codec().encode(ops, f22.getter().apply(value)));
                result.put(ops.createString(f23.name()), f23.codec().encode(ops, f23.getter().apply(value)));
                result.put(ops.createString(f24.name()), f24.codec().encode(ops, f24.getter().apply(value)));
                return ops.createMap(result);
            }
        };
    }

    public static <A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21, A22, A23, A24, A25, T> Codec<T> create(
            Codec.Field<T, A1> f1, Codec.Field<T, A2> f2, Codec.Field<T, A3> f3, Codec.Field<T, A4> f4, Codec.Field<T, A5> f5, Codec.Field<T, A6> f6, Codec.Field<T, A7> f7, Codec.Field<T, A8> f8, Codec.Field<T, A9> f9, Codec.Field<T, A10> f10, Codec.Field<T, A11> f11, Codec.Field<T, A12> f12, Codec.Field<T, A13> f13, Codec.Field<T, A14> f14, Codec.Field<T, A15> f15, Codec.Field<T, A16> f16, Codec.Field<T, A17> f17, Codec.Field<T, A18> f18, Codec.Field<T, A19> f19, Codec.Field<T, A20> f20, Codec.Field<T, A21> f21, Codec.Field<T, A22> f22, Codec.Field<T, A23> f23, Codec.Field<T, A24> f24, Codec.Field<T, A25> f25,
            Function25<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21, A22, A23, A24, A25, T> builder) {
        return new Codec<>() {
            @Override
            public <D> T decode(DynamicOps<D> ops, D input) throws Codec.CodecException {
                Map<D, D> raw = ops.getMap(input).orElseThrow(() -> new Codec.CodecException("Expected map"));
                A1 v1 = f1.codec().decode(ops, raw.get(ops.createString(f1.name())));
                A2 v2 = f2.codec().decode(ops, raw.get(ops.createString(f2.name())));
                A3 v3 = f3.codec().decode(ops, raw.get(ops.createString(f3.name())));
                A4 v4 = f4.codec().decode(ops, raw.get(ops.createString(f4.name())));
                A5 v5 = f5.codec().decode(ops, raw.get(ops.createString(f5.name())));
                A6 v6 = f6.codec().decode(ops, raw.get(ops.createString(f6.name())));
                A7 v7 = f7.codec().decode(ops, raw.get(ops.createString(f7.name())));
                A8 v8 = f8.codec().decode(ops, raw.get(ops.createString(f8.name())));
                A9 v9 = f9.codec().decode(ops, raw.get(ops.createString(f9.name())));
                A10 v10 = f10.codec().decode(ops, raw.get(ops.createString(f10.name())));
                A11 v11 = f11.codec().decode(ops, raw.get(ops.createString(f11.name())));
                A12 v12 = f12.codec().decode(ops, raw.get(ops.createString(f12.name())));
                A13 v13 = f13.codec().decode(ops, raw.get(ops.createString(f13.name())));
                A14 v14 = f14.codec().decode(ops, raw.get(ops.createString(f14.name())));
                A15 v15 = f15.codec().decode(ops, raw.get(ops.createString(f15.name())));
                A16 v16 = f16.codec().decode(ops, raw.get(ops.createString(f16.name())));
                A17 v17 = f17.codec().decode(ops, raw.get(ops.createString(f17.name())));
                A18 v18 = f18.codec().decode(ops, raw.get(ops.createString(f18.name())));
                A19 v19 = f19.codec().decode(ops, raw.get(ops.createString(f19.name())));
                A20 v20 = f20.codec().decode(ops, raw.get(ops.createString(f20.name())));
                A21 v21 = f21.codec().decode(ops, raw.get(ops.createString(f21.name())));
                A22 v22 = f22.codec().decode(ops, raw.get(ops.createString(f22.name())));
                A23 v23 = f23.codec().decode(ops, raw.get(ops.createString(f23.name())));
                A24 v24 = f24.codec().decode(ops, raw.get(ops.createString(f24.name())));
                A25 v25 = f25.codec().decode(ops, raw.get(ops.createString(f25.name())));
                return builder.apply(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15, v16, v17, v18, v19, v20, v21, v22, v23, v24, v25);
            }

            @Override
            public <D> D encode(DynamicOps<D> ops, T value) throws Codec.CodecException {
                Map<D, D> result = new LinkedHashMap<>();
                result.put(ops.createString(f1.name()), f1.codec().encode(ops, f1.getter().apply(value)));
                result.put(ops.createString(f2.name()), f2.codec().encode(ops, f2.getter().apply(value)));
                result.put(ops.createString(f3.name()), f3.codec().encode(ops, f3.getter().apply(value)));
                result.put(ops.createString(f4.name()), f4.codec().encode(ops, f4.getter().apply(value)));
                result.put(ops.createString(f5.name()), f5.codec().encode(ops, f5.getter().apply(value)));
                result.put(ops.createString(f6.name()), f6.codec().encode(ops, f6.getter().apply(value)));
                result.put(ops.createString(f7.name()), f7.codec().encode(ops, f7.getter().apply(value)));
                result.put(ops.createString(f8.name()), f8.codec().encode(ops, f8.getter().apply(value)));
                result.put(ops.createString(f9.name()), f9.codec().encode(ops, f9.getter().apply(value)));
                result.put(ops.createString(f10.name()), f10.codec().encode(ops, f10.getter().apply(value)));
                result.put(ops.createString(f11.name()), f11.codec().encode(ops, f11.getter().apply(value)));
                result.put(ops.createString(f12.name()), f12.codec().encode(ops, f12.getter().apply(value)));
                result.put(ops.createString(f13.name()), f13.codec().encode(ops, f13.getter().apply(value)));
                result.put(ops.createString(f14.name()), f14.codec().encode(ops, f14.getter().apply(value)));
                result.put(ops.createString(f15.name()), f15.codec().encode(ops, f15.getter().apply(value)));
                result.put(ops.createString(f16.name()), f16.codec().encode(ops, f16.getter().apply(value)));
                result.put(ops.createString(f17.name()), f17.codec().encode(ops, f17.getter().apply(value)));
                result.put(ops.createString(f18.name()), f18.codec().encode(ops, f18.getter().apply(value)));
                result.put(ops.createString(f19.name()), f19.codec().encode(ops, f19.getter().apply(value)));
                result.put(ops.createString(f20.name()), f20.codec().encode(ops, f20.getter().apply(value)));
                result.put(ops.createString(f21.name()), f21.codec().encode(ops, f21.getter().apply(value)));
                result.put(ops.createString(f22.name()), f22.codec().encode(ops, f22.getter().apply(value)));
                result.put(ops.createString(f23.name()), f23.codec().encode(ops, f23.getter().apply(value)));
                result.put(ops.createString(f24.name()), f24.codec().encode(ops, f24.getter().apply(value)));
                result.put(ops.createString(f25.name()), f25.codec().encode(ops, f25.getter().apply(value)));
                return ops.createMap(result);
            }
        };
    }

    public static <A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21, A22, A23, A24, A25, A26, T> Codec<T> create(
            Codec.Field<T, A1> f1, Codec.Field<T, A2> f2, Codec.Field<T, A3> f3, Codec.Field<T, A4> f4, Codec.Field<T, A5> f5, Codec.Field<T, A6> f6, Codec.Field<T, A7> f7, Codec.Field<T, A8> f8, Codec.Field<T, A9> f9, Codec.Field<T, A10> f10, Codec.Field<T, A11> f11, Codec.Field<T, A12> f12, Codec.Field<T, A13> f13, Codec.Field<T, A14> f14, Codec.Field<T, A15> f15, Codec.Field<T, A16> f16, Codec.Field<T, A17> f17, Codec.Field<T, A18> f18, Codec.Field<T, A19> f19, Codec.Field<T, A20> f20, Codec.Field<T, A21> f21, Codec.Field<T, A22> f22, Codec.Field<T, A23> f23, Codec.Field<T, A24> f24, Codec.Field<T, A25> f25, Codec.Field<T, A26> f26,
            Function26<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21, A22, A23, A24, A25, A26, T> builder) {
        return new Codec<>() {
            @Override
            public <D> T decode(DynamicOps<D> ops, D input) throws Codec.CodecException {
                Map<D, D> raw = ops.getMap(input).orElseThrow(() -> new Codec.CodecException("Expected map"));
                A1 v1 = f1.codec().decode(ops, raw.get(ops.createString(f1.name())));
                A2 v2 = f2.codec().decode(ops, raw.get(ops.createString(f2.name())));
                A3 v3 = f3.codec().decode(ops, raw.get(ops.createString(f3.name())));
                A4 v4 = f4.codec().decode(ops, raw.get(ops.createString(f4.name())));
                A5 v5 = f5.codec().decode(ops, raw.get(ops.createString(f5.name())));
                A6 v6 = f6.codec().decode(ops, raw.get(ops.createString(f6.name())));
                A7 v7 = f7.codec().decode(ops, raw.get(ops.createString(f7.name())));
                A8 v8 = f8.codec().decode(ops, raw.get(ops.createString(f8.name())));
                A9 v9 = f9.codec().decode(ops, raw.get(ops.createString(f9.name())));
                A10 v10 = f10.codec().decode(ops, raw.get(ops.createString(f10.name())));
                A11 v11 = f11.codec().decode(ops, raw.get(ops.createString(f11.name())));
                A12 v12 = f12.codec().decode(ops, raw.get(ops.createString(f12.name())));
                A13 v13 = f13.codec().decode(ops, raw.get(ops.createString(f13.name())));
                A14 v14 = f14.codec().decode(ops, raw.get(ops.createString(f14.name())));
                A15 v15 = f15.codec().decode(ops, raw.get(ops.createString(f15.name())));
                A16 v16 = f16.codec().decode(ops, raw.get(ops.createString(f16.name())));
                A17 v17 = f17.codec().decode(ops, raw.get(ops.createString(f17.name())));
                A18 v18 = f18.codec().decode(ops, raw.get(ops.createString(f18.name())));
                A19 v19 = f19.codec().decode(ops, raw.get(ops.createString(f19.name())));
                A20 v20 = f20.codec().decode(ops, raw.get(ops.createString(f20.name())));
                A21 v21 = f21.codec().decode(ops, raw.get(ops.createString(f21.name())));
                A22 v22 = f22.codec().decode(ops, raw.get(ops.createString(f22.name())));
                A23 v23 = f23.codec().decode(ops, raw.get(ops.createString(f23.name())));
                A24 v24 = f24.codec().decode(ops, raw.get(ops.createString(f24.name())));
                A25 v25 = f25.codec().decode(ops, raw.get(ops.createString(f25.name())));
                A26 v26 = f26.codec().decode(ops, raw.get(ops.createString(f26.name())));
                return builder.apply(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15, v16, v17, v18, v19, v20, v21, v22, v23, v24, v25, v26);
            }

            @Override
            public <D> D encode(DynamicOps<D> ops, T value) throws Codec.CodecException {
                Map<D, D> result = new LinkedHashMap<>();
                result.put(ops.createString(f1.name()), f1.codec().encode(ops, f1.getter().apply(value)));
                result.put(ops.createString(f2.name()), f2.codec().encode(ops, f2.getter().apply(value)));
                result.put(ops.createString(f3.name()), f3.codec().encode(ops, f3.getter().apply(value)));
                result.put(ops.createString(f4.name()), f4.codec().encode(ops, f4.getter().apply(value)));
                result.put(ops.createString(f5.name()), f5.codec().encode(ops, f5.getter().apply(value)));
                result.put(ops.createString(f6.name()), f6.codec().encode(ops, f6.getter().apply(value)));
                result.put(ops.createString(f7.name()), f7.codec().encode(ops, f7.getter().apply(value)));
                result.put(ops.createString(f8.name()), f8.codec().encode(ops, f8.getter().apply(value)));
                result.put(ops.createString(f9.name()), f9.codec().encode(ops, f9.getter().apply(value)));
                result.put(ops.createString(f10.name()), f10.codec().encode(ops, f10.getter().apply(value)));
                result.put(ops.createString(f11.name()), f11.codec().encode(ops, f11.getter().apply(value)));
                result.put(ops.createString(f12.name()), f12.codec().encode(ops, f12.getter().apply(value)));
                result.put(ops.createString(f13.name()), f13.codec().encode(ops, f13.getter().apply(value)));
                result.put(ops.createString(f14.name()), f14.codec().encode(ops, f14.getter().apply(value)));
                result.put(ops.createString(f15.name()), f15.codec().encode(ops, f15.getter().apply(value)));
                result.put(ops.createString(f16.name()), f16.codec().encode(ops, f16.getter().apply(value)));
                result.put(ops.createString(f17.name()), f17.codec().encode(ops, f17.getter().apply(value)));
                result.put(ops.createString(f18.name()), f18.codec().encode(ops, f18.getter().apply(value)));
                result.put(ops.createString(f19.name()), f19.codec().encode(ops, f19.getter().apply(value)));
                result.put(ops.createString(f20.name()), f20.codec().encode(ops, f20.getter().apply(value)));
                result.put(ops.createString(f21.name()), f21.codec().encode(ops, f21.getter().apply(value)));
                result.put(ops.createString(f22.name()), f22.codec().encode(ops, f22.getter().apply(value)));
                result.put(ops.createString(f23.name()), f23.codec().encode(ops, f23.getter().apply(value)));
                result.put(ops.createString(f24.name()), f24.codec().encode(ops, f24.getter().apply(value)));
                result.put(ops.createString(f25.name()), f25.codec().encode(ops, f25.getter().apply(value)));
                result.put(ops.createString(f26.name()), f26.codec().encode(ops, f26.getter().apply(value)));
                return ops.createMap(result);
            }
        };
    }

    public static <A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21, A22, A23, A24, A25, A26, A27, T> Codec<T> create(
            Codec.Field<T, A1> f1, Codec.Field<T, A2> f2, Codec.Field<T, A3> f3, Codec.Field<T, A4> f4, Codec.Field<T, A5> f5, Codec.Field<T, A6> f6, Codec.Field<T, A7> f7, Codec.Field<T, A8> f8, Codec.Field<T, A9> f9, Codec.Field<T, A10> f10, Codec.Field<T, A11> f11, Codec.Field<T, A12> f12, Codec.Field<T, A13> f13, Codec.Field<T, A14> f14, Codec.Field<T, A15> f15, Codec.Field<T, A16> f16, Codec.Field<T, A17> f17, Codec.Field<T, A18> f18, Codec.Field<T, A19> f19, Codec.Field<T, A20> f20, Codec.Field<T, A21> f21, Codec.Field<T, A22> f22, Codec.Field<T, A23> f23, Codec.Field<T, A24> f24, Codec.Field<T, A25> f25, Codec.Field<T, A26> f26, Codec.Field<T, A27> f27,
            Function27<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21, A22, A23, A24, A25, A26, A27, T> builder) {
        return new Codec<>() {
            @Override
            public <D> T decode(DynamicOps<D> ops, D input) throws Codec.CodecException {
                Map<D, D> raw = ops.getMap(input).orElseThrow(() -> new Codec.CodecException("Expected map"));
                A1 v1 = f1.codec().decode(ops, raw.get(ops.createString(f1.name())));
                A2 v2 = f2.codec().decode(ops, raw.get(ops.createString(f2.name())));
                A3 v3 = f3.codec().decode(ops, raw.get(ops.createString(f3.name())));
                A4 v4 = f4.codec().decode(ops, raw.get(ops.createString(f4.name())));
                A5 v5 = f5.codec().decode(ops, raw.get(ops.createString(f5.name())));
                A6 v6 = f6.codec().decode(ops, raw.get(ops.createString(f6.name())));
                A7 v7 = f7.codec().decode(ops, raw.get(ops.createString(f7.name())));
                A8 v8 = f8.codec().decode(ops, raw.get(ops.createString(f8.name())));
                A9 v9 = f9.codec().decode(ops, raw.get(ops.createString(f9.name())));
                A10 v10 = f10.codec().decode(ops, raw.get(ops.createString(f10.name())));
                A11 v11 = f11.codec().decode(ops, raw.get(ops.createString(f11.name())));
                A12 v12 = f12.codec().decode(ops, raw.get(ops.createString(f12.name())));
                A13 v13 = f13.codec().decode(ops, raw.get(ops.createString(f13.name())));
                A14 v14 = f14.codec().decode(ops, raw.get(ops.createString(f14.name())));
                A15 v15 = f15.codec().decode(ops, raw.get(ops.createString(f15.name())));
                A16 v16 = f16.codec().decode(ops, raw.get(ops.createString(f16.name())));
                A17 v17 = f17.codec().decode(ops, raw.get(ops.createString(f17.name())));
                A18 v18 = f18.codec().decode(ops, raw.get(ops.createString(f18.name())));
                A19 v19 = f19.codec().decode(ops, raw.get(ops.createString(f19.name())));
                A20 v20 = f20.codec().decode(ops, raw.get(ops.createString(f20.name())));
                A21 v21 = f21.codec().decode(ops, raw.get(ops.createString(f21.name())));
                A22 v22 = f22.codec().decode(ops, raw.get(ops.createString(f22.name())));
                A23 v23 = f23.codec().decode(ops, raw.get(ops.createString(f23.name())));
                A24 v24 = f24.codec().decode(ops, raw.get(ops.createString(f24.name())));
                A25 v25 = f25.codec().decode(ops, raw.get(ops.createString(f25.name())));
                A26 v26 = f26.codec().decode(ops, raw.get(ops.createString(f26.name())));
                A27 v27 = f27.codec().decode(ops, raw.get(ops.createString(f27.name())));
                return builder.apply(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15, v16, v17, v18, v19, v20, v21, v22, v23, v24, v25, v26, v27);
            }

            @Override
            public <D> D encode(DynamicOps<D> ops, T value) throws Codec.CodecException {
                Map<D, D> result = new LinkedHashMap<>();
                result.put(ops.createString(f1.name()), f1.codec().encode(ops, f1.getter().apply(value)));
                result.put(ops.createString(f2.name()), f2.codec().encode(ops, f2.getter().apply(value)));
                result.put(ops.createString(f3.name()), f3.codec().encode(ops, f3.getter().apply(value)));
                result.put(ops.createString(f4.name()), f4.codec().encode(ops, f4.getter().apply(value)));
                result.put(ops.createString(f5.name()), f5.codec().encode(ops, f5.getter().apply(value)));
                result.put(ops.createString(f6.name()), f6.codec().encode(ops, f6.getter().apply(value)));
                result.put(ops.createString(f7.name()), f7.codec().encode(ops, f7.getter().apply(value)));
                result.put(ops.createString(f8.name()), f8.codec().encode(ops, f8.getter().apply(value)));
                result.put(ops.createString(f9.name()), f9.codec().encode(ops, f9.getter().apply(value)));
                result.put(ops.createString(f10.name()), f10.codec().encode(ops, f10.getter().apply(value)));
                result.put(ops.createString(f11.name()), f11.codec().encode(ops, f11.getter().apply(value)));
                result.put(ops.createString(f12.name()), f12.codec().encode(ops, f12.getter().apply(value)));
                result.put(ops.createString(f13.name()), f13.codec().encode(ops, f13.getter().apply(value)));
                result.put(ops.createString(f14.name()), f14.codec().encode(ops, f14.getter().apply(value)));
                result.put(ops.createString(f15.name()), f15.codec().encode(ops, f15.getter().apply(value)));
                result.put(ops.createString(f16.name()), f16.codec().encode(ops, f16.getter().apply(value)));
                result.put(ops.createString(f17.name()), f17.codec().encode(ops, f17.getter().apply(value)));
                result.put(ops.createString(f18.name()), f18.codec().encode(ops, f18.getter().apply(value)));
                result.put(ops.createString(f19.name()), f19.codec().encode(ops, f19.getter().apply(value)));
                result.put(ops.createString(f20.name()), f20.codec().encode(ops, f20.getter().apply(value)));
                result.put(ops.createString(f21.name()), f21.codec().encode(ops, f21.getter().apply(value)));
                result.put(ops.createString(f22.name()), f22.codec().encode(ops, f22.getter().apply(value)));
                result.put(ops.createString(f23.name()), f23.codec().encode(ops, f23.getter().apply(value)));
                result.put(ops.createString(f24.name()), f24.codec().encode(ops, f24.getter().apply(value)));
                result.put(ops.createString(f25.name()), f25.codec().encode(ops, f25.getter().apply(value)));
                result.put(ops.createString(f26.name()), f26.codec().encode(ops, f26.getter().apply(value)));
                result.put(ops.createString(f27.name()), f27.codec().encode(ops, f27.getter().apply(value)));
                return ops.createMap(result);
            }
        };
    }

    public static <A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21, A22, A23, A24, A25, A26, A27, A28, T> Codec<T> create(
            Codec.Field<T, A1> f1, Codec.Field<T, A2> f2, Codec.Field<T, A3> f3, Codec.Field<T, A4> f4, Codec.Field<T, A5> f5, Codec.Field<T, A6> f6, Codec.Field<T, A7> f7, Codec.Field<T, A8> f8, Codec.Field<T, A9> f9, Codec.Field<T, A10> f10, Codec.Field<T, A11> f11, Codec.Field<T, A12> f12, Codec.Field<T, A13> f13, Codec.Field<T, A14> f14, Codec.Field<T, A15> f15, Codec.Field<T, A16> f16, Codec.Field<T, A17> f17, Codec.Field<T, A18> f18, Codec.Field<T, A19> f19, Codec.Field<T, A20> f20, Codec.Field<T, A21> f21, Codec.Field<T, A22> f22, Codec.Field<T, A23> f23, Codec.Field<T, A24> f24, Codec.Field<T, A25> f25, Codec.Field<T, A26> f26, Codec.Field<T, A27> f27, Codec.Field<T, A28> f28,
            Function28<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21, A22, A23, A24, A25, A26, A27, A28, T> builder) {
        return new Codec<>() {
            @Override
            public <D> T decode(DynamicOps<D> ops, D input) throws Codec.CodecException {
                Map<D, D> raw = ops.getMap(input).orElseThrow(() -> new Codec.CodecException("Expected map"));
                A1 v1 = f1.codec().decode(ops, raw.get(ops.createString(f1.name())));
                A2 v2 = f2.codec().decode(ops, raw.get(ops.createString(f2.name())));
                A3 v3 = f3.codec().decode(ops, raw.get(ops.createString(f3.name())));
                A4 v4 = f4.codec().decode(ops, raw.get(ops.createString(f4.name())));
                A5 v5 = f5.codec().decode(ops, raw.get(ops.createString(f5.name())));
                A6 v6 = f6.codec().decode(ops, raw.get(ops.createString(f6.name())));
                A7 v7 = f7.codec().decode(ops, raw.get(ops.createString(f7.name())));
                A8 v8 = f8.codec().decode(ops, raw.get(ops.createString(f8.name())));
                A9 v9 = f9.codec().decode(ops, raw.get(ops.createString(f9.name())));
                A10 v10 = f10.codec().decode(ops, raw.get(ops.createString(f10.name())));
                A11 v11 = f11.codec().decode(ops, raw.get(ops.createString(f11.name())));
                A12 v12 = f12.codec().decode(ops, raw.get(ops.createString(f12.name())));
                A13 v13 = f13.codec().decode(ops, raw.get(ops.createString(f13.name())));
                A14 v14 = f14.codec().decode(ops, raw.get(ops.createString(f14.name())));
                A15 v15 = f15.codec().decode(ops, raw.get(ops.createString(f15.name())));
                A16 v16 = f16.codec().decode(ops, raw.get(ops.createString(f16.name())));
                A17 v17 = f17.codec().decode(ops, raw.get(ops.createString(f17.name())));
                A18 v18 = f18.codec().decode(ops, raw.get(ops.createString(f18.name())));
                A19 v19 = f19.codec().decode(ops, raw.get(ops.createString(f19.name())));
                A20 v20 = f20.codec().decode(ops, raw.get(ops.createString(f20.name())));
                A21 v21 = f21.codec().decode(ops, raw.get(ops.createString(f21.name())));
                A22 v22 = f22.codec().decode(ops, raw.get(ops.createString(f22.name())));
                A23 v23 = f23.codec().decode(ops, raw.get(ops.createString(f23.name())));
                A24 v24 = f24.codec().decode(ops, raw.get(ops.createString(f24.name())));
                A25 v25 = f25.codec().decode(ops, raw.get(ops.createString(f25.name())));
                A26 v26 = f26.codec().decode(ops, raw.get(ops.createString(f26.name())));
                A27 v27 = f27.codec().decode(ops, raw.get(ops.createString(f27.name())));
                A28 v28 = f28.codec().decode(ops, raw.get(ops.createString(f28.name())));
                return builder.apply(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15, v16, v17, v18, v19, v20, v21, v22, v23, v24, v25, v26, v27, v28);
            }

            @Override
            public <D> D encode(DynamicOps<D> ops, T value) throws Codec.CodecException {
                Map<D, D> result = new LinkedHashMap<>();
                result.put(ops.createString(f1.name()), f1.codec().encode(ops, f1.getter().apply(value)));
                result.put(ops.createString(f2.name()), f2.codec().encode(ops, f2.getter().apply(value)));
                result.put(ops.createString(f3.name()), f3.codec().encode(ops, f3.getter().apply(value)));
                result.put(ops.createString(f4.name()), f4.codec().encode(ops, f4.getter().apply(value)));
                result.put(ops.createString(f5.name()), f5.codec().encode(ops, f5.getter().apply(value)));
                result.put(ops.createString(f6.name()), f6.codec().encode(ops, f6.getter().apply(value)));
                result.put(ops.createString(f7.name()), f7.codec().encode(ops, f7.getter().apply(value)));
                result.put(ops.createString(f8.name()), f8.codec().encode(ops, f8.getter().apply(value)));
                result.put(ops.createString(f9.name()), f9.codec().encode(ops, f9.getter().apply(value)));
                result.put(ops.createString(f10.name()), f10.codec().encode(ops, f10.getter().apply(value)));
                result.put(ops.createString(f11.name()), f11.codec().encode(ops, f11.getter().apply(value)));
                result.put(ops.createString(f12.name()), f12.codec().encode(ops, f12.getter().apply(value)));
                result.put(ops.createString(f13.name()), f13.codec().encode(ops, f13.getter().apply(value)));
                result.put(ops.createString(f14.name()), f14.codec().encode(ops, f14.getter().apply(value)));
                result.put(ops.createString(f15.name()), f15.codec().encode(ops, f15.getter().apply(value)));
                result.put(ops.createString(f16.name()), f16.codec().encode(ops, f16.getter().apply(value)));
                result.put(ops.createString(f17.name()), f17.codec().encode(ops, f17.getter().apply(value)));
                result.put(ops.createString(f18.name()), f18.codec().encode(ops, f18.getter().apply(value)));
                result.put(ops.createString(f19.name()), f19.codec().encode(ops, f19.getter().apply(value)));
                result.put(ops.createString(f20.name()), f20.codec().encode(ops, f20.getter().apply(value)));
                result.put(ops.createString(f21.name()), f21.codec().encode(ops, f21.getter().apply(value)));
                result.put(ops.createString(f22.name()), f22.codec().encode(ops, f22.getter().apply(value)));
                result.put(ops.createString(f23.name()), f23.codec().encode(ops, f23.getter().apply(value)));
                result.put(ops.createString(f24.name()), f24.codec().encode(ops, f24.getter().apply(value)));
                result.put(ops.createString(f25.name()), f25.codec().encode(ops, f25.getter().apply(value)));
                result.put(ops.createString(f26.name()), f26.codec().encode(ops, f26.getter().apply(value)));
                result.put(ops.createString(f27.name()), f27.codec().encode(ops, f27.getter().apply(value)));
                result.put(ops.createString(f28.name()), f28.codec().encode(ops, f28.getter().apply(value)));
                return ops.createMap(result);
            }
        };
    }

    public static <A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21, A22, A23, A24, A25, A26, A27, A28, A29, T> Codec<T> create(
            Codec.Field<T, A1> f1, Codec.Field<T, A2> f2, Codec.Field<T, A3> f3, Codec.Field<T, A4> f4, Codec.Field<T, A5> f5, Codec.Field<T, A6> f6, Codec.Field<T, A7> f7, Codec.Field<T, A8> f8, Codec.Field<T, A9> f9, Codec.Field<T, A10> f10, Codec.Field<T, A11> f11, Codec.Field<T, A12> f12, Codec.Field<T, A13> f13, Codec.Field<T, A14> f14, Codec.Field<T, A15> f15, Codec.Field<T, A16> f16, Codec.Field<T, A17> f17, Codec.Field<T, A18> f18, Codec.Field<T, A19> f19, Codec.Field<T, A20> f20, Codec.Field<T, A21> f21, Codec.Field<T, A22> f22, Codec.Field<T, A23> f23, Codec.Field<T, A24> f24, Codec.Field<T, A25> f25, Codec.Field<T, A26> f26, Codec.Field<T, A27> f27, Codec.Field<T, A28> f28, Codec.Field<T, A29> f29,
            Function29<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21, A22, A23, A24, A25, A26, A27, A28, A29, T> builder) {
        return new Codec<>() {
            @Override
            public <D> T decode(DynamicOps<D> ops, D input) throws Codec.CodecException {
                Map<D, D> raw = ops.getMap(input).orElseThrow(() -> new Codec.CodecException("Expected map"));
                A1 v1 = f1.codec().decode(ops, raw.get(ops.createString(f1.name())));
                A2 v2 = f2.codec().decode(ops, raw.get(ops.createString(f2.name())));
                A3 v3 = f3.codec().decode(ops, raw.get(ops.createString(f3.name())));
                A4 v4 = f4.codec().decode(ops, raw.get(ops.createString(f4.name())));
                A5 v5 = f5.codec().decode(ops, raw.get(ops.createString(f5.name())));
                A6 v6 = f6.codec().decode(ops, raw.get(ops.createString(f6.name())));
                A7 v7 = f7.codec().decode(ops, raw.get(ops.createString(f7.name())));
                A8 v8 = f8.codec().decode(ops, raw.get(ops.createString(f8.name())));
                A9 v9 = f9.codec().decode(ops, raw.get(ops.createString(f9.name())));
                A10 v10 = f10.codec().decode(ops, raw.get(ops.createString(f10.name())));
                A11 v11 = f11.codec().decode(ops, raw.get(ops.createString(f11.name())));
                A12 v12 = f12.codec().decode(ops, raw.get(ops.createString(f12.name())));
                A13 v13 = f13.codec().decode(ops, raw.get(ops.createString(f13.name())));
                A14 v14 = f14.codec().decode(ops, raw.get(ops.createString(f14.name())));
                A15 v15 = f15.codec().decode(ops, raw.get(ops.createString(f15.name())));
                A16 v16 = f16.codec().decode(ops, raw.get(ops.createString(f16.name())));
                A17 v17 = f17.codec().decode(ops, raw.get(ops.createString(f17.name())));
                A18 v18 = f18.codec().decode(ops, raw.get(ops.createString(f18.name())));
                A19 v19 = f19.codec().decode(ops, raw.get(ops.createString(f19.name())));
                A20 v20 = f20.codec().decode(ops, raw.get(ops.createString(f20.name())));
                A21 v21 = f21.codec().decode(ops, raw.get(ops.createString(f21.name())));
                A22 v22 = f22.codec().decode(ops, raw.get(ops.createString(f22.name())));
                A23 v23 = f23.codec().decode(ops, raw.get(ops.createString(f23.name())));
                A24 v24 = f24.codec().decode(ops, raw.get(ops.createString(f24.name())));
                A25 v25 = f25.codec().decode(ops, raw.get(ops.createString(f25.name())));
                A26 v26 = f26.codec().decode(ops, raw.get(ops.createString(f26.name())));
                A27 v27 = f27.codec().decode(ops, raw.get(ops.createString(f27.name())));
                A28 v28 = f28.codec().decode(ops, raw.get(ops.createString(f28.name())));
                A29 v29 = f29.codec().decode(ops, raw.get(ops.createString(f29.name())));
                return builder.apply(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15, v16, v17, v18, v19, v20, v21, v22, v23, v24, v25, v26, v27, v28, v29);
            }

            @Override
            public <D> D encode(DynamicOps<D> ops, T value) throws Codec.CodecException {
                Map<D, D> result = new LinkedHashMap<>();
                result.put(ops.createString(f1.name()), f1.codec().encode(ops, f1.getter().apply(value)));
                result.put(ops.createString(f2.name()), f2.codec().encode(ops, f2.getter().apply(value)));
                result.put(ops.createString(f3.name()), f3.codec().encode(ops, f3.getter().apply(value)));
                result.put(ops.createString(f4.name()), f4.codec().encode(ops, f4.getter().apply(value)));
                result.put(ops.createString(f5.name()), f5.codec().encode(ops, f5.getter().apply(value)));
                result.put(ops.createString(f6.name()), f6.codec().encode(ops, f6.getter().apply(value)));
                result.put(ops.createString(f7.name()), f7.codec().encode(ops, f7.getter().apply(value)));
                result.put(ops.createString(f8.name()), f8.codec().encode(ops, f8.getter().apply(value)));
                result.put(ops.createString(f9.name()), f9.codec().encode(ops, f9.getter().apply(value)));
                result.put(ops.createString(f10.name()), f10.codec().encode(ops, f10.getter().apply(value)));
                result.put(ops.createString(f11.name()), f11.codec().encode(ops, f11.getter().apply(value)));
                result.put(ops.createString(f12.name()), f12.codec().encode(ops, f12.getter().apply(value)));
                result.put(ops.createString(f13.name()), f13.codec().encode(ops, f13.getter().apply(value)));
                result.put(ops.createString(f14.name()), f14.codec().encode(ops, f14.getter().apply(value)));
                result.put(ops.createString(f15.name()), f15.codec().encode(ops, f15.getter().apply(value)));
                result.put(ops.createString(f16.name()), f16.codec().encode(ops, f16.getter().apply(value)));
                result.put(ops.createString(f17.name()), f17.codec().encode(ops, f17.getter().apply(value)));
                result.put(ops.createString(f18.name()), f18.codec().encode(ops, f18.getter().apply(value)));
                result.put(ops.createString(f19.name()), f19.codec().encode(ops, f19.getter().apply(value)));
                result.put(ops.createString(f20.name()), f20.codec().encode(ops, f20.getter().apply(value)));
                result.put(ops.createString(f21.name()), f21.codec().encode(ops, f21.getter().apply(value)));
                result.put(ops.createString(f22.name()), f22.codec().encode(ops, f22.getter().apply(value)));
                result.put(ops.createString(f23.name()), f23.codec().encode(ops, f23.getter().apply(value)));
                result.put(ops.createString(f24.name()), f24.codec().encode(ops, f24.getter().apply(value)));
                result.put(ops.createString(f25.name()), f25.codec().encode(ops, f25.getter().apply(value)));
                result.put(ops.createString(f26.name()), f26.codec().encode(ops, f26.getter().apply(value)));
                result.put(ops.createString(f27.name()), f27.codec().encode(ops, f27.getter().apply(value)));
                result.put(ops.createString(f28.name()), f28.codec().encode(ops, f28.getter().apply(value)));
                result.put(ops.createString(f29.name()), f29.codec().encode(ops, f29.getter().apply(value)));
                return ops.createMap(result);
            }
        };
    }

    public static <A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21, A22, A23, A24, A25, A26, A27, A28, A29, A30, T> Codec<T> create(
            Codec.Field<T, A1> f1, Codec.Field<T, A2> f2, Codec.Field<T, A3> f3, Codec.Field<T, A4> f4, Codec.Field<T, A5> f5, Codec.Field<T, A6> f6, Codec.Field<T, A7> f7, Codec.Field<T, A8> f8, Codec.Field<T, A9> f9, Codec.Field<T, A10> f10, Codec.Field<T, A11> f11, Codec.Field<T, A12> f12, Codec.Field<T, A13> f13, Codec.Field<T, A14> f14, Codec.Field<T, A15> f15, Codec.Field<T, A16> f16, Codec.Field<T, A17> f17, Codec.Field<T, A18> f18, Codec.Field<T, A19> f19, Codec.Field<T, A20> f20, Codec.Field<T, A21> f21, Codec.Field<T, A22> f22, Codec.Field<T, A23> f23, Codec.Field<T, A24> f24, Codec.Field<T, A25> f25, Codec.Field<T, A26> f26, Codec.Field<T, A27> f27, Codec.Field<T, A28> f28, Codec.Field<T, A29> f29, Codec.Field<T, A30> f30,
            Function30<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21, A22, A23, A24, A25, A26, A27, A28, A29, A30, T> builder) {
        return new Codec<>() {
            @Override
            public <D> T decode(DynamicOps<D> ops, D input) throws Codec.CodecException {
                Map<D, D> raw = ops.getMap(input).orElseThrow(() -> new Codec.CodecException("Expected map"));
                A1 v1 = f1.codec().decode(ops, raw.get(ops.createString(f1.name())));
                A2 v2 = f2.codec().decode(ops, raw.get(ops.createString(f2.name())));
                A3 v3 = f3.codec().decode(ops, raw.get(ops.createString(f3.name())));
                A4 v4 = f4.codec().decode(ops, raw.get(ops.createString(f4.name())));
                A5 v5 = f5.codec().decode(ops, raw.get(ops.createString(f5.name())));
                A6 v6 = f6.codec().decode(ops, raw.get(ops.createString(f6.name())));
                A7 v7 = f7.codec().decode(ops, raw.get(ops.createString(f7.name())));
                A8 v8 = f8.codec().decode(ops, raw.get(ops.createString(f8.name())));
                A9 v9 = f9.codec().decode(ops, raw.get(ops.createString(f9.name())));
                A10 v10 = f10.codec().decode(ops, raw.get(ops.createString(f10.name())));
                A11 v11 = f11.codec().decode(ops, raw.get(ops.createString(f11.name())));
                A12 v12 = f12.codec().decode(ops, raw.get(ops.createString(f12.name())));
                A13 v13 = f13.codec().decode(ops, raw.get(ops.createString(f13.name())));
                A14 v14 = f14.codec().decode(ops, raw.get(ops.createString(f14.name())));
                A15 v15 = f15.codec().decode(ops, raw.get(ops.createString(f15.name())));
                A16 v16 = f16.codec().decode(ops, raw.get(ops.createString(f16.name())));
                A17 v17 = f17.codec().decode(ops, raw.get(ops.createString(f17.name())));
                A18 v18 = f18.codec().decode(ops, raw.get(ops.createString(f18.name())));
                A19 v19 = f19.codec().decode(ops, raw.get(ops.createString(f19.name())));
                A20 v20 = f20.codec().decode(ops, raw.get(ops.createString(f20.name())));
                A21 v21 = f21.codec().decode(ops, raw.get(ops.createString(f21.name())));
                A22 v22 = f22.codec().decode(ops, raw.get(ops.createString(f22.name())));
                A23 v23 = f23.codec().decode(ops, raw.get(ops.createString(f23.name())));
                A24 v24 = f24.codec().decode(ops, raw.get(ops.createString(f24.name())));
                A25 v25 = f25.codec().decode(ops, raw.get(ops.createString(f25.name())));
                A26 v26 = f26.codec().decode(ops, raw.get(ops.createString(f26.name())));
                A27 v27 = f27.codec().decode(ops, raw.get(ops.createString(f27.name())));
                A28 v28 = f28.codec().decode(ops, raw.get(ops.createString(f28.name())));
                A29 v29 = f29.codec().decode(ops, raw.get(ops.createString(f29.name())));
                A30 v30 = f30.codec().decode(ops, raw.get(ops.createString(f30.name())));
                return builder.apply(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15, v16, v17, v18, v19, v20, v21, v22, v23, v24, v25, v26, v27, v28, v29, v30);
            }

            @Override
            public <D> D encode(DynamicOps<D> ops, T value) throws Codec.CodecException {
                Map<D, D> result = new LinkedHashMap<>();
                result.put(ops.createString(f1.name()), f1.codec().encode(ops, f1.getter().apply(value)));
                result.put(ops.createString(f2.name()), f2.codec().encode(ops, f2.getter().apply(value)));
                result.put(ops.createString(f3.name()), f3.codec().encode(ops, f3.getter().apply(value)));
                result.put(ops.createString(f4.name()), f4.codec().encode(ops, f4.getter().apply(value)));
                result.put(ops.createString(f5.name()), f5.codec().encode(ops, f5.getter().apply(value)));
                result.put(ops.createString(f6.name()), f6.codec().encode(ops, f6.getter().apply(value)));
                result.put(ops.createString(f7.name()), f7.codec().encode(ops, f7.getter().apply(value)));
                result.put(ops.createString(f8.name()), f8.codec().encode(ops, f8.getter().apply(value)));
                result.put(ops.createString(f9.name()), f9.codec().encode(ops, f9.getter().apply(value)));
                result.put(ops.createString(f10.name()), f10.codec().encode(ops, f10.getter().apply(value)));
                result.put(ops.createString(f11.name()), f11.codec().encode(ops, f11.getter().apply(value)));
                result.put(ops.createString(f12.name()), f12.codec().encode(ops, f12.getter().apply(value)));
                result.put(ops.createString(f13.name()), f13.codec().encode(ops, f13.getter().apply(value)));
                result.put(ops.createString(f14.name()), f14.codec().encode(ops, f14.getter().apply(value)));
                result.put(ops.createString(f15.name()), f15.codec().encode(ops, f15.getter().apply(value)));
                result.put(ops.createString(f16.name()), f16.codec().encode(ops, f16.getter().apply(value)));
                result.put(ops.createString(f17.name()), f17.codec().encode(ops, f17.getter().apply(value)));
                result.put(ops.createString(f18.name()), f18.codec().encode(ops, f18.getter().apply(value)));
                result.put(ops.createString(f19.name()), f19.codec().encode(ops, f19.getter().apply(value)));
                result.put(ops.createString(f20.name()), f20.codec().encode(ops, f20.getter().apply(value)));
                result.put(ops.createString(f21.name()), f21.codec().encode(ops, f21.getter().apply(value)));
                result.put(ops.createString(f22.name()), f22.codec().encode(ops, f22.getter().apply(value)));
                result.put(ops.createString(f23.name()), f23.codec().encode(ops, f23.getter().apply(value)));
                result.put(ops.createString(f24.name()), f24.codec().encode(ops, f24.getter().apply(value)));
                result.put(ops.createString(f25.name()), f25.codec().encode(ops, f25.getter().apply(value)));
                result.put(ops.createString(f26.name()), f26.codec().encode(ops, f26.getter().apply(value)));
                result.put(ops.createString(f27.name()), f27.codec().encode(ops, f27.getter().apply(value)));
                result.put(ops.createString(f28.name()), f28.codec().encode(ops, f28.getter().apply(value)));
                result.put(ops.createString(f29.name()), f29.codec().encode(ops, f29.getter().apply(value)));
                result.put(ops.createString(f30.name()), f30.codec().encode(ops, f30.getter().apply(value)));
                return ops.createMap(result);
            }
        };
    }

    public static <A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21, A22, A23, A24, A25, A26, A27, A28, A29, A30, A31, T> Codec<T> create(
            Codec.Field<T, A1> f1, Codec.Field<T, A2> f2, Codec.Field<T, A3> f3, Codec.Field<T, A4> f4, Codec.Field<T, A5> f5, Codec.Field<T, A6> f6, Codec.Field<T, A7> f7, Codec.Field<T, A8> f8, Codec.Field<T, A9> f9, Codec.Field<T, A10> f10, Codec.Field<T, A11> f11, Codec.Field<T, A12> f12, Codec.Field<T, A13> f13, Codec.Field<T, A14> f14, Codec.Field<T, A15> f15, Codec.Field<T, A16> f16, Codec.Field<T, A17> f17, Codec.Field<T, A18> f18, Codec.Field<T, A19> f19, Codec.Field<T, A20> f20, Codec.Field<T, A21> f21, Codec.Field<T, A22> f22, Codec.Field<T, A23> f23, Codec.Field<T, A24> f24, Codec.Field<T, A25> f25, Codec.Field<T, A26> f26, Codec.Field<T, A27> f27, Codec.Field<T, A28> f28, Codec.Field<T, A29> f29, Codec.Field<T, A30> f30, Codec.Field<T, A31> f31,
            Function31<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21, A22, A23, A24, A25, A26, A27, A28, A29, A30, A31, T> builder) {
        return new Codec<>() {
            @Override
            public <D> T decode(DynamicOps<D> ops, D input) throws Codec.CodecException {
                Map<D, D> raw = ops.getMap(input).orElseThrow(() -> new Codec.CodecException("Expected map"));
                A1 v1 = f1.codec().decode(ops, raw.get(ops.createString(f1.name())));
                A2 v2 = f2.codec().decode(ops, raw.get(ops.createString(f2.name())));
                A3 v3 = f3.codec().decode(ops, raw.get(ops.createString(f3.name())));
                A4 v4 = f4.codec().decode(ops, raw.get(ops.createString(f4.name())));
                A5 v5 = f5.codec().decode(ops, raw.get(ops.createString(f5.name())));
                A6 v6 = f6.codec().decode(ops, raw.get(ops.createString(f6.name())));
                A7 v7 = f7.codec().decode(ops, raw.get(ops.createString(f7.name())));
                A8 v8 = f8.codec().decode(ops, raw.get(ops.createString(f8.name())));
                A9 v9 = f9.codec().decode(ops, raw.get(ops.createString(f9.name())));
                A10 v10 = f10.codec().decode(ops, raw.get(ops.createString(f10.name())));
                A11 v11 = f11.codec().decode(ops, raw.get(ops.createString(f11.name())));
                A12 v12 = f12.codec().decode(ops, raw.get(ops.createString(f12.name())));
                A13 v13 = f13.codec().decode(ops, raw.get(ops.createString(f13.name())));
                A14 v14 = f14.codec().decode(ops, raw.get(ops.createString(f14.name())));
                A15 v15 = f15.codec().decode(ops, raw.get(ops.createString(f15.name())));
                A16 v16 = f16.codec().decode(ops, raw.get(ops.createString(f16.name())));
                A17 v17 = f17.codec().decode(ops, raw.get(ops.createString(f17.name())));
                A18 v18 = f18.codec().decode(ops, raw.get(ops.createString(f18.name())));
                A19 v19 = f19.codec().decode(ops, raw.get(ops.createString(f19.name())));
                A20 v20 = f20.codec().decode(ops, raw.get(ops.createString(f20.name())));
                A21 v21 = f21.codec().decode(ops, raw.get(ops.createString(f21.name())));
                A22 v22 = f22.codec().decode(ops, raw.get(ops.createString(f22.name())));
                A23 v23 = f23.codec().decode(ops, raw.get(ops.createString(f23.name())));
                A24 v24 = f24.codec().decode(ops, raw.get(ops.createString(f24.name())));
                A25 v25 = f25.codec().decode(ops, raw.get(ops.createString(f25.name())));
                A26 v26 = f26.codec().decode(ops, raw.get(ops.createString(f26.name())));
                A27 v27 = f27.codec().decode(ops, raw.get(ops.createString(f27.name())));
                A28 v28 = f28.codec().decode(ops, raw.get(ops.createString(f28.name())));
                A29 v29 = f29.codec().decode(ops, raw.get(ops.createString(f29.name())));
                A30 v30 = f30.codec().decode(ops, raw.get(ops.createString(f30.name())));
                A31 v31 = f31.codec().decode(ops, raw.get(ops.createString(f31.name())));
                return builder.apply(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15, v16, v17, v18, v19, v20, v21, v22, v23, v24, v25, v26, v27, v28, v29, v30, v31);
            }

            @Override
            public <D> D encode(DynamicOps<D> ops, T value) throws Codec.CodecException {
                Map<D, D> result = new LinkedHashMap<>();
                result.put(ops.createString(f1.name()), f1.codec().encode(ops, f1.getter().apply(value)));
                result.put(ops.createString(f2.name()), f2.codec().encode(ops, f2.getter().apply(value)));
                result.put(ops.createString(f3.name()), f3.codec().encode(ops, f3.getter().apply(value)));
                result.put(ops.createString(f4.name()), f4.codec().encode(ops, f4.getter().apply(value)));
                result.put(ops.createString(f5.name()), f5.codec().encode(ops, f5.getter().apply(value)));
                result.put(ops.createString(f6.name()), f6.codec().encode(ops, f6.getter().apply(value)));
                result.put(ops.createString(f7.name()), f7.codec().encode(ops, f7.getter().apply(value)));
                result.put(ops.createString(f8.name()), f8.codec().encode(ops, f8.getter().apply(value)));
                result.put(ops.createString(f9.name()), f9.codec().encode(ops, f9.getter().apply(value)));
                result.put(ops.createString(f10.name()), f10.codec().encode(ops, f10.getter().apply(value)));
                result.put(ops.createString(f11.name()), f11.codec().encode(ops, f11.getter().apply(value)));
                result.put(ops.createString(f12.name()), f12.codec().encode(ops, f12.getter().apply(value)));
                result.put(ops.createString(f13.name()), f13.codec().encode(ops, f13.getter().apply(value)));
                result.put(ops.createString(f14.name()), f14.codec().encode(ops, f14.getter().apply(value)));
                result.put(ops.createString(f15.name()), f15.codec().encode(ops, f15.getter().apply(value)));
                result.put(ops.createString(f16.name()), f16.codec().encode(ops, f16.getter().apply(value)));
                result.put(ops.createString(f17.name()), f17.codec().encode(ops, f17.getter().apply(value)));
                result.put(ops.createString(f18.name()), f18.codec().encode(ops, f18.getter().apply(value)));
                result.put(ops.createString(f19.name()), f19.codec().encode(ops, f19.getter().apply(value)));
                result.put(ops.createString(f20.name()), f20.codec().encode(ops, f20.getter().apply(value)));
                result.put(ops.createString(f21.name()), f21.codec().encode(ops, f21.getter().apply(value)));
                result.put(ops.createString(f22.name()), f22.codec().encode(ops, f22.getter().apply(value)));
                result.put(ops.createString(f23.name()), f23.codec().encode(ops, f23.getter().apply(value)));
                result.put(ops.createString(f24.name()), f24.codec().encode(ops, f24.getter().apply(value)));
                result.put(ops.createString(f25.name()), f25.codec().encode(ops, f25.getter().apply(value)));
                result.put(ops.createString(f26.name()), f26.codec().encode(ops, f26.getter().apply(value)));
                result.put(ops.createString(f27.name()), f27.codec().encode(ops, f27.getter().apply(value)));
                result.put(ops.createString(f28.name()), f28.codec().encode(ops, f28.getter().apply(value)));
                result.put(ops.createString(f29.name()), f29.codec().encode(ops, f29.getter().apply(value)));
                result.put(ops.createString(f30.name()), f30.codec().encode(ops, f30.getter().apply(value)));
                result.put(ops.createString(f31.name()), f31.codec().encode(ops, f31.getter().apply(value)));
                return ops.createMap(result);
            }
        };
    }

    public static <A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21, A22, A23, A24, A25, A26, A27, A28, A29, A30, A31, A32, T> Codec<T> create(
            Codec.Field<T, A1> f1, Codec.Field<T, A2> f2, Codec.Field<T, A3> f3, Codec.Field<T, A4> f4, Codec.Field<T, A5> f5, Codec.Field<T, A6> f6, Codec.Field<T, A7> f7, Codec.Field<T, A8> f8, Codec.Field<T, A9> f9, Codec.Field<T, A10> f10, Codec.Field<T, A11> f11, Codec.Field<T, A12> f12, Codec.Field<T, A13> f13, Codec.Field<T, A14> f14, Codec.Field<T, A15> f15, Codec.Field<T, A16> f16, Codec.Field<T, A17> f17, Codec.Field<T, A18> f18, Codec.Field<T, A19> f19, Codec.Field<T, A20> f20, Codec.Field<T, A21> f21, Codec.Field<T, A22> f22, Codec.Field<T, A23> f23, Codec.Field<T, A24> f24, Codec.Field<T, A25> f25, Codec.Field<T, A26> f26, Codec.Field<T, A27> f27, Codec.Field<T, A28> f28, Codec.Field<T, A29> f29, Codec.Field<T, A30> f30, Codec.Field<T, A31> f31, Codec.Field<T, A32> f32,
            Function32<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21, A22, A23, A24, A25, A26, A27, A28, A29, A30, A31, A32, T> builder) {
        return new Codec<>() {
            @Override
            public <D> T decode(DynamicOps<D> ops, D input) throws Codec.CodecException {
                Map<D, D> raw = ops.getMap(input).orElseThrow(() -> new Codec.CodecException("Expected map"));
                A1 v1 = f1.codec().decode(ops, raw.get(ops.createString(f1.name())));
                A2 v2 = f2.codec().decode(ops, raw.get(ops.createString(f2.name())));
                A3 v3 = f3.codec().decode(ops, raw.get(ops.createString(f3.name())));
                A4 v4 = f4.codec().decode(ops, raw.get(ops.createString(f4.name())));
                A5 v5 = f5.codec().decode(ops, raw.get(ops.createString(f5.name())));
                A6 v6 = f6.codec().decode(ops, raw.get(ops.createString(f6.name())));
                A7 v7 = f7.codec().decode(ops, raw.get(ops.createString(f7.name())));
                A8 v8 = f8.codec().decode(ops, raw.get(ops.createString(f8.name())));
                A9 v9 = f9.codec().decode(ops, raw.get(ops.createString(f9.name())));
                A10 v10 = f10.codec().decode(ops, raw.get(ops.createString(f10.name())));
                A11 v11 = f11.codec().decode(ops, raw.get(ops.createString(f11.name())));
                A12 v12 = f12.codec().decode(ops, raw.get(ops.createString(f12.name())));
                A13 v13 = f13.codec().decode(ops, raw.get(ops.createString(f13.name())));
                A14 v14 = f14.codec().decode(ops, raw.get(ops.createString(f14.name())));
                A15 v15 = f15.codec().decode(ops, raw.get(ops.createString(f15.name())));
                A16 v16 = f16.codec().decode(ops, raw.get(ops.createString(f16.name())));
                A17 v17 = f17.codec().decode(ops, raw.get(ops.createString(f17.name())));
                A18 v18 = f18.codec().decode(ops, raw.get(ops.createString(f18.name())));
                A19 v19 = f19.codec().decode(ops, raw.get(ops.createString(f19.name())));
                A20 v20 = f20.codec().decode(ops, raw.get(ops.createString(f20.name())));
                A21 v21 = f21.codec().decode(ops, raw.get(ops.createString(f21.name())));
                A22 v22 = f22.codec().decode(ops, raw.get(ops.createString(f22.name())));
                A23 v23 = f23.codec().decode(ops, raw.get(ops.createString(f23.name())));
                A24 v24 = f24.codec().decode(ops, raw.get(ops.createString(f24.name())));
                A25 v25 = f25.codec().decode(ops, raw.get(ops.createString(f25.name())));
                A26 v26 = f26.codec().decode(ops, raw.get(ops.createString(f26.name())));
                A27 v27 = f27.codec().decode(ops, raw.get(ops.createString(f27.name())));
                A28 v28 = f28.codec().decode(ops, raw.get(ops.createString(f28.name())));
                A29 v29 = f29.codec().decode(ops, raw.get(ops.createString(f29.name())));
                A30 v30 = f30.codec().decode(ops, raw.get(ops.createString(f30.name())));
                A31 v31 = f31.codec().decode(ops, raw.get(ops.createString(f31.name())));
                A32 v32 = f32.codec().decode(ops, raw.get(ops.createString(f32.name())));
                return builder.apply(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15, v16, v17, v18, v19, v20, v21, v22, v23, v24, v25, v26, v27, v28, v29, v30, v31, v32);
            }

            @Override
            public <D> D encode(DynamicOps<D> ops, T value) throws Codec.CodecException {
                Map<D, D> result = new LinkedHashMap<>();
                result.put(ops.createString(f1.name()), f1.codec().encode(ops, f1.getter().apply(value)));
                result.put(ops.createString(f2.name()), f2.codec().encode(ops, f2.getter().apply(value)));
                result.put(ops.createString(f3.name()), f3.codec().encode(ops, f3.getter().apply(value)));
                result.put(ops.createString(f4.name()), f4.codec().encode(ops, f4.getter().apply(value)));
                result.put(ops.createString(f5.name()), f5.codec().encode(ops, f5.getter().apply(value)));
                result.put(ops.createString(f6.name()), f6.codec().encode(ops, f6.getter().apply(value)));
                result.put(ops.createString(f7.name()), f7.codec().encode(ops, f7.getter().apply(value)));
                result.put(ops.createString(f8.name()), f8.codec().encode(ops, f8.getter().apply(value)));
                result.put(ops.createString(f9.name()), f9.codec().encode(ops, f9.getter().apply(value)));
                result.put(ops.createString(f10.name()), f10.codec().encode(ops, f10.getter().apply(value)));
                result.put(ops.createString(f11.name()), f11.codec().encode(ops, f11.getter().apply(value)));
                result.put(ops.createString(f12.name()), f12.codec().encode(ops, f12.getter().apply(value)));
                result.put(ops.createString(f13.name()), f13.codec().encode(ops, f13.getter().apply(value)));
                result.put(ops.createString(f14.name()), f14.codec().encode(ops, f14.getter().apply(value)));
                result.put(ops.createString(f15.name()), f15.codec().encode(ops, f15.getter().apply(value)));
                result.put(ops.createString(f16.name()), f16.codec().encode(ops, f16.getter().apply(value)));
                result.put(ops.createString(f17.name()), f17.codec().encode(ops, f17.getter().apply(value)));
                result.put(ops.createString(f18.name()), f18.codec().encode(ops, f18.getter().apply(value)));
                result.put(ops.createString(f19.name()), f19.codec().encode(ops, f19.getter().apply(value)));
                result.put(ops.createString(f20.name()), f20.codec().encode(ops, f20.getter().apply(value)));
                result.put(ops.createString(f21.name()), f21.codec().encode(ops, f21.getter().apply(value)));
                result.put(ops.createString(f22.name()), f22.codec().encode(ops, f22.getter().apply(value)));
                result.put(ops.createString(f23.name()), f23.codec().encode(ops, f23.getter().apply(value)));
                result.put(ops.createString(f24.name()), f24.codec().encode(ops, f24.getter().apply(value)));
                result.put(ops.createString(f25.name()), f25.codec().encode(ops, f25.getter().apply(value)));
                result.put(ops.createString(f26.name()), f26.codec().encode(ops, f26.getter().apply(value)));
                result.put(ops.createString(f27.name()), f27.codec().encode(ops, f27.getter().apply(value)));
                result.put(ops.createString(f28.name()), f28.codec().encode(ops, f28.getter().apply(value)));
                result.put(ops.createString(f29.name()), f29.codec().encode(ops, f29.getter().apply(value)));
                result.put(ops.createString(f30.name()), f30.codec().encode(ops, f30.getter().apply(value)));
                result.put(ops.createString(f31.name()), f31.codec().encode(ops, f31.getter().apply(value)));
                result.put(ops.createString(f32.name()), f32.codec().encode(ops, f32.getter().apply(value)));
                return ops.createMap(result);
            }
        };
    }

    @FunctionalInterface
    public interface Function2<T1, T2, R> {
        R apply(T1 t1, T2 t2);
    }

    @FunctionalInterface
    public interface Function3<T1, T2, T3, R> {
        R apply(T1 t1, T2 t2, T3 t3);
    }

    @FunctionalInterface
    public interface Function4<T1, T2, T3, T4, R> {
        R apply(T1 t1, T2 t2, T3 t3, T4 t4);
    }

    @FunctionalInterface
    public interface Function5<T1, T2, T3, T4, T5, R> {
        R apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5);
    }

    @FunctionalInterface
    public interface Function6<T1, T2, T3, T4, T5, T6, R> {
        R apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6);
    }

    @FunctionalInterface
    public interface Function7<T1, T2, T3, T4, T5, T6, T7, R> {
        R apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7);
    }

    @FunctionalInterface
    public interface Function8<T1, T2, T3, T4, T5, T6, T7, T8, R> {
        R apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8);
    }

    @FunctionalInterface
    public interface Function9<T1, T2, T3, T4, T5, T6, T7, T8, T9, R> {
        R apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9);
    }

    @FunctionalInterface
    public interface Function10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> {
        R apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10);
    }

    @FunctionalInterface
    public interface Function11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R> {
        R apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11);
    }

    @FunctionalInterface
    public interface Function12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R> {
        R apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12);
    }

    @FunctionalInterface
    public interface Function13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R> {
        R apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13);
    }

    @FunctionalInterface
    public interface Function14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R> {
        R apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13, T14 t14);
    }

    @FunctionalInterface
    public interface Function15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R> {
        R apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13, T14 t14, T15 t15);
    }

    @FunctionalInterface
    public interface Function16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R> {
        R apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13, T14 t14, T15 t15, T16 t16);
    }

    @FunctionalInterface
    public interface Function17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, R> {
        R apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13, T14 t14, T15 t15, T16 t16, T17 t17);
    }

    @FunctionalInterface
    public interface Function18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, R> {
        R apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13, T14 t14, T15 t15, T16 t16, T17 t17, T18 t18);
    }

    @FunctionalInterface
    public interface Function19<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, R> {
        R apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13, T14 t14, T15 t15, T16 t16, T17 t17, T18 t18, T19 t19);
    }

    @FunctionalInterface
    public interface Function20<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, R> {
        R apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13, T14 t14, T15 t15, T16 t16, T17 t17, T18 t18, T19 t19, T20 t20);
    }

    @FunctionalInterface
    public interface Function21<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, R> {
        R apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13, T14 t14, T15 t15, T16 t16, T17 t17, T18 t18, T19 t19, T20 t20, T21 t21);
    }

    @FunctionalInterface
    public interface Function22<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, R> {
        R apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13, T14 t14, T15 t15, T16 t16, T17 t17, T18 t18, T19 t19, T20 t20, T21 t21, T22 t22);
    }

    @FunctionalInterface
    public interface Function23<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, R> {
        R apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13, T14 t14, T15 t15, T16 t16, T17 t17, T18 t18, T19 t19, T20 t20, T21 t21, T22 t22, T23 t23);
    }

    @FunctionalInterface
    public interface Function24<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, R> {
        R apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13, T14 t14, T15 t15, T16 t16, T17 t17, T18 t18, T19 t19, T20 t20, T21 t21, T22 t22, T23 t23, T24 t24);
    }

    @FunctionalInterface
    public interface Function25<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, R> {
        R apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13, T14 t14, T15 t15, T16 t16, T17 t17, T18 t18, T19 t19, T20 t20, T21 t21, T22 t22, T23 t23, T24 t24, T25 t25);
    }

    @FunctionalInterface
    public interface Function26<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, R> {
        R apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13, T14 t14, T15 t15, T16 t16, T17 t17, T18 t18, T19 t19, T20 t20, T21 t21, T22 t22, T23 t23, T24 t24, T25 t25, T26 t26);
    }

    @FunctionalInterface
    public interface Function27<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, R> {
        R apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13, T14 t14, T15 t15, T16 t16, T17 t17, T18 t18, T19 t19, T20 t20, T21 t21, T22 t22, T23 t23, T24 t24, T25 t25, T26 t26, T27 t27);
    }

    @FunctionalInterface
    public interface Function28<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28, R> {
        R apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13, T14 t14, T15 t15, T16 t16, T17 t17, T18 t18, T19 t19, T20 t20, T21 t21, T22 t22, T23 t23, T24 t24, T25 t25, T26 t26, T27 t27, T28 t28);
    }

    @FunctionalInterface
    public interface Function29<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28, T29, R> {
        R apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13, T14 t14, T15 t15, T16 t16, T17 t17, T18 t18, T19 t19, T20 t20, T21 t21, T22 t22, T23 t23, T24 t24, T25 t25, T26 t26, T27 t27, T28 t28, T29 t29);
    }

    @FunctionalInterface
    public interface Function30<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28, T29, T30, R> {
        R apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13, T14 t14, T15 t15, T16 t16, T17 t17, T18 t18, T19 t19, T20 t20, T21 t21, T22 t22, T23 t23, T24 t24, T25 t25, T26 t26, T27 t27, T28 t28, T29 t29, T30 t30);
    }

    @FunctionalInterface
    public interface Function31<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28, T29, T30, T31, R> {
        R apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13, T14 t14, T15 t15, T16 t16, T17 t17, T18 t18, T19 t19, T20 t20, T21 t21, T22 t22, T23 t23, T24 t24, T25 t25, T26 t26, T27 t27, T28 t28, T29 t29, T30 t30, T31 t31);
    }

    @FunctionalInterface
    public interface Function32<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22, T23, T24, T25, T26, T27, T28, T29, T30, T31, T32, R> {
        R apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13, T14 t14, T15 t15, T16 t16, T17 t17, T18 t18, T19 t19, T20 t20, T21 t21, T22 t22, T23 t23, T24 t24, T25 t25, T26 t26, T27 t27, T28 t28, T29 t29, T30 t30, T31 t31, T32 t32);
    }

}