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

/**
 * Handles serialization and deserialization for AbyssalLib {@link CustomBlock} objects.
 * <p>
 * This utility bridges standard Minecraft visual states with AbyssalLib's custom
 * property system, allowing for the persistence of custom block logic and metadata.
 */
public class AbyssalLibBlockSerializer {

    /**
     * Serializes a {@link CustomBlock} into a dynamic map structure.
     * <p>
     * The process captures two distinct datasets:
     * <ul>
     * <li><b>States:</b> Standard Bukkit {@link BlockData} properties (e.g., rotation).</li>
     * <li><b>Properties:</b> The serialized data from the associated {@link BlockEntity}.</li>
     * </ul>
     *
     * @param block The custom block instance to serialize.
     * @param ops   The {@link DynamicOps} instance for data conversion.
     * @param <D>   The data format type.
     * @return A map containing both standard block states and custom properties.
     */
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

    /**
     * Extracts and applies standard block states from a serialized data map.
     *
     * @param data       The serialized data map.
     * @param ops        The {@link DynamicOps} instance.
     * @param targetData The {@link BlockData} instance to populate.
     * @param <D>        The data format type.
     */
    public static <D> void deserializeBlockData(Map<D, D> data, DynamicOps<D> ops, BlockData targetData) {
        if (data == null) return;
        Map<D, D> states = ops.getMap(data.get(ops.createString("states"))).orElse(Collections.emptyMap());
        Adapter.load(ops, states, targetData);
    }

    /**
     * Extracts and applies custom entity properties to a {@link CustomBlock}.
     * <p>
     * This method ensures that the block has an associated {@link BlockEntity}
     * before delegating the property restoration to the entity's own deserialization logic.
     *
     * @param block The custom block instance to update.
     * @param data  The serialized data map.
     * @param ops   The {@link DynamicOps} instance.
     * @param <D>   The data format type.
     */
    public static <D> void deserializeEntity(CustomBlock block, Map<D, D> data, DynamicOps<D> ops) {
        if (data == null) return;
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