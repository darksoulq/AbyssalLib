package com.github.darksoulq.abyssallib.world.gen;

import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

/**
 * An abstract extension of Bukkit's block populator that integrates
 * with the AbyssalLib world generation API.
 */
public abstract class CustomBlockPopulator extends BlockPopulator {

    /**
     * Standard Bukkit entry point for chunk population.
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
     *
     * @param level  The world generation access wrapper.
     * @param chunkX The X coordinate of the chunk being generated.
     * @param chunkZ The Z coordinate of the chunk being generated.
     * @param random The random source for this generation pass.
     */
    public abstract void generate(WorldGenAccess level, int chunkX, int chunkZ, Random random);
}