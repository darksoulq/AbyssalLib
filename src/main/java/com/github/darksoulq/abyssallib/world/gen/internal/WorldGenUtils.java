package com.github.darksoulq.abyssallib.world.gen.internal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.darksoulq.abyssallib.common.serialization.AbyssalLibBlockSerializer;
import com.github.darksoulq.abyssallib.common.serialization.BlockInfo;
import com.github.darksoulq.abyssallib.common.serialization.MinecraftBlockSerializer;
import com.github.darksoulq.abyssallib.common.serialization.ops.JsonOps;
import com.github.darksoulq.abyssallib.world.block.CustomBlock;
import com.github.darksoulq.abyssallib.world.gen.WorldGenAccess;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Utility class for managing block placement and validation logic during world generation.
 */
public class WorldGenUtils {

    /**
     * Validates if a block at a specific location matches an allowed target.
     *
     * @param level        The world generation accessor, or null for direct Bukkit access.
     * @param loc          The location to check.
     * @param validTargets A list of valid block targets. Returns true if empty or null.
     * @return True if the block matches an allowed target.
     */
    public static boolean isValidBlock(WorldGenAccess level, Location loc, List<BlockInfo> validTargets) {
        if (validTargets == null || validTargets.isEmpty()) return true;

        for (BlockInfo target : validTargets) {
            if (matchesTarget(level, loc, target)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if the block at the specified location matches a specific block target, avoiding chunk load deadlocks.
     *
     * @param level  The world generation accessor.
     * @param loc    The location to check.
     * @param target The target block info.
     * @return True if the location matches the target criteria.
     */
    private static boolean matchesTarget(WorldGenAccess level, Location loc, BlockInfo target) {
        Material mat = level != null ? level.getType(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()) : loc.getBlock().getType();
        String vanillaId = "minecraft:" + mat.name().toLowerCase();

        CustomBlock cb = CustomBlock.resolve(loc);
        String currentId = cb != null ? cb.getId().toString() : vanillaId;

        if (!target.getAsString().equals(currentId)) {
            return false;
        }

        if (target.combinedData() != null) {
            BlockData currentData = level != null ? level.getBlockData(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()) : loc.getBlock().getBlockData();
            if (target.block() instanceof BlockData targetData) {
                return currentData.matches(targetData);
            }
        }

        return true;
    }

    /**
     * Places a block into the world using its default orientation and no mirroring.
     *
     * @param level    The world generation accessor.
     * @param location The location to place the block.
     * @param info     The block information to place.
     */
    public static void placeBlock(WorldGenAccess level, Location location, BlockInfo info) {
        placeBlock(level, location, info, StructureRotation.NONE, Mirror.NONE);
    }

    /**
     * Places a block into the world applying the specified structural transformations.
     *
     * @param level    The world generation accessor, or null to place directly via Bukkit.
     * @param location The absolute world location.
     * @param info     The block information structure.
     * @param rotation The rotation transformation to apply.
     * @param mirror   The mirror transformation to apply.
     */
    public static void placeBlock(WorldGenAccess level, Location location, BlockInfo info, StructureRotation rotation, Mirror mirror) {
        Object blockObject = info.block();
        ObjectNode combinedData = info.combinedData();
        ObjectNode nbt = info.nbt();

        try {
            if (blockObject instanceof CustomBlock cb) {
                CustomBlock clone = cb.clone();
                BlockData bd = clone.getMaterial().createBlockData();

                if (combinedData != null) {
                    Map<JsonNode, JsonNode> mapData = JsonOps.INSTANCE.getMap(combinedData).orElse(Collections.emptyMap());
                    AbyssalLibBlockSerializer.deserializeBlockData(mapData, JsonOps.INSTANCE, bd);
                }

                applyTransform(bd, mirror, rotation);

                if (level != null) {
                    if (level.getType(location.getBlockX(), location.getBlockY(), location.getBlockZ()) == Material.WATER && bd instanceof Waterlogged wl) {
                        wl.setWaterlogged(true);
                    }
                    level.setBlock(location.getBlockX(), location.getBlockY(), location.getBlockZ(), clone, bd);
                } else {
                    if (location.getBlock().getType() == Material.WATER && bd instanceof Waterlogged wl) {
                        wl.setWaterlogged(true);
                    }
                    location.getBlock().setBlockData(bd, false);
                    clone.place(location.getBlock(), false);
                }

                if (combinedData != null) {
                    Map<JsonNode, JsonNode> mapData = JsonOps.INSTANCE.getMap(combinedData).orElse(Collections.emptyMap());
                    AbyssalLibBlockSerializer.deserializeEntity(clone, mapData, JsonOps.INSTANCE);
                }

            } else if (blockObject instanceof BlockData bd) {
                BlockData clone = bd.clone();

                if (combinedData != null) {
                    Map<JsonNode, JsonNode> mapData = JsonOps.INSTANCE.getMap(combinedData).orElse(Collections.emptyMap());
                    MinecraftBlockSerializer.deserialize(clone, mapData, JsonOps.INSTANCE);
                }

                applyTransform(clone, mirror, rotation);

                if (level != null) {
                    if (level.getType(location.getBlockX(), location.getBlockY(), location.getBlockZ()) == Material.WATER && clone instanceof Waterlogged wl) {
                        wl.setWaterlogged(true);
                    }
                    level.setBlock(location.getBlockX(), location.getBlockY(), location.getBlockZ(), clone);
                } else {
                    if (location.getBlock().getType() == Material.WATER && clone instanceof Waterlogged wl) {
                        wl.setWaterlogged(true);
                    }
                    location.getBlock().setBlockData(clone, false);
                }

                if (nbt != null && level == null) {
                    Map<JsonNode, JsonNode> nbtMap = JsonOps.INSTANCE.getMap(nbt).orElse(null);
                    if (nbtMap != null) MinecraftBlockSerializer.deserializeTile(location.getBlock(), nbtMap, JsonOps.INSTANCE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Applies directional mirror and rotation state modifications to a BlockData instance.
     *
     * @param bd       The block data to mutate.
     * @param mirror   The mirror type.
     * @param rotation The rotation type.
     */
    private static void applyTransform(BlockData bd, Mirror mirror, StructureRotation rotation) {
        if (mirror != Mirror.NONE) bd.mirror(mirror);
        if (rotation != StructureRotation.NONE) bd.rotate(rotation);
    }
}