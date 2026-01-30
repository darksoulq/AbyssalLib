package com.github.darksoulq.abyssallib.world.gen.nms;

import com.github.darksoulq.abyssallib.world.block.CustomBlock;
import com.github.darksoulq.abyssallib.world.gen.WorldGenAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.Heightmap;
import org.bukkit.HeightMap;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.generator.CraftLimitedRegion;
import org.bukkit.generator.LimitedRegion;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

/**
 * An NMS-based implementation of {@link WorldGenAccess} for high-performance world generation.
 * <p>
 * This class wraps a Minecraft {@link WorldGenLevel} obtained from a Bukkit {@link LimitedRegion}.
 * It uses a {@link net.minecraft.core.BlockPos.MutableBlockPos} internally to minimize
 * object allocation when performing frequent coordinate-based lookups and placements.
 * </p>
 */
public class NMSWorldGenAccess implements WorldGenAccess {

    /** The underlying NMS level handle. */
    private final WorldGenLevel level;

    /** The Bukkit World instance. */
    private final World world;

    /** The random source for this generation pass. */
    private final Random random;

    /** Reusable mutable position to prevent excessive object creation during iteration. */
    private final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

    /**
     * Constructs a new NMS accessor.
     *
     * @param region The Bukkit {@link LimitedRegion} provided by the populator.
     * @param world  The target Bukkit {@link World}.
     * @param random The {@link Random} source for this pass.
     */
    public NMSWorldGenAccess(LimitedRegion region, World world, Random random) {
        this.world = world;
        this.random = random;
        this.level = ((CraftLimitedRegion) region).getHandle();
    }

    /**
     * Sets a block using a Bukkit Material.
     *
     * @param x        The absolute X coordinate.
     * @param y        The absolute Y coordinate.
     * @param z        The absolute Z coordinate.
     * @param material The {@link Material} to place.
     */
    @Override
    public void setBlock(int x, int y, int z, @NotNull Material material) {
        if (!material.isBlock()) return;
        setBlock(x, y, z, material.createBlockData());
    }

    /**
     * Sets a block directly into the NMS level handle using NMS BlockStates.
     *
     * @param x    The absolute X coordinate.
     * @param y    The absolute Y coordinate.
     * @param z    The absolute Z coordinate.
     * @param data The Bukkit {@link BlockData} to be converted to NMS state.
     */
    @Override
    public void setBlock(int x, int y, int z, @NotNull BlockData data) {
        pos.set(x, y, z);
        net.minecraft.world.level.block.state.BlockState nmsState = ((CraftBlockData) data).getState();
        level.setBlock(pos, nmsState, 2);
    }

    /**
     * Places a {@link CustomBlock} at the specified location.
     *
     * @param x     The absolute X coordinate.
     * @param y     The absolute Y coordinate.
     * @param z     The absolute Z coordinate.
     * @param block The custom block definition.
     */
    @Override
    public void setBlock(int x, int y, int z, @NotNull CustomBlock block) {
        setBlock(x, y, z, block, block.getMaterial().createBlockData());
    }

    /**
     * Places a {@link CustomBlock} and executes its logic on a {@code VirtualBlock}.
     *
     * @param x     The absolute X coordinate.
     * @param y     The absolute Y coordinate.
     * @param z     The absolute Z coordinate.
     * @param block The custom block definition.
     * @param data  The data to apply to the placed block.
     */
    @Override
    public void setBlock(int x, int y, int z, @NotNull CustomBlock block, @NotNull BlockData data) {
        setBlock(x, y, z, data);
        VirtualBlock virtual = new VirtualBlock(this.getWorld(), x, y, z, data.getMaterial());
        block.place(virtual, false);
    }

    /**
     * Retrieves the material at the specified location via NMS.
     *
     * @param x The X coordinate.
     * @param y The Y coordinate.
     * @param z The Z coordinate.
     * @return The {@link Material} at the position.
     */
    @Override
    public @NotNull Material getType(int x, int y, int z) {
        pos.set(x, y, z);
        return ((CraftBlockData) CraftBlockData.fromData(level.getBlockState(pos))).getMaterial();
    }

    /**
     * Retrieves the block data at the specified location via NMS.
     *
     * @param x The X coordinate.
     * @param y The Y coordinate.
     * @param z The Z coordinate.
     * @return The {@link BlockData} wrapper for the NMS state.
     */
    @Override
    public @NotNull BlockData getBlockData(int x, int y, int z) {
        pos.set(x, y, z);
        return CraftBlockData.fromData(level.getBlockState(pos));
    }

    /**
     * Queries the NMS {@link Heightmap} for the highest block coordinate.
     *
     * @param x         The X coordinate.
     * @param z         The Z coordinate.
     * @param heightMap The Bukkit {@link HeightMap} type to map to NMS.
     * @return The Y coordinate of the highest block.
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

    /** @return The Bukkit {@link World} context. */
    @Override
    public @NotNull World getWorld() {
        return world;
    }

    /** @return The deterministic {@link Random} source for the current generation unit. */
    @Override
    public @NotNull Random getRandom() {
        return random;
    }
}