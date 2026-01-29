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

public class NMSWorldGenAccess implements WorldGenAccess {

    private final WorldGenLevel level;
    private final World world;
    private final Random random;
    private final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

    public NMSWorldGenAccess(LimitedRegion region, World world, Random random) {
        this.world = world;
        this.random = random;
        this.level = ((CraftLimitedRegion) region).getHandle();
    }

    @Override
    public void setBlock(int x, int y, int z, @NotNull Material material) {
        if (!material.isBlock()) return;
        setBlock(x, y, z, material.createBlockData());
    }

    @Override
    public void setBlock(int x, int y, int z, @NotNull BlockData data) {
        pos.set(x, y, z);
        net.minecraft.world.level.block.state.BlockState nmsState = ((CraftBlockData) data).getState();
        level.setBlock(pos, nmsState, 2);
    }

    @Override
    public void setBlock(int x, int y, int z, @NotNull CustomBlock block) {
        setBlock(x, y, z, block, block.getMaterial().createBlockData());
    }

    @Override
    public void setBlock(int x, int y, int z, @NotNull CustomBlock block, @NotNull BlockData data) {
        setBlock(x, y, z, data);
        VirtualBlock virtual = new VirtualBlock(this.getWorld(), x, y, z, data.getMaterial());
        block.place(virtual, false);
    }

    @Override
    public @NotNull Material getType(int x, int y, int z) {
        pos.set(x, y, z);
        return ((CraftBlockData) CraftBlockData.fromData(level.getBlockState(pos))).getMaterial();
    }

    @Override
    public @NotNull BlockData getBlockData(int x, int y, int z) {
        pos.set(x, y, z);
        return CraftBlockData.fromData(level.getBlockState(pos));
    }

    @Override
    public int getHighestBlockY(int x, int z, HeightMap heightMap) {
        Heightmap.Types nmsType;
        switch (heightMap) {
            case MOTION_BLOCKING -> nmsType = Heightmap.Types.MOTION_BLOCKING;
            case MOTION_BLOCKING_NO_LEAVES -> nmsType = Heightmap.Types.MOTION_BLOCKING_NO_LEAVES;
            case OCEAN_FLOOR -> nmsType = Heightmap.Types.OCEAN_FLOOR;
            case OCEAN_FLOOR_WG -> nmsType = Heightmap.Types.OCEAN_FLOOR_WG;
            case WORLD_SURFACE -> nmsType = Heightmap.Types.WORLD_SURFACE;
            case WORLD_SURFACE_WG -> nmsType = Heightmap.Types.WORLD_SURFACE_WG;
            default -> nmsType = Heightmap.Types.MOTION_BLOCKING;
        }
        return level.getHeight(nmsType, x, z);
    }

    @Override
    public @NotNull World getWorld() {
        return world;
    }

    @Override
    public @NotNull Random getRandom() {
        return random;
    }
}