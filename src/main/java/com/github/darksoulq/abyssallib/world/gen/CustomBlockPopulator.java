package com.github.darksoulq.abyssallib.world.gen;

import com.github.darksoulq.abyssallib.world.gen.nms.NMSWorldGenAccess;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public abstract class CustomBlockPopulator extends BlockPopulator {

    @Override
    public void populate(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull LimitedRegion limitedRegion) {
        WorldGenAccess access = new NMSWorldGenAccess(limitedRegion, limitedRegion.getWorld(), random);
        generate(access, chunkX, chunkZ, random);
    }

    /**
     * Implement this method to generate your features.
     *
     * @param level  The accessor for modifying the world.
     * @param chunkX The X coordinate of the chunk being generated.
     * @param chunkZ The Z coordinate of the chunk being generated.
     * @param random The random source.
     */
    public abstract void generate(WorldGenAccess level, int chunkX, int chunkZ, Random random);
}