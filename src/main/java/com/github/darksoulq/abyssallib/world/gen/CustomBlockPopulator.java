package com.github.darksoulq.abyssallib.world.gen;

import com.github.darksoulq.abyssallib.world.gen.nms.NMSWorldGenAccess;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

/**
 * An abstract extension of Bukkit's {@link BlockPopulator} that integrates
 * with the AbyssalLib world generation API.
 * <p>
 * Instead of dealing with the raw {@link LimitedRegion}, implementations of this class
 * use {@link WorldGenAccess}, which provides cleaner methods for placing custom
 * blocks and managing world state.
 * </p>
 */
public abstract class CustomBlockPopulator extends BlockPopulator {

    /**
     * Standard Bukkit entry point for chunk population.
     * <p>
     * This method wraps the provided {@code limitedRegion} into an
     * {@link NMSWorldGenAccess} and delegates the logic to
     * {@link #generate(WorldGenAccess, int, int, Random)}.
     * </p>
     *
     * @param worldInfo     Information about the world.
     * @param random        The random source for this chunk.
     * @param chunkX        The X coordinate of the chunk.
     * @param chunkZ        The Z coordinate of the chunk.
     * @param limitedRegion The region restricted to the chunk and its neighbors.
     */
    @Override
    public void populate(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull LimitedRegion limitedRegion) {
        WorldGenAccess access = new NMSWorldGenAccess(limitedRegion, limitedRegion.getWorld(), random);
        generate(access, chunkX, chunkZ, random);
    }

    /**
     * Implement this method to define custom features, ores, or structures.
     * <p>
     * This method is called during the population phase, allowing for safe block
     * modification within the bounds of the provided accessor.
     * </p>
     *
     * @param level  The {@link WorldGenAccess} used to modify or query the world.
     * @param chunkX The X coordinate of the chunk being generated.
     * @param chunkZ The Z coordinate of the chunk being generated.
     * @param random The {@link Random} source for this generation pass.
     */
    public abstract void generate(WorldGenAccess level, int chunkX, int chunkZ, Random random);
}