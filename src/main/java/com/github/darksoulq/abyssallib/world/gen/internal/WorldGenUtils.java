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

public class WorldGenUtils {

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

    public static void placeBlock(WorldGenAccess level, Location location, BlockInfo info) {
        placeBlock(level, location, info, StructureRotation.NONE, Mirror.NONE);
    }

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

    private static void applyTransform(BlockData bd, Mirror mirror, StructureRotation rotation) {
        if (mirror != Mirror.NONE) bd.mirror(mirror);
        if (rotation != StructureRotation.NONE) bd.rotate(rotation);
    }
}