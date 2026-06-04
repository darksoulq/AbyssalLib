package com.github.darksoulq.abyssallib.common.serialization.internal.block_data;

import com.github.darksoulq.abyssallib.common.serialization.DataError;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

import java.util.*;

public abstract class Adapter<T> {

    private static final Map<String, List<Adapter<?>>> ADAPTERS = new HashMap<>();
    public static final Map<Material, BlockData> BASE_CACHE = new HashMap<>();

    public abstract boolean doesApply(BlockData data);

    public abstract <D> DataResult<D> serialize(DynamicOps<D> ops, T value);

    public abstract <D> DataResult<Void> deserialize(DynamicOps<D> ops, D input, BlockData base);

    @SuppressWarnings("unchecked")
    private static <D, T> DataResult<D> serialize(Adapter<T> adapter, DynamicOps<D> ops, BlockData data) {
        return adapter.serialize(ops, (T) data);
    }

    public static <D> DataResult<Map<D, D>> save(DynamicOps<D> ops, BlockData data) {
        Map<D, D> values = new HashMap<>();
        List<DataError> warnings = new ArrayList<>();

        for (Map.Entry<String, List<Adapter<?>>> entry : ADAPTERS.entrySet()) {
            for (Adapter<?> adapter : entry.getValue()) {
                if (!adapter.doesApply(data)) continue;

                DataResult<D> serializedRes = serialize(adapter, ops, data);
                if (serializedRes.isSuccess()) {
                    values.put(ops.createString(entry.getKey()), serializedRes.getOrThrow());
                    if (serializedRes.isPartial()) {
                        warnings.addAll(serializedRes.warnings());
                    }
                    break;
                } else {
                    warnings.add(serializedRes.dataError().orElseGet(() -> DataError.custom(serializedRes.error().get())));
                }
            }
        }

        return warnings.isEmpty() ? DataResult.success(values) : DataResult.partial(values, warnings);
    }

    public static <D> DataResult<Void> load(DynamicOps<D> ops, Map<D, D> input, BlockData base) {
        List<DataError> warnings = new ArrayList<>();

        for (Map.Entry<D, D> entry : input.entrySet()) {
            Optional<String> keyOpt = ops.getStringValue(entry.getKey());
            if (keyOpt.isEmpty()) {
                warnings.add(DataError.custom("Invalid key format in BlockData map"));
                continue;
            }

            String key = keyOpt.get();
            List<Adapter<?>> adapters = ADAPTERS.get(key);
            if (adapters == null) continue;

            for (Adapter<?> adapter : adapters) {
                if (!adapter.doesApply(base)) continue;

                DataResult<Void> deserializedRes = adapter.deserialize(ops, entry.getValue(), base);
                if (deserializedRes.isSuccess()) {
                    if (deserializedRes.isPartial()) {
                        warnings.addAll(deserializedRes.warnings());
                    }
                    break;
                } else {
                    warnings.add(deserializedRes.dataError().orElseGet(() -> DataError.custom(deserializedRes.error().get())));
                }
            }
        }

        return warnings.isEmpty() ? DataResult.success((Void) null) : DataResult.partial(null, warnings);
    }

    public static void register(String key, Adapter<?>... adapters) {
        ADAPTERS.put(key, List.of(adapters));
    }
}