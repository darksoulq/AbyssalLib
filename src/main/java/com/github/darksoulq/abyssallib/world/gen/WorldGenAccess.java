package com.github.darksoulq.abyssallib.world.gen;

import com.github.darksoulq.abyssallib.world.block.CustomBlock;
import org.bukkit.HeightMap;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

/**
 * A unified interface providing access to world modification and interrogation
 * during the generation and population phases.
 * <p>
 * This interface abstracts away the differences between generating in a
 * {@link org.bukkit.generator.LimitedRegion} and a standard {@link World},
 * supporting the placement of AbyssalLib {@link CustomBlock}s.
 * </p>
 */
public interface WorldGenAccess {

    /**
     * Sets the block at the specified coordinates to a standard Material.
     *
     * @param x        The absolute X coordinate.
     * @param y        The absolute Y coordinate.
     * @param z        The absolute Z coordinate.
     * @param material The {@link Material} to place.
     */
    void setBlock(int x, int y, int z, @NotNull Material material);

    /**
     * Sets the block at the specified coordinates using specific BlockData.
     *
     * @param x    The absolute X coordinate.
     * @param y    The absolute Y coordinate.
     * @param z    The absolute Z coordinate.
     * @param data The {@link BlockData} containing state information.
     */
    void setBlock(int x, int y, int z, @NotNull BlockData data);

    /**
     * Sets the block at the specified coordinates to a CustomBlock.
     *
     * @param x     The absolute X coordinate.
     * @param y     The absolute Y coordinate.
     * @param z     The absolute Z coordinate.
     * @param block The {@link CustomBlock} instance to place.
     */
    void setBlock(int x, int y, int z, @NotNull CustomBlock block);

    /**
     * Sets a CustomBlock with specific BlockData at the specified coordinates.
     *
     * @param x     The absolute X coordinate.
     * @param y     The absolute Y coordinate.
     * @param z     The absolute Z coordinate.
     * @param block The {@link CustomBlock} instance.
     * @param data  The {@link BlockData} state to apply.
     */
    void setBlock(int x, int y, int z, @NotNull CustomBlock block, @NotNull BlockData data);

    /**
     * Retrieves the Material type at the specified coordinates.
     *
     * @param x The absolute X coordinate.
     * @param y The absolute Y coordinate.
     * @param z The absolute Z coordinate.
     * @return The {@link Material} at the position.
     */
    @NotNull Material getType(int x, int y, int z);

    /**
     * Retrieves the full BlockData at the specified coordinates.
     *
     * @param x The absolute X coordinate.
     * @param y The absolute Y coordinate.
     * @param z The absolute Z coordinate.
     * @return The {@link BlockData} at the position.
     */
    @NotNull BlockData getBlockData(int x, int y, int z);

    /**
     * Retrieves the Y-coordinate of the highest block at the given X and Z
     * based on the specified {@link HeightMap} criteria.
     *
     * @param x         The absolute X coordinate.
     * @param z         The absolute Z coordinate.
     * @param heightMap The {@link HeightMap} type to use (e.g., WORLD_SURFACE).
     * @return The highest Y coordinate found.
     */
    int getHighestBlockY(int x, int z, HeightMap heightMap);

    /**
     * @return The {@link World} currently being generated.
     */
    @NotNull World getWorld();

    /**
     * @return The {@link Random} source provided for this generation pass.
     */
    @NotNull Random getRandom();
}