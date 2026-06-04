package com.github.darksoulq.abyssallib.common.serialization.internal.entity;

import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class EntityAdapter<T> {

    private static final List<EntityAdapter<?>> ADAPTERS = new ArrayList<>();

    public abstract boolean doesApply(Entity entity);

    public abstract <D> DataResult<Void> serialize(DynamicOps<D> ops, T value, Map<D, D> map);

    public abstract <D> DataResult<Void> deserialize(DynamicOps<D> ops, Map<D, D> map, Entity base);

    @SuppressWarnings("unchecked")
    private static <D, T> DataResult<Void> serialize(EntityAdapter<T> adapter, DynamicOps<D> ops, Entity entity, Map<D, D> map) {
        return adapter.serialize(ops, (T) entity, map);
    }

    public static <D> DataResult<Map<D, D>> save(DynamicOps<D> ops, Entity entity) {
        Map<D, D> values = new HashMap<>();
        List<DataError> warnings = new ArrayList<>();

        for (EntityAdapter<?> adapter : ADAPTERS) {
            if (adapter.doesApply(entity)) {
                DataResult<Void> res = serialize(adapter, ops, entity, values);
                if (res.isError()) {
                    warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
                } else if (res.isPartial()) {
                    warnings.addAll(res.warnings());
                }
            }
        }

        return warnings.isEmpty() ? DataResult.success(values) : DataResult.partial(values, warnings);
    }

    public static <D> DataResult<Void> load(DynamicOps<D> ops, Map<D, D> input, Entity base) {
        List<DataError> warnings = new ArrayList<>();

        for (EntityAdapter<?> adapter : ADAPTERS) {
            if (adapter.doesApply(base)) {
                DataResult<Void> res = adapter.deserialize(ops, input, base);
                if (res.isError()) {
                    warnings.add(res.dataError().orElseGet(() -> DataError.custom(res.error().get())));
                } else if (res.isPartial()) {
                    warnings.addAll(res.warnings());
                }
            }
        }

        return warnings.isEmpty() ? DataResult.success((Void) null) : DataResult.partial(null, warnings);
    }

    public static void register(EntityAdapter<?> adapter) {
        ADAPTERS.add(adapter);
    }
}