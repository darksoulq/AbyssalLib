package com.github.darksoulq.abyssallib.server.bridge;

import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.util.Identifier;

import java.util.Map;

public abstract class BlockProvider<T> {
    private final String prefix;
    public BlockProvider(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }

    public abstract Identifier getId(BridgeBlock<T> value);
    public abstract BridgeBlock<T> get(Identifier id);
    public abstract <D> Map<D, D> serializeData(T value, DynamicOps<D> ops) throws Exception;
    public abstract <D> BridgeBlock<T> deserializeData(Map<D, D> data, BridgeBlock<T> value, DynamicOps<D> ops);
}
