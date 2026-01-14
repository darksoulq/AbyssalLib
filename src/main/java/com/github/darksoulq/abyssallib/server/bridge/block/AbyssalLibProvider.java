package com.github.darksoulq.abyssallib.server.bridge.block;

import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.server.bridge.BlockProvider;
import com.github.darksoulq.abyssallib.server.bridge.BridgeBlock;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.block.CustomBlock;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Waterlogged;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AbyssalLibProvider extends BlockProvider<CustomBlock> {
    public AbyssalLibProvider() {
        super("abyssallib");
    }

    @Override
    public Identifier getId(BridgeBlock<CustomBlock> value) {
        return Identifier.of(value.id().getNamespace(), value.id().getPath());
    }

    @Override
    public BridgeBlock<CustomBlock> get(Identifier id) {
        String registryKey = id.getNamespace() + ":" + id.getPath();
        CustomBlock block = Registries.BLOCKS.get(registryKey);
        if (block == null) return null;

        return new BridgeBlock<>(id, getPrefix(), block.clone()) {
            @Override
            public void place(Location location) {
                BlockData bd = value.getMaterial().createBlockData();
                if (location.getBlock().getType() == Material.WATER && bd instanceof Waterlogged wl) {
                    wl.setWaterlogged(true);
                }
                location.getWorld().setBlockData(location, bd);

                Block block = location.getBlock();
                if (block.isEmpty()) return;
                value.place(block, false);
            }
        };
    }

    @Override
    public <D> Map<D, D> serializeData(CustomBlock block, DynamicOps<D> ops) throws Exception {
        Map<D, D> map = new HashMap<>();
        BlockData bd = block.getMaterial().createBlockData();
        Location loc = block.getLocation();
        if (loc != null) {
            Block placed = loc.getBlock();
            if (!placed.isEmpty()) {
                bd = placed.getBlockData();
            }
        }

        Map<D, D> statesMap = Adapter.save(ops, bd);
        map.put(ops.createString("states"), ops.createMap(statesMap));
        if (block.getEntity() != null) {
            D entityData = block.getEntity().serialize(ops);
            map.put(ops.createString("properties"), entityData);
        }

        return map;
    }

    @Override
    public <D> BridgeBlock<CustomBlock> deserializeData(Map<D, D> data, BridgeBlock<CustomBlock> value, DynamicOps<D> ops) {
        if (!(value.value() instanceof CustomBlock block)) return null;

        Map<D, D> states = ops.getMap(data.get(ops.createString("states"))).orElse(Collections.emptyMap());

        return new BridgeBlock<>(block.getId(), value.provider(), block.clone()) {
            @Override
            public void place(Location location) throws Exception {
                BlockData bd = value.getMaterial().createBlockData();
                Adapter.load(ops, states, bd);

                if (location.getBlock().getType() == Material.WATER && bd instanceof Waterlogged wl) {
                    wl.setWaterlogged(true);
                }
                location.getWorld().setBlockData(location, bd);

                Block placedBlock = location.getBlock();
                if (placedBlock.isEmpty()) return;

                value.place(placedBlock, false);

                if (value.getEntity() != null) {
                    D props = data.get(ops.createString("properties"));
                    if (props != null) {
                        value.getEntity().deserialize(ops, props);
                    }
                }
            }
        };
    }
}