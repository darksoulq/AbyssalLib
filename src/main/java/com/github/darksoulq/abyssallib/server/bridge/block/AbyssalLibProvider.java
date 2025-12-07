package com.github.darksoulq.abyssallib.server.bridge.block;

import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.server.bridge.Provider;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.block.CustomBlock;

import java.util.Map;
import java.util.Optional;

public class AbyssalLibProvider extends Provider<BridgeBlock<?>> {
    public AbyssalLibProvider() {
        super("abyssallib");
    }

    @Override
    public boolean belongs(BridgeBlock<?> value) {
        return value.value() instanceof CustomBlock;
    }

    @Override
    public Identifier getId(BridgeBlock<?> value) {
        return Identifier.of(value.id().getNamespace(), value.id().getPath());
    }

    @Override
    public BridgeBlock<?> get(Identifier id) {
        return new BridgeBlock<>(id, getPrefix(), Registries.BLOCKS.get(id.toString()).clone());
    }

    @Override
    public void deserializeData(Map<String, Optional<Object>> data, BridgeBlock<?> value) {

    }

    @Override
    public Map<String, Optional<Object>> serializeData(BridgeBlock<?> value) {
        return Map.of();
    }
}
