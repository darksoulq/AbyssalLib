package com.github.darksoulq.abyssallib.common.serialization.internal.block_data;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.util.Try;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Adapter<T> {
    private static final Map<String, List<Adapter<?>>> ADAPTERS = new HashMap<>();
    public static final Map<Material, BlockData> BASE_CACHE = new HashMap<>();

    public abstract boolean doesApply(BlockData data);
    public abstract <D> D serialize(DynamicOps<D> ops, T value) throws Codec.CodecException;
    public abstract <D> void deserialize(DynamicOps<D> ops, D input, BlockData base) throws Codec.CodecException;

    @SuppressWarnings("unchecked")
    private static <D, T> D serialize(Adapter<T> adapter, DynamicOps<D> ops, BlockData data) throws Codec.CodecException {
        return adapter.serialize(ops, (T) data);
    }

    public static <D> Map<D, D> save(DynamicOps<D> ops, BlockData data) {
        Map<D, D> values = new HashMap<>();
        for (Map.Entry<String, List<Adapter<?>>> entry : ADAPTERS.entrySet()) {
            for (Adapter<?> adapter : entry.getValue()) {
                if (!adapter.doesApply(data)) continue;
                D serialized = Try.of(() -> serialize(adapter, ops, data))
                    .orElse(null);

                if (serialized != null) {
                    values.put(ops.createString(entry.getKey()), serialized);
                    break;
                }
            }
        }
        return values;
    }

    public static <D> void load(DynamicOps<D> ops, Map<D, D> input, BlockData base) {
        for (Map.Entry<D, D> entry : input.entrySet()) {
            String key = Try.of(() -> ops.getStringValue(entry.getKey()).orElseThrow())
                .orElse(null);
            if (key == null) continue;
            List<Adapter<?>> adapters = ADAPTERS.get(key);
            if (adapters == null) continue;

            for (Adapter<?> adapter : adapters) {
                if (!adapter.doesApply(base)) continue;
                if (Try.run(() -> adapter.deserialize(ops, entry.getValue(), base)).isSuccess()) {
                    break;
                }
            }
        }
    }

    public static void register(String key, Adapter<?> adapter) {
        ADAPTERS.put(key, List.of(adapter));
    }

    public static void register(String key, Adapter<?>... adapters) {
        ADAPTERS.put(key, List.of(adapters));
    }
}