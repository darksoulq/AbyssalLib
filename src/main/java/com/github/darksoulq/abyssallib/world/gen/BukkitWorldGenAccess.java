package com.github.darksoulq.abyssallib.world.gen;

import com.github.darksoulq.abyssallib.server.event.custom.entity.CustomEntitySpawnEvent;
import com.github.darksoulq.abyssallib.world.block.CustomBlock;
import com.github.darksoulq.abyssallib.world.entity.CustomEntity;
import org.bukkit.HeightMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

/**
 * Provides standard Bukkit API access for modifying the world during execution outside chunk populators.
 */
public class BukkitWorldGenAccess implements WorldGenAccess {

    /**
     * The Bukkit world to modify.
     */
    private final World world;

    /**
     * The random instance for procedural generation.
     */
    private final Random random;

    /**
     * Constructs a new standard world generation accessor.
     *
     * @param world  The target Bukkit world.
     * @param random The random instance to use.
     */
    public BukkitWorldGenAccess(@NotNull World world, @NotNull Random random) {
        this.world = world;
        this.random = random;
    }

    /**
     * Sets the block at the specified coordinates to the provided material.
     *
     * @param x        The X coordinate.
     * @param y        The Y coordinate.
     * @param z        The Z coordinate.
     * @param material The material to place.
     */
    @Override
    public void setBlock(int x, int y, int z, @NotNull Material material) {
        world.getBlockAt(x, y, z).setType(material, false);
    }

    /**
     * Sets the block at the specified coordinates to the provided block data.
     *
     * @param x    The X coordinate.
     * @param y    The Y coordinate.
     * @param z    The Z coordinate.
     * @param data The block data to place.
     */
    @Override
    public void setBlock(int x, int y, int z, @NotNull BlockData data) {
        world.getBlockAt(x, y, z).setBlockData(data, false);
    }

    /**
     * Places a custom block at the specified coordinates using its default material.
     *
     * @param x     The X coordinate.
     * @param y     The Y coordinate.
     * @param z     The Z coordinate.
     * @param block The custom block to place.
     */
    @Override
    public void setBlock(int x, int y, int z, @NotNull CustomBlock block) {
        setBlock(x, y, z, block, block.getMaterial().createBlockData());
    }

    /**
     * Places a custom block at the specified coordinates with specific block data.
     *
     * @param x     The X coordinate.
     * @param y     The Y coordinate.
     * @param z     The Z coordinate.
     * @param block The custom block to place.
     * @param data  The block data to apply.
     */
    @Override
    public void setBlock(int x, int y, int z, @NotNull CustomBlock block, @NotNull BlockData data) {
        world.getBlockAt(x, y, z).setBlockData(data, false);
        block.place(world.getBlockAt(x, y, z), false);
    }

    /**
     * Spawns an entity of the specified type at the given coordinates.
     *
     * @param x    The X coordinate.
     * @param y    The Y coordinate.
     * @param z    The Z coordinate.
     * @param type The entity type to spawn.
     * @return The spawned entity.
     */
    @Override
    public @NotNull Entity addEntity(double x, double y, double z, @NotNull EntityType type) {
        return world.spawnEntity(new Location(world, x, y, z), type);
    }

    /**
     * Spawns a custom entity at the specified coordinates natively.
     *
     * @param x      The X coordinate.
     * @param y      The Y coordinate.
     * @param z      The Z coordinate.
     * @param entity The custom entity to spawn.
     */
    @Override
    public void addEntity(double x, double y, double z, @NotNull CustomEntity<?> entity) {
        Location spawnLoc = new Location(world, x, y, z);
        entity.spawn(spawnLoc, CustomEntitySpawnEvent.SpawnReason.NATURAL);
    }

    /**
     * Gets the material type of the block at the specified coordinates.
     *
     * @param x The X coordinate.
     * @param y The Y coordinate.
     * @param z The Z coordinate.
     * @return The material type.
     */
    @Override
    public @NotNull Material getType(int x, int y, int z) {
        return world.getBlockAt(x, y, z).getType();
    }

    /**
     * Gets the block data of the block at the specified coordinates.
     *
     * @param x The X coordinate.
     * @param y The Y coordinate.
     * @param z The Z coordinate.
     * @return The block data.
     */
    @Override
    public @NotNull BlockData getBlockData(int x, int y, int z) {
        return world.getBlockAt(x, y, z).getBlockData();
    }

    /**
     * Gets the biome at the specified coordinates.
     *
     * @param x The X coordinate.
     * @param y The Y coordinate.
     * @param z The Z coordinate.
     * @return The biome.
     */
    @Override
    public @NotNull Biome getBiome(int x, int y, int z) {
        return world.getBiome(x, y, z);
    }

    /**
     * Gets the highest block Y coordinate at the specified X and Z coordinates based on a heightmap.
     *
     * @param x         The X coordinate.
     * @param z         The Z coordinate.
     * @param heightMap The heightmap to use.
     * @return The highest Y coordinate.
     */
    @Override
    public int getHighestBlockY(int x, int z, HeightMap heightMap) {
        return world.getHighestBlockYAt(x, z, heightMap);
    }

    /**
     * Gets the world associated with this generation access.
     *
     * @return The world.
     */
    @Override
    public @NotNull World getWorld() {
        return world;
    }

    /**
     * Gets the random instance associated with this generation access.
     *
     * @return The random instance.
     */
    @Override
    public @NotNull Random getRandom() {
        return random;
    }
}