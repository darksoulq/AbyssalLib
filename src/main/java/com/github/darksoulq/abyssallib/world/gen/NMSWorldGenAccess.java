package com.github.darksoulq.abyssallib.world.gen;

import com.github.darksoulq.abyssallib.world.block.CustomBlock;
import com.github.darksoulq.abyssallib.world.entity.CustomEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.Heightmap;
import org.bukkit.HeightMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.block.CraftBiome;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.generator.CraftLimitedRegion;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.generator.LimitedRegion;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

/**
 * Provides highly optimized world generation access utilizing internal Native Minecraft Server (NMS) methods.
 */
public class NMSWorldGenAccess implements WorldGenAccess {

    /**
     * The internal NMS world generation level.
     */
    private final WorldGenLevel level;

    /**
     * The thread-safe Bukkit region bounded to the generating chunk.
     */
    private final LimitedRegion region;

    /**
     * The Bukkit world being generated.
     */
    private final World world;

    /**
     * The random instance tied to the generation pass.
     */
    private final Random random;

    /**
     * A reusable mutable block position to minimize memory allocation.
     */
    private final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

    /**
     * Constructs a new NMS world generation accessor.
     *
     * @param region The bounded region for the chunk populator.
     * @param world  The target Bukkit world.
     * @param random The random instance for this pass.
     */
    public NMSWorldGenAccess(LimitedRegion region, World world, Random random) {
        this.region = region;
        this.world = world;
        this.random = random;
        this.level = ((CraftLimitedRegion) region).getHandle();
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
        if (!material.isBlock()) return;
        setBlock(x, y, z, material.createBlockData());
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
        pos.set(x, y, z);
        net.minecraft.world.level.block.state.BlockState nmsState = ((CraftBlockData) data).getState();
        level.setBlock(pos, nmsState, 2);
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
        setBlock(x, y, z, data);
        VirtualBlock virtual = new VirtualBlock(this.getWorld(), x, y, z, data.getMaterial());
        block.place(virtual, false);
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
        return region.spawnEntity(new Location(world, x, y, z), type);
    }

    /**
     * Spawns a custom entity at the specified coordinates.
     *
     * @param x      The X coordinate.
     * @param y      The Y coordinate.
     * @param z      The Z coordinate.
     * @param entity The custom entity to spawn.
     */
    @Override
    public void addEntity(double x, double y, double z, @NotNull CustomEntity<?> entity) {
        Location spawnLoc = new Location(world, x, y, z);
        Entity nativeEntity = region.spawnEntity(spawnLoc, entity.getBaseType());
        entity.spawnFromInstance(nativeEntity);
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
        pos.set(x, y, z);
        return CraftBlockData.fromData(level.getBlockState(pos)).getMaterial();
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
        pos.set(x, y, z);
        return CraftBlockData.fromData(level.getBlockState(pos));
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
        pos.set(x, y, z);
        Holder<net.minecraft.world.level.biome.Biome> nmsBiome = level.getNoiseBiome(x >> 2, y >> 2, z >> 2);
        return CraftBiome.minecraftHolderToBukkit(nmsBiome);
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