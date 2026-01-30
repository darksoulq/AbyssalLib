package com.github.darksoulq.abyssallib.world.gen.internal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.darksoulq.abyssallib.common.serialization.ops.JsonOps;
import com.github.darksoulq.abyssallib.world.block.CustomBlock;
import com.github.darksoulq.abyssallib.world.gen.WorldGenAccess;
import com.github.darksoulq.abyssallib.world.gen.nms.NMSWorldGenAccess;
import com.github.darksoulq.abyssallib.world.structure.processor.BlockInfo;
import com.github.darksoulq.abyssallib.world.structure.serializer.AbyssalLibBlockSerializer;
import com.github.darksoulq.abyssallib.world.structure.serializer.MinecraftBlockSerializer;
import org.bukkit.Bukkit;
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
 * Internal utility class for managing block placement and validation during world generation.
 * <p>
 * This class provides methods to safely place serialized block information, handling
 * complex transformations such as rotation and mirroring for both vanilla and AbyssalLib
 * {@link CustomBlock}s.
 */
public class WorldGenUtils {

    /**
     * Validates if a block at a specific location matches a list of allowed identifiers.
     * <p>
     * Supports both vanilla IDs (e.g., "minecraft:stone") and {@link CustomBlock} IDs.
     *
     * @param level    The world generation accessor.
     * @param loc      The {@link Location} to check.
     * @param validIds A {@link List} of namespaced identifiers. If null or empty, returns true.
     * @return {@code true} if the block matches an allowed ID or the list is empty.
     */
    public static boolean isValidBlock(WorldGenAccess level, Location loc, List<String> validIds) {
        if (validIds == null || validIds.isEmpty()) return true;

        Material mat = level.getType(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        String vanillaId = "minecraft:" + mat.name().toLowerCase();

        if (validIds.contains(vanillaId)) return true;

        CustomBlock cb = CustomBlock.from(loc.getBlock());
        if (cb != null) {
            return validIds.contains(cb.getId().toString());
        }

        return false;
    }

    /**
     * Places a block based on {@link BlockInfo} with default orientation.
     *
     * @param level    The world generation accessor.
     * @param location The {@link Location} to place the block.
     * @param info     The {@link BlockInfo} containing block data and NBT.
     */
    public static void placeBlock(WorldGenAccess level, Location location, BlockInfo info) {
        placeBlock(level, location, info, StructureRotation.NONE, Mirror.NONE);
    }

    /**
     * Places a block with specific transformations applied.
     * <p>
     * This method handles the logic for {@link CustomBlock} cloning, {@link BlockData}
     * deserialization, and NBT application. It automatically detects if
     * {@link NMSWorldGenAccess} is being used for optimized placement.
     *
     * @param level    The world generation accessor.
     * @param location The {@link Location} to place the block.
     * @param info     The {@link BlockInfo} structure.
     * @param rotation The {@link StructureRotation} to apply.
     * @param mirror   The {@link Mirror} transformation to apply.
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

                if (level instanceof NMSWorldGenAccess nmsLevel) {
                    nmsLevel.setBlock(location.getBlockX(), location.getBlockY(), location.getBlockZ(), clone, bd);
                } else if (level != null) {
                    if (location.getBlock().getType() == Material.WATER && bd instanceof Waterlogged wl) wl.setWaterlogged(true);
                    level.setBlock(location.getBlockX(), location.getBlockY(), location.getBlockZ(), bd);
                    clone.place(location.getBlock(), false);
                } else {
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

                if (level instanceof NMSWorldGenAccess nmsLevel) {
                    nmsLevel.setBlock(location.getBlockX(), location.getBlockY(), location.getBlockZ(), clone);
                } else if (level != null) {
                    if (location.getBlock().getType() == Material.WATER && clone instanceof Waterlogged wl) wl.setWaterlogged(true);
                    level.setBlock(location.getBlockX(), location.getBlockY(), location.getBlockZ(), clone);
                } else {
                    location.getBlock().setBlockData(clone, false);
                }

                if (nbt != null) {
                    Map<JsonNode, JsonNode> nbtMap = JsonOps.INSTANCE.getMap(nbt).orElse(null);
                    if (nbtMap != null) MinecraftBlockSerializer.deserializeTile(location.getBlock(), nbtMap, JsonOps.INSTANCE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Internal helper to apply mirror and rotation to block data.
     *
     * @param bd       The {@link BlockData} to transform.
     * @param mirror   The {@link Mirror} type.
     * @param rotation The {@link StructureRotation} type.
     */
    private static void applyTransform(BlockData bd, Mirror mirror, StructureRotation rotation) {
        if (mirror != Mirror.NONE) bd.mirror(mirror);
        if (rotation != StructureRotation.NONE) bd.rotate(rotation);
    }
}