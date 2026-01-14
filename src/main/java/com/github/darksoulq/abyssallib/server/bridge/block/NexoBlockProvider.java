package com.github.darksoulq.abyssallib.server.bridge.block;

import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.server.bridge.BlockProvider;
import com.github.darksoulq.abyssallib.server.bridge.BridgeBlock;
import com.nexomc.nexo.api.NexoBlocks;
import com.nexomc.nexo.mechanics.custom_block.CustomBlockMechanic;
import org.bukkit.Location;

import java.util.Map;

public class NexoBlockProvider extends BlockProvider<CustomBlockMechanic> {
    public NexoBlockProvider() {
        super("nexo");
    }

    @Override
    public Identifier getId(BridgeBlock<CustomBlockMechanic> value) {
        return Identifier.of(value.id().getNamespace(), value.id().getPath());
    }

    @Override
    public BridgeBlock<CustomBlockMechanic> get(Identifier id) {
        String registryKey = id.getNamespace() + ":" + id.getPath();
        CustomBlockMechanic block = NexoBlocks.customBlockMechanic(registryKey);
        if (block == null) return null;
        return new BridgeBlock<>(id, getPrefix(), block) {
            @Override
            public void place(Location location) {
                NexoBlocks.place(id.toString(), location);
            }
        };
    }

    // TODO: Figure out how the fuck i can set/get states for Nexo Blocks

    @Override
    public <D> Map<D, D> serializeData(CustomBlockMechanic value, DynamicOps<D> ops) throws Exception {
        return Map.of();
    }

    @Override
    public <D> BridgeBlock<CustomBlockMechanic> deserializeData(Map<D, D> data, BridgeBlock<CustomBlockMechanic> value, DynamicOps<D> ops) {
        return value;
    }
}
