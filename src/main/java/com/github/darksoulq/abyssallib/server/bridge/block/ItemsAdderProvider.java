package com.github.darksoulq.abyssallib.server.bridge.block;

import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.server.bridge.BlockProvider;
import com.github.darksoulq.abyssallib.server.bridge.BridgeBlock;
import dev.lone.itemsadder.api.CustomBlock;
import org.bukkit.Location;

import java.util.Map;

public class ItemsAdderProvider extends BlockProvider<CustomBlock> {
    public ItemsAdderProvider() {
        super("ia");
    }

    @Override
    public Identifier getId(BridgeBlock<CustomBlock> value) {
        return Identifier.of(value.id().getNamespace(), value.id().getPath());
    }

    @Override
    public BridgeBlock<CustomBlock> get(Identifier id) {
        String registryKey = id.getNamespace() + ":" + id.getPath();
        CustomBlock block = CustomBlock.getInstance(registryKey);
        if (block == null) return null;
        return new BridgeBlock<>(id, getPrefix(), block) {
            @Override
            public void place(Location location) {
                block.place(location);
            }
        };
    }

    @Override
    public <D> Map<D, D> serializeData(CustomBlock value, DynamicOps<D> ops) throws Exception {
        return Map.of();
    }

    @Override
    public <D> BridgeBlock<CustomBlock> deserializeData(Map<D, D> data, BridgeBlock<CustomBlock> value, DynamicOps<D> ops) {
        return value;
    }
}
