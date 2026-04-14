package com.github.darksoulq.abyssallib.world.gen.internal;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.darksoulq.abyssallib.common.serialization.AbyssalLibBlockSerializer;
import com.github.darksoulq.abyssallib.common.serialization.BlockInfo;
import com.github.darksoulq.abyssallib.common.serialization.MinecraftBlockSerializer;
import com.github.darksoulq.abyssallib.common.serialization.ops.JsonOps;
import com.github.darksoulq.abyssallib.world.block.CustomBlock;
import com.github.darksoulq.abyssallib.world.gen.WorldGenAccess;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Provides utility methods for procedural world generation, including validation
 * and placement of both vanilla and custom blocks.
 * This class bridges high-level structure generation logic with low-level block
 * placement APIs, supporting state, property, and NBT deserialization alongside
 * rotational transforms.
 */
public class WorldGenUtils {

    /**
     * Determines whether a block at a given location is valid for replacement
     * based on a list of allowed {@link BlockInfo} targets.
     * If the target list is null or empty, this method assumes no restrictions exist.
     *
     * @param level
     * The {@link WorldGenAccess} context for reading chunk data. Can be null for live world access.
     * @param loc
     * The target {@link Location} to validate.
     * @param validTargets
     * A list of allowed block definitions.
     * @return
     * True if the block matches any valid target or if no restrictions exist.
     */
    public static boolean isValidBlock(WorldGenAccess level, Location loc, List<BlockInfo> validTargets) {
        if (validTargets == null || validTargets.isEmpty()) {
            return true;
        }

        for (BlockInfo target : validTargets) {
            if (matchesTarget(level, loc, target)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Internal logic to verify if a location currently contains a specific block definition.
     * This method evaluates both base block types (vanilla/custom) and optional states.
     *
     * @param level
     * The {@link WorldGenAccess} instance.
     * @param loc
     * The target {@link Location}.
     * @param target
     * The {@link BlockInfo} definition to compare against.
     * @return
     * True if the location strictly matches the target properties.
     */
    private static boolean matchesTarget(WorldGenAccess level, Location loc, BlockInfo target) {
        Material mat = level != null
            ? level.getType(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())
            : loc.getBlock().getType();

        String vanillaId = "minecraft:" + mat.name().toLowerCase();

        CustomBlock cb = CustomBlock.resolve(loc);
        String currentId = cb != null ? cb.getId().toString() : vanillaId;

        if (!target.getAsString().equals(currentId)) {
            return false;
        }

        if (target.states() != null) {
            BlockData currentData = level != null
                ? level.getBlockData(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())
                : loc.getBlock().getBlockData();

            if (target.block() instanceof BlockData targetData) {
                return currentData.matches(targetData);
            }
        }

        return true;
    }

    /**
     * Pre-calculates the Bukkit BlockData for a specific block definition, applying
     * deserialized JSON states, mirroring, and rotational transforms.
     *
     * @param info
     * The {@link BlockInfo} detailing the block type and JSON states.
     * @param rotation
     * The {@link StructureRotation} to apply to directional properties.
     * @param mirror
     * The {@link Mirror} transform to apply.
     * @return
     * A finalized {@link BlockData} instance ready for placement.
     */
    public static BlockData bakeData(BlockInfo info, StructureRotation rotation, Mirror mirror) {
        BlockData bd = null;
        Object blockObj = info.block();
        if (blockObj instanceof CustomBlock cb) {
            bd = cb.getMaterial().createBlockData();
        } else if (blockObj instanceof BlockData data) {
            bd = data.clone();
        } else if (blockObj instanceof Material mat && mat.isBlock()) {
            bd = mat.createBlockData();
        }

        if (bd != null) {
            if (info.states() != null) {
                Map<JsonNode, JsonNode> mapData = JsonOps.INSTANCE.getMap(info.states()).orElse(Collections.emptyMap());
                if (blockObj instanceof CustomBlock) {
                    AbyssalLibBlockSerializer.deserializeBlockData(mapData, JsonOps.INSTANCE, bd);
                } else {
                    MinecraftBlockSerializer.deserialize(bd, mapData, JsonOps.INSTANCE);
                }
            }
            if (mirror != Mirror.NONE) {
                bd.mirror(mirror);
            }
            if (rotation != StructureRotation.NONE) {
                bd.rotate(rotation);
            }
        }
        return bd;
    }

    /**
     * Convenience method to place a block without applying any rotation or mirroring.
     *
     * @param level
     * The generation context.
     * @param location
     * The target coordinates.
     * @param info
     * The block definition.
     */
    public static void placeBlock(WorldGenAccess level, Location location, BlockInfo info) {
        BlockData bakedData = bakeData(info, StructureRotation.NONE, Mirror.NONE);
        CustomBlock bakedCustom = info.block() instanceof CustomBlock c ? c.clone() : null;
        placeBlock(level, location, info, bakedData, bakedCustom);
    }

    /**
     * Places a fully configured block at the specified location within the world.
     * This method handles the assignment of pre-baked BlockData, instantiation of
     * custom block logic, restoration of NBT/TileEntity data, and waterlogging physics.
     *
     * @param level
     * The {@link WorldGenAccess} context for fast/async placement. Null for live Bukkit edits.
     * @param location
     * The absolute {@link Location} where the block should be placed.
     * @param info
     * The {@link BlockInfo} containing the original serialized state and NBT.
     * @param bakedData
     * The pre-processed {@link BlockData} containing rotational adjustments.
     * @param bakedCustom
     * The pre-processed {@link CustomBlock} instance, or null if vanilla.
     */
    public static void placeBlock(WorldGenAccess level, Location location, BlockInfo info, BlockData bakedData, CustomBlock bakedCustom) {
        if (location.getBlockY() < location.getWorld().getMinHeight() || location.getBlockY() >= location.getWorld().getMaxHeight()) {
            return;
        }

        try {
            if (bakedCustom != null) {
                if (level != null) {
                    if (level.getType(location.getBlockX(), location.getBlockY(), location.getBlockZ()) == Material.WATER && bakedData instanceof Waterlogged wl) {
                        wl.setWaterlogged(true);
                    }
                    level.setBlock(location.getBlockX(), location.getBlockY(), location.getBlockZ(), bakedCustom, bakedData);
                } else {
                    if (location.getBlock().getType() == Material.WATER && bakedData instanceof Waterlogged wl) {
                        wl.setWaterlogged(true);
                    }
                    location.getBlock().setBlockData(bakedData, false);
                    bakedCustom.place(location.getBlock(), false);
                }
                if (info.properties() != null) {
                    AbyssalLibBlockSerializer.deserializeEntity(bakedCustom, info.properties(), JsonOps.INSTANCE);
                }
            } else if (bakedData != null) {
                if (level != null) {
                    if (level.getType(location.getBlockX(), location.getBlockY(), location.getBlockZ()) == Material.WATER && bakedData instanceof Waterlogged wl) {
                        wl.setWaterlogged(true);
                    }
                    level.setBlock(location.getBlockX(), location.getBlockY(), location.getBlockZ(), bakedData);
                } else {
                    if (location.getBlock().getType() == Material.WATER && bakedData instanceof Waterlogged wl) {
                        wl.setWaterlogged(true);
                    }
                    location.getBlock().setBlockData(bakedData, false);
                }
            }

            if (info.nbt() != null) {
                BlockState state = level != null
                    ? level.getBlockState(location.getBlockX(), location.getBlockY(), location.getBlockZ())
                    : location.getBlock().getState();

                Map<JsonNode, JsonNode> nbtMap = JsonOps.INSTANCE.getMap(info.nbt()).orElse(null);
                if (nbtMap != null) {
                    MinecraftBlockSerializer.deserializeTile(state, nbtMap, JsonOps.INSTANCE);
                    state.update(true, false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}