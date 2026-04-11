package com.github.darksoulq.abyssallib.world.gen;

import com.github.darksoulq.abyssallib.common.serialization.SavedEntity;
import com.github.darksoulq.abyssallib.server.event.custom.entity.CustomEntitySpawnEvent;
import com.github.darksoulq.abyssallib.world.block.CustomBlock;
import com.github.darksoulq.abyssallib.world.entity.CustomEntity;
import org.bukkit.HeightMap;
import org.bukkit.Location;
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
 * Standard {@link WorldGenAccess} implementation using the Bukkit API.
 *
 * <p>This implementation is intended for use outside of chunk generation,
 * where full Bukkit world access is safe and unrestricted.</p>
 *
 * <p>Unlike {@link NMSWorldGenAccess}, this class does not rely on internal
 * server mechanics and instead operates directly on live world data.</p>
 */
public class BukkitWorldGenAccess implements WorldGenAccess {

    /** Target Bukkit world. */
    private final World world;

    /** Random instance for generation logic. */
    private final Random random;

    /**
     * Creates a new Bukkit-based world access wrapper.
     *
     * @param world  Target world
     * @param random Random instance for generation
     */
    public BukkitWorldGenAccess(@NotNull World world, @NotNull Random random) {
        this.world = world;
        this.random = random;
    }

    /**
     * Sets a block using a {@link Material}.
     *
     * @param x        X coordinate
     * @param y        Y coordinate
     * @param z        Z coordinate
     * @param material Material to place
     */
    @Override
    public void setBlock(int x, int y, int z, @NotNull Material material) {
        world.getBlockAt(x, y, z).setType(material, false);
    }

    /**
     * Sets a block using {@link BlockData}.
     *
     * @param x    X coordinate
     * @param y    Y coordinate
     * @param z    Z coordinate
     * @param data Block data to apply
     */
    @Override
    public void setBlock(int x, int y, int z, @NotNull BlockData data) {
        world.getBlockAt(x, y, z).setBlockData(data, false);
    }

    /**
     * Places a {@link CustomBlock} using its default state.
     *
     * @param x     X coordinate
     * @param y     Y coordinate
     * @param z     Z coordinate
     * @param block Custom block instance
     */
    @Override
    public void setBlock(int x, int y, int z, @NotNull CustomBlock block) {
        setBlock(x, y, z, block, block.getMaterial().createBlockData());
    }

    /**
     * Places a {@link CustomBlock} with explicit {@link BlockData}.
     *
     * @param x     X coordinate
     * @param y     Y coordinate
     * @param z     Z coordinate
     * @param block Custom block instance
     * @param data  Block data to apply
     */
    @Override
    public void setBlock(int x, int y, int z, @NotNull CustomBlock block, @NotNull BlockData data) {
        world.getBlockAt(x, y, z).setBlockData(data, false);
        block.place(world.getBlockAt(x, y, z), false);
    }

    /**
     * Spawns a vanilla entity.
     *
     * @param x    X coordinate
     * @param y    Y coordinate
     * @param z    Z coordinate
     * @param type Entity type
     * @return Spawned entity
     */
    @Override
    public @NotNull Entity addEntity(double x, double y, double z, @NotNull EntityType type) {
        return world.spawnEntity(new Location(world, x, y, z), type);
    }

    /**
     * Spawns a {@link CustomEntity}.
     *
     * @param x      X coordinate
     * @param y      Y coordinate
     * @param z      Z coordinate
     * @param entity Custom entity instance
     */
    @Override
    public void addEntity(double x, double y, double z, @NotNull CustomEntity<?> entity) {
        Location spawnLoc = new Location(world, x, y, z);
        entity.spawn(spawnLoc, CustomEntitySpawnEvent.SpawnReason.NATURAL);
    }

    /**
     * Spawns a {@link SavedEntity}.
     *
     * @param x      X coordinate
     * @param y      Y coordinate
     * @param z      Z coordinate
     * @param entity Saved entity definition
     * @return Spawned entity or {@code null} if failed
     */
    @Override
    public @Nullable Entity addEntity(double x, double y, double z, @NotNull SavedEntity entity) {
        return entity.spawn(this, new Location(world, x, y, z));
    }

    /**
     * Gets the material at a position.
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @return Material
     */
    @Override
    public @NotNull Material getType(int x, int y, int z) {
        return world.getBlockAt(x, y, z).getType();
    }

    /**
     * Gets the {@link BlockData} at a position.
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @return Block data
     */
    @Override
    public @NotNull BlockData getBlockData(int x, int y, int z) {
        return world.getBlockAt(x, y, z).getBlockData();
    }

    /**
     * Gets the full {@link BlockState}.
     *
     * <p>Includes tile entity data such as inventories, names, etc.</p>
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @return Block state snapshot
     */
    @Override
    public @NotNull BlockState getBlockState(int x, int y, int z) {
        return world.getBlockAt(x, y, z).getState();
    }

    /**
     * Gets the biome at a position.
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @return Biome
     */
    @Override
    public @NotNull Biome getBiome(int x, int y, int z) {
        return world.getBiome(x, y, z);
    }

    /**
     * Gets the highest Y coordinate using a {@link HeightMap}.
     *
     * @param x         X coordinate
     * @param z         Z coordinate
     * @param heightMap Heightmap type
     * @return Highest Y value
     */
    @Override
    public int getHighestBlockY(int x, int z, HeightMap heightMap) {
        return world.getHighestBlockYAt(x, z, heightMap);
    }

    /**
     * Gets the Bukkit world.
     *
     * @return World instance
     */
    @Override
    public @NotNull World getWorld() {
        return world;
    }

    /**
     * Gets the random instance used for generation.
     *
     * @return Random instance
     */
    @Override
    public @NotNull Random getRandom() {
        return random;
    }
}