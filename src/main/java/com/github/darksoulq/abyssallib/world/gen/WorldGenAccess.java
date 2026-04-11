package com.github.darksoulq.abyssallib.world.gen;

import com.github.darksoulq.abyssallib.common.serialization.SavedEntity;
import com.github.darksoulq.abyssallib.world.block.CustomBlock;
import com.github.darksoulq.abyssallib.world.entity.CustomEntity;
import org.bukkit.HeightMap;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

/**
 * Provides a unified abstraction layer for interacting with the world during
 * generation and population phases.
 *
 * <p>This interface is designed to safely expose block and entity manipulation
 * in contexts where direct Bukkit API usage may be unsafe or restricted
 * (such as async chunk generation or custom world pipelines).</p>
 *
 * <p>Implementations may:
 * <ul>
 *     <li>Buffer writes instead of applying them immediately</li>
 *     <li>Bypass Bukkit thread restrictions</li>
 *     <li>Operate on custom world representations</li>
 * </ul>
 *
 * <p>All coordinates are expected to be in absolute world space unless otherwise stated.</p>
 */
public interface WorldGenAccess {

    /**
     * Sets a block at the given coordinates using a {@link Material}.
     *
     * <p>This replaces any existing block at the target position.</p>
     *
     * @param x        The absolute X coordinate
     * @param y        The absolute Y coordinate
     * @param z        The absolute Z coordinate
     * @param material The material to place (must represent a valid block)
     */
    void setBlock(int x, int y, int z, @NotNull Material material);

    /**
     * Sets a block at the given coordinates using full {@link BlockData}.
     *
     * <p>This allows specifying block states such as orientation, waterlogging, etc.</p>
     *
     * @param x    The absolute X coordinate
     * @param y    The absolute Y coordinate
     * @param z    The absolute Z coordinate
     * @param data The block data to apply
     */
    void setBlock(int x, int y, int z, @NotNull BlockData data);

    /**
     * Places a {@link CustomBlock} at the given coordinates.
     *
     * <p>The implementation is responsible for correctly initializing any associated
     * custom logic, metadata, or block entities.</p>
     *
     * @param x     The absolute X coordinate
     * @param y     The absolute Y coordinate
     * @param z     The absolute Z coordinate
     * @param block The custom block instance to place
     */
    void setBlock(int x, int y, int z, @NotNull CustomBlock block);

    /**
     * Places a {@link CustomBlock} with explicit {@link BlockData}.
     *
     * <p>This allows combining custom logic with a specific visual or state configuration.</p>
     *
     * @param x     The absolute X coordinate
     * @param y     The absolute Y coordinate
     * @param z     The absolute Z coordinate
     * @param block The custom block instance
     * @param data  The block data defining its visual/state configuration
     */
    void setBlock(int x, int y, int z, @NotNull CustomBlock block, @NotNull BlockData data);

    /**
     * Spawns a vanilla {@link Entity} at the given coordinates.
     *
     * <p>Implementations must ensure this operation is safe within the generation
     * context (e.g. deferred execution if required by threading constraints).</p>
     *
     * @param x    The absolute X coordinate (double precision)
     * @param y    The absolute Y coordinate (double precision)
     * @param z    The absolute Z coordinate (double precision)
     * @param type The {@link EntityType} to spawn
     * @return The created entity instance
     */
    @NotNull Entity addEntity(double x, double y, double z, @NotNull EntityType type);

    /**
     * Spawns a {@link CustomEntity} at the given coordinates.
     *
     * <p>The provided entity acts as a template and should be instantiated
     * according to its internal logic.</p>
     *
     * @param x      The absolute X coordinate (double precision)
     * @param y      The absolute Y coordinate (double precision)
     * @param z      The absolute Z coordinate (double precision)
     * @param entity The custom entity template
     */
    void addEntity(double x, double y, double z, @NotNull CustomEntity<?> entity);

    /**
     * Spawns a {@link SavedEntity} at the given coordinates.
     *
     * <p>This typically restores an entity from serialized data (NBT or similar).</p>
     *
     * @param x      The absolute X coordinate (double precision)
     * @param y      The absolute Y coordinate (double precision)
     * @param z      The absolute Z coordinate (double precision)
     * @param entity The saved entity definition
     * @return The spawned entity instance, or {@code null} if spawning failed
     */
    @Nullable Entity addEntity(double x, double y, double z, @NotNull SavedEntity entity);

    /**
     * Retrieves the {@link Material} at the given coordinates.
     *
     * @param x The absolute X coordinate
     * @param y The absolute Y coordinate
     * @param z The absolute Z coordinate
     * @return The material at the specified position
     */
    @NotNull Material getType(int x, int y, int z);

    /**
     * Retrieves the {@link BlockData} at the given coordinates.
     *
     * @param x The absolute X coordinate
     * @param y The absolute Y coordinate
     * @param z The absolute Z coordinate
     * @return The block data at the specified position
     */
    @NotNull BlockData getBlockData(int x, int y, int z);

    /**
     * Retrieves a snapshot {@link BlockState} at the given coordinates.
     *
     * <p>This includes tile entity data where applicable.</p>
     *
     * @param x The absolute X coordinate
     * @param y The absolute Y coordinate
     * @param z The absolute Z coordinate
     * @return The block state snapshot
     */
    @NotNull BlockState getBlockState(int x, int y, int z);

    /**
     * Retrieves the {@link Biome} at the given coordinates.
     *
     * @param x The absolute X coordinate
     * @param y The absolute Y coordinate
     * @param z The absolute Z coordinate
     * @return The biome at the specified position
     */
    @NotNull Biome getBiome(int x, int y, int z);

    /**
     * Retrieves the highest Y coordinate at the given X/Z position based on a {@link HeightMap}.
     *
     * <p>The result depends on the selected heightmap type (e.g. surface, ocean floor).</p>
     *
     * @param x         The absolute X coordinate
     * @param z         The absolute Z coordinate
     * @param heightMap The height map type to query
     * @return The highest Y coordinate matching the height map criteria
     */
    int getHighestBlockY(int x, int z, HeightMap heightMap);

    /**
     * Returns the backing {@link World} instance.
     *
     * <p>Note that direct interaction with the returned world may not always be safe
     * depending on the implementation context.</p>
     *
     * @return The world associated with this generation access
     */
    @NotNull World getWorld();

    /**
     * Returns the {@link Random} instance used for this generation pass.
     *
     * <p>This should be preferred over creating new random instances to ensure
     * deterministic generation when required.</p>
     *
     * @return The random source
     */
    @NotNull Random getRandom();
}