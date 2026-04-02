package com.github.darksoulq.abyssallib.world.gen;

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

/**
 * A headless block implementation designed for firing block interaction
 * events or applying logic without directly instantiating a full world state.
 */
@SuppressWarnings({"Deprecated", "For removal", "removal"})
public class VirtualBlock implements Block {

    /** The world context. */
    private final World world;

    /** The X coordinate. */
    private final int x;

    /** The Y coordinate. */
    private final int y;

    /** The Z coordinate. */
    private final int z;

    /** The represented material type. */
    private Material type;

    /**
     * Constructs a new virtual block.
     *
     * @param world The world context.
     * @param x     The X coordinate.
     * @param y     The Y coordinate.
     * @param z     The Z coordinate.
     * @param type  The material type representing this block.
     */
    public VirtualBlock(World world, int x, int y, int z, Material type) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.type = type;
    }

    /**
     * Gets the location of this virtual block.
     *
     * @return The location.
     */
    @Override
    public @NotNull Location getLocation() {
        return new Location(world, x, y, z);
    }

    /**
     * Stores the location of this block into the provided object.
     *
     * @param loc The location to overwrite.
     * @return The modified location object.
     */
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

    /**
     * Retrieves the material type of this virtual block.
     *
     * @return The material.
     */
    @Override
    public @NotNull Material getType() {
        return type;
    }

    /**
     * Sets the material type of this virtual block.
     *
     * @param type The new material.
     */
    @Override
    public void setType(@NotNull Material type) {
        this.type = type;
    }

    /**
     * Sets the material type and ignores physics application.
     *
     * @param type         The new material.
     * @param applyPhysics Ignored.
     */
    @Override
    public void setType(@NotNull Material type, boolean applyPhysics) {
        setType(type);
    }

    /**
     * Retrieves the block data for this virtual block based on its material.
     *
     * @return The generated block data.
     */
    @Override
    public @NotNull BlockData getBlockData() {
        return type.createBlockData();
    }

    /**
     * Sets the material of this virtual block based on the provided block data.
     *
     * @param data The block data to apply.
     */
    @Override
    public void setBlockData(@NotNull BlockData data) {
        this.type = data.getMaterial();
    }

    /**
     * Sets the block data and ignores physics.
     *
     * @param data         The block data to apply.
     * @param applyPhysics Ignored.
     */
    @Override
    public void setBlockData(@NotNull BlockData data, boolean applyPhysics) {
        setBlockData(data);
    }

    /**
     * Gets a virtual relative block relative to this block's coordinates.
     *
     * @param modX The X modifier.
     * @param modY The Y modifier.
     * @param modZ The Z modifier.
     * @return A new empty virtual block at the relative coordinates.
     */
    @Override
    public @NotNull Block getRelative(int modX, int modY, int modZ) {
        return new VirtualBlock(world, x + modX, y + modY, z + modZ, Material.AIR);
    }

    /**
     * Gets a virtual relative block at the specified face.
     *
     * @param face The block face to check.
     * @return The relative virtual block.
     */
    @Override
    public @NotNull Block getRelative(@NotNull BlockFace face) {
        return getRelative(face.getModX(), face.getModY(), face.getModZ());
    }

    /**
     * Gets a virtual relative block at the specified face and distance.
     *
     * @param face     The block face direction.
     * @param distance The distance to multiply the offset by.
     * @return The relative virtual block.
     */
    @Override
    public @NotNull Block getRelative(@NotNull BlockFace face, int distance) {
        return getRelative(face.getModX() * distance, face.getModY() * distance, face.getModZ() * distance);
    }

    /**
     * Retrieves the bounding box for this virtual block.
     *
     * @return A 1x1x1 bounding box at this block's location.
     */
    @Override
    public @NotNull BoundingBox getBoundingBox() {
        return BoundingBox.of(getLocation(), 1, 1, 1);
    }

    /**
     * Retrieves the collision shape of this virtual block.
     *
     * @return A voxel shape encompassing the standard bounding box.
     */
    @Override
    public @NotNull VoxelShape getCollisionShape() {
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

    /**
     * Retrieves the sound group associated with this block's material.
     *
     * @return The block sound group.
     */
    @Override
    public @NotNull BlockSoundGroup getSoundGroup() {
        assert type.asBlockType() != null;
        SoundGroup group = type.asBlockType().createBlockData().getSoundGroup();
        return new BlockSoundGroup() {
            @Override public @NotNull Sound getBreakSound() { return group.getBreakSound(); }
            @Override public @NotNull Sound getStepSound() { return group.getStepSound(); }
            @Override public @NotNull Sound getPlaceSound() { return group.getPlaceSound(); }
            @Override public @NotNull Sound getHitSound() { return group.getHitSound(); }
            @Override public @NotNull Sound getFallSound() { return group.getFallSound(); }
        };
    }

    /**
     * Retrieves the core sound group associated with this block.
     *
     * @return The sound group.
     */
    @Override
    public @NotNull SoundGroup getBlockSoundGroup() {
        assert type.asBlockType() != null;
        return type.asBlockType().createBlockData().getSoundGroup();
    }

    @Override public byte getLightLevel() { return 0; }
    @Override public byte getLightFromSky() { return 0; }
    @Override public byte getLightFromBlocks() { return 0; }
    @Override public byte getData() { return 0; }
    @Override public @NotNull BlockFace getFace(@NotNull Block block) { return BlockFace.SELF; }
    @Override public @NotNull BlockState getState() { throw new UnsupportedOperationException("VirtualBlock does not support states"); }
    @Override public @NotNull BlockState getState(boolean useSnapshot) { return null; }
    @Override public @NotNull Biome getBiome() { return world.getBiome(x, y, z); }
    @Override public @NotNull Biome getComputedBiome() { return world.getBiome(x, y, z); }
    @Override public void setBiome(@NotNull Biome biome) { }
    @Override public boolean isBlockPowered() { return false; }
    @Override public boolean isBlockIndirectlyPowered() { return false; }
    @Override public boolean isBlockFacePowered(@NotNull BlockFace face) { return false; }
    @Override public boolean isBlockFaceIndirectlyPowered(@NotNull BlockFace face) { return false; }
    @Override public int getBlockPower(@NotNull BlockFace face) { return 0; }
    @Override public int getBlockPower() { return 0; }
    @Override public boolean isEmpty() { return type == Material.AIR; }
    @Override public boolean isLiquid() { return type == Material.WATER || type == Material.LAVA; }
    @Override public boolean isBuildable() { return false; }
    @Override public double getTemperature() { return 0; }
    @Override public double getHumidity() { return 0; }
    @Override public @NotNull PistonMoveReaction getPistonMoveReaction() { return PistonMoveReaction.IGNORE; }
    @Override public boolean breakNaturally() { return false; }
    @Override public boolean breakNaturally(@Nullable ItemStack tool) { return false; }
    @Override public boolean breakNaturally(boolean triggerEffect) { return false; }
    @Override public boolean breakNaturally(boolean triggerEffect, boolean dropExperience) { return false; }
    @Override public boolean breakNaturally(@NotNull ItemStack tool, boolean triggerEffect) { return false; }
    @Override public boolean breakNaturally(@NotNull ItemStack tool, boolean triggerEffect, boolean dropExperience) { return false; }
    @Override public boolean breakNaturally(@NotNull ItemStack tool, boolean triggerEffect, boolean dropExperience, boolean forceEffect) { return false; }
    @Override public boolean applyBoneMeal(@NotNull BlockFace face) { return false; }
    @Override public @NotNull Collection<ItemStack> getDrops() { return List.of(); }
    @Override public @NotNull Collection<ItemStack> getDrops(@Nullable ItemStack tool) { return List.of(); }
    @Override public @NotNull Collection<ItemStack> getDrops(@Nullable ItemStack tool, @Nullable Entity entity) { return List.of(); }
    @Override public boolean isPassable() { return true; }
    @Override public @Nullable RayTraceResult rayTrace(@NotNull Location start, @NotNull Vector direction, double maxDistance, @NotNull FluidCollisionMode fluidCollisionMode) { return null; }
    @Override public boolean canPlace(@NotNull BlockData data) { return true; }
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
    @Override public long getBlockKey() { return Block.super.getBlockKey(); }
    @Override public boolean isValidTool(@NotNull ItemStack itemStack) { return false; }
    @Override public @NotNull Chunk getChunk() { return world.getChunkAt(this); }
    @Override public boolean isCollidable() { return true; }
    @Override public void tick() { }
    @Override public void fluidTick() { }
    @Override public void randomTick() { }
    @Override public boolean isBurnable() { return false; }
    @Override public boolean isReplaceable() { return true; }
    @Override public boolean isSolid() { return type.isSolid(); }
    @Override public @NotNull String getTranslationKey() { return type.getTranslationKey(); }
    @Override public float getDestroySpeed(@NotNull ItemStack itemStack) { return 0; }
    @Override public float getDestroySpeed(@NotNull ItemStack itemStack, boolean considerEnchants) { return 0; }
    @Override public boolean isSuffocating() { return false; }
    @Override public @NotNull String translationKey() { return ""; }
}