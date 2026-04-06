package com.github.darksoulq.abyssallib.common.serialization;

import com.github.darksoulq.abyssallib.common.serialization.internal.block_data.Adapter;
import com.github.darksoulq.abyssallib.common.serialization.internal.tile_state.TileAdapter;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.block.data.BlockData;

import java.util.Map;

/**
 * Provides a complete utility layer for serializing and deserializing native Bukkit
 * {@link BlockData} and {@link TileState} instances into generic data representations
 * via {@link DynamicOps}.
 *
 * <p>This class acts as a low-level bridge between Minecraft's internal block state
 * system and AbyssalLib's abstract serialization framework, enabling:
 * <ul>
 *     <li>Conversion of block states into generic map-based formats</li>
 *     <li>Reconstruction of block states from serialized data</li>
 *     <li>Extraction and restoration of tile entity (NBT-like) metadata</li>
 * </ul>
 *
 * <p>All operations are stateless and thread-safe provided the underlying
 * {@link DynamicOps} implementation is thread-safe.
 */
public class MinecraftBlockSerializer {

    /**
     * Serializes a {@link BlockData} instance into a generic key-value map using
     * the provided {@link DynamicOps} format.
     *
     * <p>This includes all block state properties such as:
     * <ul>
     *     <li>Facing direction</li>
     *     <li>Power levels</li>
     *     <li>Waterlogged state</li>
     *     <li>Any other block-specific properties</li>
     * </ul>
     *
     * @param data The {@link BlockData} instance to serialize. Must not be null.
     * @param ops  The {@link DynamicOps} implementation defining the output format
     *             (e.g., JSON, NBT-like structures).
     * @param <D>  The generic data type used by the {@link DynamicOps} instance.
     *
     * @return A map representing the serialized block state. Never null, but may be empty
     *         if the block has no configurable properties.
     */
    public static <D> Map<D, D> serialize(BlockData data, DynamicOps<D> ops) {
        return Adapter.save(ops, data);
    }

    /**
     * Applies serialized block state data onto an existing {@link BlockData} instance.
     *
     * <p>This method mutates the provided {@code data} object by applying all properties
     * found in the given map. Any missing properties are left unchanged.
     *
     * @param data The target {@link BlockData} instance to modify. Must not be null.
     * @param map  The serialized property map previously produced by {@link #serialize}.
     *             May be null or empty, in which case no changes are applied.
     * @param ops  The {@link DynamicOps} instance used to interpret the map values.
     * @param <D>  The generic data type used by the {@link DynamicOps} instance.
     */
    public static <D> void deserialize(BlockData data, Map<D, D> map, DynamicOps<D> ops) {
        if (map == null || map.isEmpty()) return;
        Adapter.load(ops, map, data);
    }

    /**
     * Serializes tile entity data (block entity / NBT-like metadata) from a {@link BlockState}.
     *
     * <p>This method only operates on instances of {@link TileState}. If the provided
     * state is not a tile entity, this method returns {@code null}.
     *
     * <p>Examples of supported tile states include:
     * <ul>
     *     <li>Chests</li>
     *     <li>Furnaces</li>
     *     <li>Signs</li>
     *     <li>Containers with persistent metadata</li>
     * </ul>
     *
     * @param state The {@link BlockState} to extract tile data from.
     * @param ops   The {@link DynamicOps} implementation defining the output format.
     * @param <D>   The generic data type used by the {@link DynamicOps} instance.
     *
     * @return A map containing serialized tile data, or {@code null} if:
     *         <ul>
     *             <li>The state is not a {@link TileState}</li>
     *             <li>The tile contains no serializable data</li>
     *         </ul>
     */
    public static <D> Map<D, D> serializeTile(BlockState state, DynamicOps<D> ops) {
        if (!(state instanceof TileState tileState)) return null;

        Map<D, D> mapped = TileAdapter.save(ops, tileState);
        return mapped.isEmpty() ? null : mapped;
    }

    /**
     * Restores tile entity data onto a {@link BlockState} from a serialized map.
     *
     * <p>This method only applies to {@link TileState} instances. If the provided
     * state is not a tile entity, the method safely exits without performing any action.
     *
     * <p>The provided data should originate from {@link #serializeTile(BlockState, DynamicOps)}
     * to ensure compatibility.
     *
     * @param state The target {@link BlockState} to apply tile data to.
     * @param data  The serialized tile data map. May be null or empty, in which case
     *              no changes are applied.
     * @param ops   The {@link DynamicOps} instance used to interpret the serialized values.
     * @param <D>   The generic data type used by the {@link DynamicOps} instance.
     */
    public static <D> void deserializeTile(BlockState state, Map<D, D> data, DynamicOps<D> ops) {
        if (data == null || data.isEmpty()) return;
        if (!(state instanceof TileState tileState)) return;

        TileAdapter.load(ops, data, tileState);
        tileState.update(true, false);
    }
}