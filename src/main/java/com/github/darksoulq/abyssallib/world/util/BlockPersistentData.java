package com.github.darksoulq.abyssallib.world.util;

import com.github.darksoulq.abyssallib.AbyssalLib;
import org.bukkit.Chunk;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

/**
 * Provides per-block persistent data stored inside the chunk.
 */
public final class BlockPersistentData {
    private static final String PREFIX = "abyssallib_block_";

    private BlockPersistentData() {}

    /**
     * Returns the persistent data container associated with the block.
     *
     * @param block block to access
     * @return persistent data container for that block
     */
    public static PersistentDataContainer get(Block block) {
        Chunk chunk = block.getChunk();
        PersistentDataContainer chunkPDC = chunk.getPersistentDataContainer();
        NamespacedKey key = keyFor(block);

        PersistentDataContainer existing =
                chunkPDC.get(key, PersistentDataType.TAG_CONTAINER);

        if (existing != null) return existing;

        PersistentDataContainer created =
                chunkPDC.getAdapterContext().newPersistentDataContainer();

        chunkPDC.set(key, PersistentDataType.TAG_CONTAINER, created);

        return created;
    }

    /**
     * Removes all persisted data associated with this block.
     *
     * @param block block to clear
     */
    public static void remove(Block block) {
        block.getChunk().getPersistentDataContainer()
                .remove(keyFor(block));
    }

    private static NamespacedKey keyFor(Block block) {
        int lx = block.getX() & 15;
        int lz = block.getZ() & 15;
        return new NamespacedKey(AbyssalLib.getInstance(), PREFIX + lx + "_" + block.getY() + "_" + lz);
    }
}
