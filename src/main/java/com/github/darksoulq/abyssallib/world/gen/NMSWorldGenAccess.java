package com.github.darksoulq.abyssallib.world.gen;

import com.github.darksoulq.abyssallib.world.block.CustomBlock;
import com.github.darksoulq.abyssallib.world.entity.CustomEntity;
import com.github.darksoulq.abyssallib.world.entity.SavedEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.Heightmap;
import org.bukkit.HeightMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.block.CraftBiome;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.generator.CraftLimitedRegion;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.generator.LimitedRegion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

/**
 * Implementation of {@link WorldGenAccess} backed by NMS (Native Minecraft Server).
 *
 * <p>This class provides high-performance world access during chunk generation by
 * interacting directly with {@link WorldGenLevel}, while maintaining thread safety
 * through the use of {@link LimitedRegion}.</p>
 *
 * <p>It is designed specifically for use in terrain generation, structure placement,
 * and other population phases where standard Bukkit API access is restricted.</p>
 */
public class NMSWorldGenAccess implements WorldGenAccess {

    /** Underlying NMS world generation level. */
    private final WorldGenLevel level;

    /** Thread-safe region representing the currently generating chunk. */
    private final LimitedRegion region;

    /** Bukkit world reference. */
    private final World world;

    /** Random instance tied to this generation pass. */
    private final Random random;

    /** Reusable mutable position to reduce object allocation. */
    private final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

    /**
     * Creates a new NMS-backed world generation access instance.
     *
     * @param region The limited region for safe block/entity access
     * @param world  The Bukkit world being generated
     * @param random The random instance for this generation pass
     */
    public NMSWorldGenAccess(LimitedRegion region, World world, Random random) {
        this.region = region;
        this.world = world;
        this.random = random;
        this.level = ((CraftLimitedRegion) region).getHandle();
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
        if (!material.isBlock()) return;
        setBlock(x, y, z, material.createBlockData());
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
        pos.set(x, y, z);
        net.minecraft.world.level.block.state.BlockState nmsState = ((CraftBlockData) data).getState();
        level.setBlock(pos, nmsState, 2);
    }

    /**
     * Places a {@link CustomBlock} using its default block data.
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
     * <p>The base block is placed first, then custom logic is applied via a virtual wrapper.</p>
     *
     * @param x     X coordinate
     * @param y     Y coordinate
     * @param z     Z coordinate
     * @param block Custom block instance
     * @param data  Block data to apply
     */
    @Override
    public void setBlock(int x, int y, int z, @NotNull CustomBlock block, @NotNull BlockData data) {
        setBlock(x, y, z, data);
        VirtualBlock virtual = new VirtualBlock(this.getWorld(), x, y, z, data.getMaterial());
        block.place(virtual, false);
    }

    /**
     * Spawns a vanilla entity safely within the generation region.
     *
     * @param x    X coordinate
     * @param y    Y coordinate
     * @param z    Z coordinate
     * @param type Entity type
     * @return Spawned entity
     */
    @Override
    public @NotNull Entity addEntity(double x, double y, double z, @NotNull EntityType type) {
        return region.spawnEntity(new Location(world, x, y, z), type);
    }

    /**
     * Spawns a {@link CustomEntity} at the given coordinates.
     *
     * @param x      X coordinate
     * @param y      Y coordinate
     * @param z      Z coordinate
     * @param entity Custom entity instance
     */
    @Override
    public void addEntity(double x, double y, double z, @NotNull CustomEntity<?> entity) {
        Location spawnLoc = new Location(world, x, y, z);
        Entity nativeEntity = region.spawnEntity(spawnLoc, entity.getBaseType());
        entity.spawnFromInstance(nativeEntity);
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
     * @return Material at location
     */
    @Override
    public @NotNull Material getType(int x, int y, int z) {
        pos.set(x, y, z);
        return CraftBlockData.fromData(level.getBlockState(pos)).getMaterial();
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
        pos.set(x, y, z);
        return CraftBlockData.fromData(level.getBlockState(pos));
    }

    /**
     * Retrieves the full {@link BlockState} using the safe {@link LimitedRegion}.
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @return Block state snapshot
     */
    @Override
    public @NotNull BlockState getBlockState(int x, int y, int z) {
        return region.getBlockState(x, y, z);
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
        pos.set(x, y, z);
        Holder<net.minecraft.world.level.biome.Biome> nmsBiome = level.getNoiseBiome(x >> 2, y >> 2, z >> 2);
        return CraftBiome.minecraftHolderToBukkit(nmsBiome);
    }

    /**
     * Gets the highest Y value at a position using a {@link HeightMap}.
     *
     * @param x         X coordinate
     * @param z         Z coordinate
     * @param heightMap Heightmap type
     * @return Highest Y coordinate
     */
    @Override
    public int getHighestBlockY(int x, int z, HeightMap heightMap) {
        Heightmap.Types nmsType;
        switch (heightMap) {
            case MOTION_BLOCKING_NO_LEAVES -> nmsType = Heightmap.Types.MOTION_BLOCKING_NO_LEAVES;
            case OCEAN_FLOOR -> nmsType = Heightmap.Types.OCEAN_FLOOR;
            case OCEAN_FLOOR_WG -> nmsType = Heightmap.Types.OCEAN_FLOOR_WG;
            case WORLD_SURFACE -> nmsType = Heightmap.Types.WORLD_SURFACE;
            case WORLD_SURFACE_WG -> nmsType = Heightmap.Types.WORLD_SURFACE_WG;
            default -> nmsType = Heightmap.Types.MOTION_BLOCKING;
        }
        return level.getHeight(nmsType, x, z);
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