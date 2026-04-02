package com.github.darksoulq.abyssallib.world.gen;

import com.github.darksoulq.abyssallib.world.block.CustomBlock;
import com.github.darksoulq.abyssallib.world.entity.CustomEntity;
import org.bukkit.HeightMap;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

/**
 * A unified interface providing access to world modification and interrogation
 * during the generation and population phases.
 */
public interface WorldGenAccess {

    /**
     * Sets the block at the specified coordinates to a standard Material.
     *
     * @param x        The absolute X coordinate.
     * @param y        The absolute Y coordinate.
     * @param z        The absolute Z coordinate.
     * @param material The material to place.
     */
    void setBlock(int x, int y, int z, @NotNull Material material);

    /**
     * Sets the block at the specified coordinates using specific BlockData.
     *
     * @param x    The absolute X coordinate.
     * @param y    The absolute Y coordinate.
     * @param z    The absolute Z coordinate.
     * @param data The block data containing state information.
     */
    void setBlock(int x, int y, int z, @NotNull BlockData data);

    /**
     * Sets the block at the specified coordinates to a CustomBlock.
     *
     * @param x     The absolute X coordinate.
     * @param y     The absolute Y coordinate.
     * @param z     The absolute Z coordinate.
     * @param block The custom block instance to place.
     */
    void setBlock(int x, int y, int z, @NotNull CustomBlock block);

    /**
     * Sets a CustomBlock with specific BlockData at the specified coordinates.
     *
     * @param x     The absolute X coordinate.
     * @param y     The absolute Y coordinate.
     * @param z     The absolute Z coordinate.
     * @param block The custom block instance.
     * @param data  The block data state to apply.
     */
    void setBlock(int x, int y, int z, @NotNull CustomBlock block, @NotNull BlockData data);

    /**
     * Safely dispatches standard vanilla entity spawning operations respecting the rigid
     * thread-locks imposed during procedural chunk population.
     *
     * @param x    The absolute decimal X coordinate.
     * @param y    The absolute decimal Y coordinate.
     * @param z    The absolute decimal Z coordinate.
     * @param type The categorical entity type target.
     * @return The initialized entity reference.
     */
    @NotNull Entity addEntity(double x, double y, double z, @NotNull EntityType type);

    /**
     * Safely deploys a customized entity instance directly into the procedural generation environment,
     * ensuring internal hooks and physics calculations bypass thread-lock boundaries.
     *
     * @param x      The absolute decimal X coordinate.
     * @param y      The absolute decimal Y coordinate.
     * @param z      The absolute decimal Z coordinate.
     * @param entity The custom entity template to instantiate.
     */
    void addEntity(double x, double y, double z, @NotNull CustomEntity<?> entity);

    /**
     * Retrieves the Material type at the specified coordinates.
     *
     * @param x The absolute X coordinate.
     * @param y The absolute Y coordinate.
     * @param z The absolute Z coordinate.
     * @return The material at the position.
     */
    @NotNull Material getType(int x, int y, int z);

    /**
     * Retrieves the full BlockData at the specified coordinates.
     *
     * @param x The absolute X coordinate.
     * @param y The absolute Y coordinate.
     * @param z The absolute Z coordinate.
     * @return The block data at the position.
     */
    @NotNull BlockData getBlockData(int x, int y, int z);

    /**
     * Retrieves the Biome at the specified coordinates.
     *
     * @param x The absolute X coordinate.
     * @param y The absolute Y coordinate.
     * @param z The absolute Z coordinate.
     * @return The biome at the position.
     */
    @NotNull Biome getBiome(int x, int y, int z);

    /**
     * Retrieves the Y-coordinate of the highest block based on height map criteria.
     *
     * @param x         The absolute X coordinate.
     * @param z         The absolute Z coordinate.
     * @param heightMap The height map type to use.
     * @return The highest Y coordinate found.
     */
    int getHighestBlockY(int x, int z, HeightMap heightMap);

    /**
     * Retrieves the world currently being generated.
     *
     * @return The Bukkit world context.
     */
    @NotNull World getWorld();

    /**
     * Retrieves the random source for the generation pass.
     *
     * @return The random source.
     */
    @NotNull Random getRandom();
}