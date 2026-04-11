package com.github.darksoulq.abyssallib.common.serialization;

import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import com.github.darksoulq.abyssallib.world.block.BlockEntity;
import com.github.darksoulq.abyssallib.world.block.CustomBlock;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

import java.util.Map;

/**
 * Utility class responsible for serializing and deserializing {@link CustomBlock}
 * state and associated {@link BlockEntity} data.
 *
 * <p>This serializer handles two distinct layers:</p>
 * <ul>
 *     <li><b>Block states</b> — the visual {@link BlockData}</li>
 *     <li><b>Block entity data</b> — custom persistent logic</li>
 * </ul>
 *
 * <p>All operations are performed using {@link DynamicOps} to remain format-agnostic.</p>
 */
public final class AbyssalLibBlockSerializer {

    private AbyssalLibBlockSerializer() {}

    /**
     * Serializes the current {@link BlockData} state of a {@link CustomBlock}.
     *
     * <p>If the block is placed in the world, the live {@link BlockData} from the
     * world is used. Otherwise, the default material state is used.</p>
     *
     * @param block the custom block instance
     * @param ops   the dynamic operations instance used for encoding
     * @param <D>   the encoded data type
     * @return a map containing serialized block state properties, never null
     */
    public static <D> Map<D, D> serializeStates(CustomBlock block, DynamicOps<D> ops) {
        BlockData bd = block.getMaterial().createBlockData();
        Location loc = block.getLocation();

        if (loc != null) {
            Block placed = loc.getBlock();
            if (!placed.isEmpty()) {
                bd = placed.getBlockData();
            }
        }

        return Adapter.save(ops, bd);
    }

    /**
     * Serializes the associated {@link BlockEntity} of a {@link CustomBlock}.
     *
     * <p>If no entity exists, {@code null} is returned.</p>
     *
     * @param block the custom block instance
     * @param ops   the dynamic operations instance used for encoding
     * @param <D>   the encoded data type
     * @return the serialized entity data, or {@code null} if no entity is present
     */
    public static <D> D serializeProperties(CustomBlock block, DynamicOps<D> ops) {
        if (block.getEntity() != null) {
            try {
                return block.getEntity().serialize(ops);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Applies serialized block state data onto an existing {@link BlockData} instance.
     *
     * <p>If the provided data is {@code null}, this method does nothing.</p>
     *
     * @param data       the serialized block state map
     * @param ops        the dynamic operations instance used for decoding
     * @param targetData the target block data to mutate
     * @param <D>        the encoded data type
     */
    public static <D> void deserializeBlockData(Map<D, D> data, DynamicOps<D> ops, BlockData targetData) {
        if (data == null) return;
        Adapter.load(ops, data, targetData);
    }

    /**
     * Deserializes and applies block entity data onto a {@link CustomBlock}.
     *
     * <p>If the block does not currently have a {@link BlockEntity}, one will be created
     * via {@link CustomBlock#createBlockEntity(Location)}.</p>
     *
     * <p>If {@code props} is {@code null}, this method does nothing.</p>
     *
     * @param block the target custom block
     * @param props the serialized entity data
     * @param ops   the dynamic operations instance used for decoding
     * @param <D>   the encoded data type
     */
    public static <D> void deserializeEntity(CustomBlock block, D props, DynamicOps<D> ops) {
        if (props == null) return;

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