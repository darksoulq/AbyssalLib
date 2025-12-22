package com.github.darksoulq.abyssallib.server.bridge.block;

import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.server.bridge.Provider;
import dev.lone.itemsadder.api.CustomBlock;

import java.util.Map;
import java.util.Optional;

public class ItemsAdderProvider extends Provider<BridgeBlock<?>> {
    public ItemsAdderProvider() {
        super("ia");
    }

    @Override
    public boolean belongs(BridgeBlock<?> value) {
        return CustomBlock.isInRegistry(Identifier.of(value.id().getNamespace(), value.id().getPath()).toString());
    }

    @Override
    public Identifier getId(BridgeBlock<?> value) {
        return Identifier.of(value.id().getNamespace(), value.id().getPath());
    }

    @Override
    public BridgeBlock<?> get(Identifier id) {
        CustomBlock block = CustomBlock.getInstance(id.toString());
        if (block == null) return null;
        return new BridgeBlock<>(id, getPrefix(), block);
    }

    @Override
    public Map<String, Optional<Object>> serializeData(BridgeBlock<?> value, DynamicOps<?> ops) {
        return Map.of();
    }

    @Override
    public <T> void deserializeData(Map<String, Optional<T>> data, BridgeBlock<?> value, DynamicOps<T> ops) {}
}
