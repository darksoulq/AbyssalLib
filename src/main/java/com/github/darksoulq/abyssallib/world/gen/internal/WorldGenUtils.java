package com.github.darksoulq.abyssallib.world.gen.internal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.darksoulq.abyssallib.common.serialization.AbyssalLibBlockSerializer;
import com.github.darksoulq.abyssallib.common.serialization.BlockInfo;
import com.github.darksoulq.abyssallib.common.serialization.MinecraftBlockSerializer;
import com.github.darksoulq.abyssallib.common.serialization.ops.JsonOps;
import com.github.darksoulq.abyssallib.world.block.CustomBlock;
import com.github.darksoulq.abyssallib.world.gen.NMSWorldGenAccess;
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
 *
 * <p>This class bridges high-level structure generation logic with low-level block
 * placement APIs, supporting:
 * <ul>
 *     <li>Validation of placement conditions</li>
 *     <li>CustomBlock and vanilla BlockData placement</li>
 *     <li>Application of rotation and mirroring transforms</li>
 *     <li>State, property, and NBT deserialization</li>
 * </ul>
 *
 * <p>All methods are static and designed to work in both:
 * <ul>
 *     <li>{@link WorldGenAccess} (async/virtual world generation)</li>
 *     <li>Direct Bukkit world access (when {@code level == null})</li>
 * </ul>
 */
public class WorldGenUtils {

    /**
     * Determines whether a block at a given location is valid for replacement
     * based on a list of allowed {@link BlockInfo} targets.
     *
     * <p>If {@code validTargets} is null or empty, this method always returns {@code true}.
     *
     * @param level        The world generation access context. May be null when operating
     *                     directly on a Bukkit world.
     * @param loc          The target location to validate.
     * @param validTargets A list of allowed block definitions. May be null or empty.
     *
     * @return {@code true} if the block matches any valid target or if no restrictions exist,
     *         otherwise {@code false}.
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
     * Checks whether the block at the given location matches a specific {@link BlockInfo}.
     *
     * <p>This includes:
     * <ul>
     *     <li>Matching block ID (vanilla or custom)</li>
     *     <li>Optional block state comparison</li>
     * </ul>
     *
     * @param level  The world generation access context. May be null.
     * @param loc    The location of the block being checked.
     * @param target The target block definition to compare against.
     *
     * @return {@code true} if the block matches the target definition, otherwise {@code false}.
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
     * Places a block at the specified location using default transformation settings
     * (no rotation or mirroring).
     *
     * @param level    The world generation access context. May be null.
     * @param location The target placement location.
     * @param info     The {@link BlockInfo} describing the block to place.
     */
    public static void placeBlock(WorldGenAccess level, Location location, BlockInfo info) {
        placeBlock(level, location, info, StructureRotation.NONE, Mirror.NONE);
    }

    /**
     * Places a block at the specified location using full configuration data including
     * rotation, mirroring, block states, properties, and NBT.
     *
     * <p>Supports both:
     * <ul>
     *     <li>{@link CustomBlock} placement (with entity data)</li>
     *     <li>Vanilla {@link BlockData} placement</li>
     * </ul>
     *
     * <p>Also handles:
     * <ul>
     *     <li>Waterlogging when placed into water</li>
     *     <li>Tile entity (NBT) restoration</li>
     * </ul>
     *
     * @param level    The world generation access context. If null, placement occurs
     *                 directly in the Bukkit world.
     * @param location The target location for placement.
     * @param info     The {@link BlockInfo} containing block, state, and NBT data.
     * @param rotation The rotation to apply to the block.
     * @param mirror   The mirror transformation to apply.
     */
    public static void placeBlock(WorldGenAccess level, Location location, BlockInfo info, StructureRotation rotation, Mirror mirror) {
        if (level instanceof NMSWorldGenAccess nms && !nms.isInRegion(location.getBlockX(), location.getBlockY(), location.getBlockZ())) {
            new org.bukkit.scheduler.BukkitRunnable() {
                @Override
                public void run() {
                    if (!location.isChunkLoaded()) location.getChunk().load(true);
                    placeBlock(null, location, info, rotation, mirror);
                }
            }.runTask(com.github.darksoulq.abyssallib.AbyssalLib.getInstance());
            return;
        }

        Object blockObject = info.block();
        ObjectNode states = info.states();
        ObjectNode properties = info.properties();
        ObjectNode nbt = info.nbt();

        try {
            if (blockObject instanceof CustomBlock cb) {
                CustomBlock clone = cb.clone();
                BlockData bd = clone.getMaterial().createBlockData();

                if (states != null) {
                    Map<JsonNode, JsonNode> mapData = JsonOps.INSTANCE.getMap(states).orElse(Collections.emptyMap());
                    AbyssalLibBlockSerializer.deserializeBlockData(mapData, JsonOps.INSTANCE, bd);
                }

                applyTransform(bd, mirror, rotation);

                if (level != null) {
                    if (level.getType(location.getBlockX(), location.getBlockY(), location.getBlockZ()) == Material.WATER
                        && bd instanceof Waterlogged wl) {
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

                if (properties != null) {
                    AbyssalLibBlockSerializer.deserializeEntity(clone, properties, JsonOps.INSTANCE);
                }

            } else if (blockObject instanceof BlockData bd) {
                BlockData clone = bd.clone();

                if (states != null) {
                    Map<JsonNode, JsonNode> mapData = JsonOps.INSTANCE.getMap(states).orElse(Collections.emptyMap());
                    MinecraftBlockSerializer.deserialize(clone, mapData, JsonOps.INSTANCE);
                }

                applyTransform(clone, mirror, rotation);

                if (level != null) {
                    if (level.getType(location.getBlockX(), location.getBlockY(), location.getBlockZ()) == Material.WATER
                        && clone instanceof Waterlogged wl) {
                        wl.setWaterlogged(true);
                    }
                    level.setBlock(location.getBlockX(), location.getBlockY(), location.getBlockZ(), clone);
                } else {
                    if (location.getBlock().getType() == Material.WATER && clone instanceof Waterlogged wl) {
                        wl.setWaterlogged(true);
                    }
                    location.getBlock().setBlockData(clone, false);
                }
            }

            if (nbt != null) {
                BlockState state = level != null
                    ? level.getBlockState(location.getBlockX(), location.getBlockY(), location.getBlockZ())
                    : location.getBlock().getState();

                Map<JsonNode, JsonNode> nbtMap = JsonOps.INSTANCE.getMap(nbt).orElse(null);
                if (nbtMap != null) {
                    MinecraftBlockSerializer.deserializeTile(state, nbtMap, JsonOps.INSTANCE);
                    state.update(true, false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Applies mirror and rotation transformations to a {@link BlockData} instance.
     *
     * <p>Transformations are applied in the following order:
     * <ol>
     *     <li>Mirror</li>
     *     <li>Rotation</li>
     * </ol>
     *
     * @param bd       The block data to transform.
     * @param mirror   The mirror transformation to apply.
     * @param rotation The rotation transformation to apply.
     */
    private static void applyTransform(BlockData bd, Mirror mirror, StructureRotation rotation) {
        if (mirror != Mirror.NONE) bd.mirror(mirror);
        if (rotation != StructureRotation.NONE) bd.rotate(rotation);
    }
}