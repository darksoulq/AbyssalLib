package com.github.darksoulq.abyssallib.common.serialization;

import java.util.*;

public class EncodeContext<D> {

    private final DynamicOps<D> ops;
    private final Map<D, D> map;
    private final List<DataError> errors = new ArrayList<>();
    private final List<DataError> warnings = new ArrayList<>();

    private EncodeContext(DynamicOps<D> ops, Map<D, D> map) {
        this.ops = ops;
        this.map = map;
    }

    public static <D> EncodeContext<D> of(DynamicOps<D> ops, Map<D, D> map) {
        return new EncodeContext<>(ops, map);
    }

    public static <D> EncodeContext<D> of(DynamicOps<D> ops) {
        return new EncodeContext<>(ops, new LinkedHashMap<>());
    }

    public <T> EncodeContext<D> write(String key, Codec<T> codec, T value) {
        if (value == null) {
            errors.add(DataError.nullValue(key));
            return this;
        }

        DataResult<D> res = codec.encode(ops, value).prependPath(key);
        if (res.isError()) {
            errors.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
        } else {
            map.put(ops.createString(key), res.getOrThrow());
            if (res.isPartial()) {
                warnings.addAll(res.warnings());
            }
        }
        return this;
    }

    public <T> EncodeContext<D> writeNullable(String key, Codec<T> codec, T value) {
        if (value != null) {
            return write(key, codec, value);
        }
        return this;
    }

    public <T> EncodeContext<D> writeOptional(String key, Codec<T> codec, Optional<T> value) {
        value.ifPresent(t -> write(key, codec, t));
        return this;
    }

    public <T> EncodeContext<D> writeOrElse(String key, Codec<T> codec, T value, T defaultValue) {
        if (value != null && !value.equals(defaultValue)) {
            return write(key, codec, value);
        }
        return this;
    }

    public DataResult<D> result() {
        if (!errors.isEmpty()) {
            return DataResult.error(errors.get(0));
        }
        D out = ops.createMap(map);
        return warnings.isEmpty() ? DataResult.success(out) : DataResult.partial(out, warnings);
    }
}