package com.github.darksoulq.abyssallib.world.gen;

import com.github.darksoulq.abyssallib.world.block.CustomBlock;
import org.bukkit.HeightMap;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public interface WorldGenAccess {
    void setBlock(int x, int y, int z, @NotNull Material material);
    void setBlock(int x, int y, int z, @NotNull BlockData data);
    void setBlock(int x, int y, int z, @NotNull CustomBlock block);
    void setBlock(int x, int y, int z, @NotNull CustomBlock block, @NotNull BlockData data);
    @NotNull Material getType(int x, int y, int z);
    @NotNull BlockData getBlockData(int x, int y, int z);
    int getHighestBlockY(int x, int z, HeightMap heightMap);
    @NotNull World getWorld();
    @NotNull Random getRandom();
}