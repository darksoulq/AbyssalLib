package com.github.darksoulq.abyssallib.world.structure.serializer;

import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import com.github.darksoulq.abyssallib.world.block.BlockEntity;
import com.github.darksoulq.abyssallib.world.block.CustomBlock;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AbyssalLibBlockSerializer {

    public static <D> Map<D, D> serialize(CustomBlock block, DynamicOps<D> ops) {
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
            try {
                D entityData = block.getEntity().serialize(ops);
                map.put(ops.createString("properties"), entityData);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    public static <D> void deserialize(CustomBlock block, Map<D, D> data, DynamicOps<D> ops, BlockData targetData) {
        Map<D, D> states = ops.getMap(data.get(ops.createString("states"))).orElse(Collections.emptyMap());
        Adapter.load(ops, states, targetData);

        D props = data.get(ops.createString("properties"));
        if (props != null) {
            try {
                BlockEntity entity = block.getEntity();
                if (entity == null) {
                    entity = block.createBlockEntity(null);
                    block.setEntity(entity);
                }

                if (entity != null) {
                    entity.deserialize(ops, props);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}