package com.github.darksoulq.abyssallib.world.gen.nms;

import com.destroystokyo.paper.block.BlockSoundGroup;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.bukkit.util.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@SuppressWarnings({"Deprecated", "For removal", "removal"})
public class VirtualBlock implements Block {
    private final World world;
    private final int x, y, z;
    private Material type;

    public VirtualBlock(World world, int x, int y, int z, Material type) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.type = type;
    }

    @Override
    public @NotNull Location getLocation() {
        return new Location(world, x, y, z);
    }

    @Override
    public @NotNull Location getLocation(@Nullable Location loc) {
        if (loc != null) {
            loc.setWorld(world);
            loc.setX(x);
            loc.setY(y);
            loc.setZ(z);
            return loc;
        }
        return getLocation();
    }

    @Override
    public @NotNull Material getType() {
        return type;
    }

    @Override
    public byte getLightLevel() {
        return 0;
    }

    @Override
    public byte getLightFromSky() {
        return 0;
    }

    @Override
    public byte getLightFromBlocks() {
        return 0;
    }

    @Override
    public void setType(@NotNull Material type) {
        this.type = type;
    }

    @Override
    public void setType(@NotNull Material type, boolean applyPhysics) {
        setType(type);
    }

    @Override
    public @NotNull BlockData getBlockData() {
        return type.createBlockData();
    }

    @Override
    public void setBlockData(@NotNull BlockData data) {
        this.type = data.getMaterial();
    }

    @Override
    public void setBlockData(@NotNull BlockData data, boolean applyPhysics) {
        setBlockData(data);
    }

    @Override public byte getData() { return 0; }
    @Override public @NotNull Block getRelative(int modX, int modY, int modZ) { return new VirtualBlock(world, x+modX, y+modY, z+modZ, Material.AIR); }
    @Override public @NotNull Block getRelative(@NotNull BlockFace face) { return getRelative(face.getModX(), face.getModY(), face.getModZ()); }
    @Override public @NotNull Block getRelative(@NotNull BlockFace face, int distance) { return getRelative(face.getModX() * distance, face.getModY() * distance, face.getModZ() * distance); }
    @Override public @NotNull BlockFace getFace(@NotNull Block block) { return BlockFace.SELF; }
    @Override public @NotNull BlockState getState() { throw new UnsupportedOperationException("VirtualBlock does not support states"); }

    @Override
    public @NotNull BlockState getState(boolean useSnapshot) {
        return null;
    }

    @Override public @NotNull Biome getBiome() { return world.getBiome(x, y, z); }

    @Override
    public @NotNull Biome getComputedBiome() {
        return null;
    }

    @Override public void setBiome(@NotNull Biome biome) { }
    @Override public boolean isBlockPowered() { return false; }
    @Override public boolean isBlockIndirectlyPowered() { return false; }
    @Override public boolean isBlockFacePowered(@NotNull BlockFace face) { return false; }
    @Override public boolean isBlockFaceIndirectlyPowered(@NotNull BlockFace face) { return false; }
    @Override public int getBlockPower(@NotNull BlockFace face) { return 0; }
    @Override public int getBlockPower() { return 0; }
    @Override public boolean isEmpty() { return type == Material.AIR; }
    @Override public boolean isLiquid() { return type == Material.WATER || type == Material.LAVA; }

    @Override
    public boolean isBuildable() {
        return false;
    }

    @Override public double getTemperature() { return 0; }
    @Override public double getHumidity() { return 0; }
    @Override public @NotNull PistonMoveReaction getPistonMoveReaction() { return PistonMoveReaction.IGNORE; }
    @Override public boolean breakNaturally() { return false; }
    @Override public boolean breakNaturally(@Nullable ItemStack tool) { return false; }

    @Override
    public boolean breakNaturally(boolean triggerEffect) {
        return Block.super.breakNaturally(triggerEffect);
    }

    @Override
    public boolean breakNaturally(boolean triggerEffect, boolean dropExperience) {
        return false;
    }

    @Override
    public boolean breakNaturally(@NotNull ItemStack tool, boolean triggerEffect) {
        return Block.super.breakNaturally(tool, triggerEffect);
    }

    @Override
    public boolean breakNaturally(@NotNull ItemStack tool, boolean triggerEffect, boolean dropExperience) {
        return false;
    }

    @Override
    public boolean breakNaturally(@NotNull ItemStack tool, boolean triggerEffect, boolean dropExperience, boolean forceEffect) {
        return false;
    }

    @Override public boolean applyBoneMeal(@NotNull BlockFace face) { return false; }
    @Override public @NotNull Collection<ItemStack> getDrops() { return List.of(); }
    @Override public @NotNull Collection<ItemStack> getDrops(@Nullable ItemStack tool) { return List.of(); }
    @Override public @NotNull Collection<ItemStack> getDrops(@Nullable ItemStack tool, @Nullable Entity entity) { return List.of(); }
    @Override public boolean isPassable() { return true; }

    @Override public @Nullable RayTraceResult rayTrace(@NotNull Location start, @NotNull Vector direction, double maxDistance, @NotNull FluidCollisionMode fluidCollisionMode) { return null; }
    @Override public @NotNull BoundingBox getBoundingBox() { return BoundingBox.of(getLocation(), 1, 1, 1); }
    @Override public @NotNull VoxelShape getCollisionShape() {
        return new VoxelShape() {
            @Override
            public @NotNull Collection<BoundingBox> getBoundingBoxes() {
                return Collections.singleton(getBoundingBox());
            }

            @Override
            public boolean overlaps(@NotNull BoundingBox other) {
                return false;
            }
        };
    }
    @Override public boolean canPlace(@NotNull BlockData data) { return true; }

    @Override
    public @NotNull BlockSoundGroup getSoundGroup() {
        assert type.asBlockType() != null;
        SoundGroup group = type.asBlockType().createBlockData().getSoundGroup();
        return new BlockSoundGroup() {
            @Override
            public @NotNull Sound getBreakSound() {
                return group.getBreakSound();
            }

            @Override
            public @NotNull Sound getStepSound() {
                return group.getStepSound();
            }

            @Override
            public @NotNull Sound getPlaceSound() {
                return group.getPlaceSound();
            }

            @Override
            public @NotNull Sound getHitSound() {
                return group.getHitSound();
            }

            @Override
            public @NotNull Sound getFallSound() {
                return group.getFallSound();
            }
        };
    }

    @Override
    public @NotNull SoundGroup getBlockSoundGroup() {
        assert type.asBlockType() != null;
        return type.asBlockType().createBlockData().getSoundGroup();
    }

    @Override public void setMetadata(@NotNull String metadataKey, @NotNull MetadataValue newMetadataValue) { }
    @Override public @NotNull List<MetadataValue> getMetadata(@NotNull String metadataKey) { return List.of(); }
    @Override public boolean hasMetadata(@NotNull String metadataKey) { return false; }
    @Override public void removeMetadata(@NotNull String metadataKey, @NotNull Plugin owningPlugin) { }
    @Override public boolean isPreferredTool(@NotNull ItemStack tool) { return false; }

    @Override public float getBreakSpeed(@NotNull Player player) { return 0; }
    @Override public @NotNull World getWorld() { return world; }
    @Override public int getX() { return x; }
    @Override public int getY() { return y; }
    @Override public int getZ() { return z; }

    @Override
    public long getBlockKey() {
        return Block.super.getBlockKey();
    }

    @Override
    public boolean isValidTool(@NotNull ItemStack itemStack) {
        return false;
    }

    @Override public @NotNull Chunk getChunk() { return world.getChunkAt(this); }
    @Override public boolean isCollidable() { return true; }
    @Override public void tick() { }

    @Override
    public void fluidTick() {

    }

    @Override
    public void randomTick() {

    }

    @Override public boolean isBurnable() { return false; }
    @Override public boolean isReplaceable() { return true; }
    @Override public boolean isSolid() { return type.isSolid(); }
    @Override public @NotNull String getTranslationKey() { return type.getTranslationKey(); }

    @Override
    public float getDestroySpeed(@NotNull ItemStack itemStack) {
        return Block.super.getDestroySpeed(itemStack);
    }

    @Override
    public float getDestroySpeed(@NotNull ItemStack itemStack, boolean considerEnchants) {
        return Block.super.getDestroySpeed(itemStack, considerEnchants);
    }

    @Override
    public boolean isSuffocating() {
        return false;
    }

    @Override
    public @NotNull String translationKey() {
        return "";
    }
}