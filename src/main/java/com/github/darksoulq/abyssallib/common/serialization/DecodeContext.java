package com.github.darksoulq.abyssallib.common.serialization;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class DecodeContext<D> {

    private final DynamicOps<D> ops;
    private final Map<D, D> map;
    private final List<DataError> errors = new ArrayList<>();
    private final List<DataError> warnings = new ArrayList<>();

    private DecodeContext(DynamicOps<D> ops, Map<D, D> map) {
        this.ops = ops;
        this.map = map;
    }

    public static <D> DecodeContext<D> of(DynamicOps<D> ops, Map<D, D> map) {
        return new DecodeContext<>(ops, map);
    }

    public <T> DecodeContext<D> read(String key, Codec<T> codec, Consumer<T> action) {
        D data = map.get(ops.createString(key));
        if (data == null) {
            errors.add(DataError.missingField(key));
            return this;
        }

        DataResult<T> res = codec.decode(ops, data).prependPath(key);
        if (res.isError()) {
            errors.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
        } else {
            try {
                action.accept(res.getOrThrow());
            } catch (Exception e) {
                errors.add(DataError.custom("Failed to apply value for '" + key + "': " + e.getMessage()));
            }
            if (res.isPartial()) {
                warnings.addAll(res.warnings());
            }
        }
        return this;
    }

    public <T> DecodeContext<D> readNullable(String key, Codec<T> codec, Consumer<T> action) {
        D data = map.get(ops.createString(key));
        if (data == null) {
            try { action.accept(null); } catch (Exception e) { errors.add(DataError.custom(e.getMessage())); }
            return this;
        }

        DataResult<T> res = codec.decode(ops, data).prependPath(key);
        if (res.isError()) {
            errors.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
        } else {
            try { action.accept(res.getOrThrow()); } catch (Exception e) { errors.add(DataError.custom(e.getMessage())); }
            if (res.isPartial()) warnings.addAll(res.warnings());
        }
        return this;
    }

    public <T> DecodeContext<D> readOptional(String key, Codec<T> codec, Consumer<Optional<T>> action) {
        D data = map.get(ops.createString(key));
        if (data == null) {
            try { action.accept(Optional.empty()); } catch (Exception e) { errors.add(DataError.custom(e.getMessage())); }
            return this;
        }

        DataResult<T> res = codec.decode(ops, data).prependPath(key);
        if (res.isError()) {
            errors.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
        } else {
            try { action.accept(Optional.of(res.getOrThrow())); } catch (Exception e) { errors.add(DataError.custom(e.getMessage())); }
            if (res.isPartial()) warnings.addAll(res.warnings());
        }
        return this;
    }

    public <T> DecodeContext<D> readOrElse(String key, Codec<T> codec, T defaultValue, Consumer<T> action) {
        D data = map.get(ops.createString(key));
        if (data == null) {
            try { action.accept(defaultValue); } catch (Exception e) { errors.add(DataError.custom(e.getMessage())); }
            return this;
        }

        DataResult<T> res = codec.decode(ops, data).prependPath(key);
        if (res.isError()) {
            errors.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
        } else {
            try { action.accept(res.getOrThrow()); } catch (Exception e) { errors.add(DataError.custom(e.getMessage())); }
            if (res.isPartial()) warnings.addAll(res.warnings());
        }
        return this;
    }

    public DataResult<Void> result() {
        if (!errors.isEmpty()) {
            return DataResult.error(errors.get(0));
        }
        return warnings.isEmpty() ? DataResult.success(null) : DataResult.partial(null, warnings);
    }
}