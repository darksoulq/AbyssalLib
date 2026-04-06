package com.github.darksoulq.abyssallib.common.serialization;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.darksoulq.abyssallib.common.serialization.ops.JsonOps;
import com.github.darksoulq.abyssallib.world.block.CustomBlock;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Represents a fully self-contained snapshot of a block, including its position,
 * type, visual states, custom properties, and optional tile/NBT data.
 *
 * <p>This record is used as a unified data structure across:
 * <ul>
 *     <li>Structure serialization and placement</li>
 *     <li>Procedural world generation</li>
 *     <li>Block state persistence and transformation</li>
 * </ul>
 *
 * <p>A {@code BlockInfo} may represent either:
 * <ul>
 *     <li>A {@link CustomBlock} (AbyssalLib-defined block)</li>
 *     <li>A vanilla {@link BlockData} instance</li>
 * </ul>
 *
 * <p>Each instance may optionally include:
 * <ul>
 *     <li>Visual state data ({@code states})</li>
 *     <li>Custom block entity properties ({@code properties})</li>
 *     <li>Vanilla tile/NBT data ({@code nbt})</li>
 * </ul>
 *
 * @param pos        The absolute or relative position of the block as a {@link Vector}.
 *                   May be {@code null} when position is not relevant (e.g., templates).
 * @param block      The underlying block representation:
 *                   <ul>
 *                       <li>{@link CustomBlock} for custom blocks</li>
 *                       <li>{@link BlockData} for vanilla blocks</li>
 *                   </ul>
 *                   Must not be {@code null}.
 * @param states     A JSON object representing serialized block state properties
 *                   (e.g., facing, waterlogged). May be {@code null} if no states exist.
 * @param properties A JSON object representing custom block entity data specific
 *                   to {@link CustomBlock} implementations. May be {@code null}.
 * @param nbt        A JSON object representing serialized vanilla tile entity (NBT-like)
 *                   data such as inventories or sign text. May be {@code null}.
 */
public record BlockInfo(
    @Nullable Vector pos,
    Object block,
    @Nullable ObjectNode states,
    @Nullable ObjectNode properties,
    @Nullable ObjectNode nbt
) {

    /**
     * Returns the string identifier of the underlying block.
     *
     * <p>The identifier format depends on the block type:
     * <ul>
     *     <li>{@link CustomBlock}: uses its registered ID (e.g., {@code abyssal:my_block})</li>
     *     <li>{@link BlockData}: uses a vanilla Minecraft ID (e.g., {@code minecraft:stone})</li>
     * </ul>
     *
     * <p>If the block type is unknown, this method defaults to {@code minecraft:air}.
     *
     * @return The string identifier representing this block.
     */
    public String getAsString() {
        if (block instanceof CustomBlock cb) {
            return cb.getId().asString();
        } else if (block instanceof BlockData bd) {
            return "minecraft:" + bd.getMaterial().name().toLowerCase();
        }
        return "minecraft:air";
    }

    /**
     * Creates a {@link BlockInfo} snapshot from a live Bukkit {@link Block}.
     *
     * <p>This method extracts and serializes:
     * <ul>
     *     <li>Block position</li>
     *     <li>Block type (custom or vanilla)</li>
     *     <li>Block state properties</li>
     *     <li>Custom block entity data (if applicable)</li>
     *     <li>Vanilla tile/NBT data</li>
     * </ul>
     *
     * <p>Resolution priority:
     * <ol>
     *     <li>If a {@link CustomBlock} exists at the location, it is used</li>
     *     <li>Otherwise, the block is treated as vanilla {@link BlockData}</li>
     * </ol>
     *
     * @param block The Bukkit {@link Block} to snapshot. Must not be {@code null}.
     *
     * @return A fully populated {@link BlockInfo} representing the current block state.
     */
    public static BlockInfo resolve(Block block) {
        Vector pos = new Vector(block.getX(), block.getY(), block.getZ());

        Object blockObj;
        ObjectNode states = null;
        ObjectNode properties = null;
        ObjectNode nbt = null;

        CustomBlock cb = CustomBlock.resolve(block);
        if (cb != null) {
            blockObj = cb;

            Map<JsonNode, JsonNode> sMap =
                AbyssalLibBlockSerializer.serializeStates(cb, JsonOps.INSTANCE);

            if (!sMap.isEmpty()) {
                states = JsonNodeFactory.instance.objectNode();
                for (Map.Entry<JsonNode, JsonNode> entry : sMap.entrySet()) {
                    states.set(entry.getKey().asText(), entry.getValue());
                }
            }

            JsonNode pNode =
                AbyssalLibBlockSerializer.serializeProperties(cb, JsonOps.INSTANCE);

            if (pNode != null && !pNode.isEmpty()) {
                properties = (ObjectNode) pNode;
            }

        } else {
            blockObj = block.getBlockData();

            Map<JsonNode, JsonNode> sMap =
                MinecraftBlockSerializer.serialize((BlockData) blockObj, JsonOps.INSTANCE);

            if (!sMap.isEmpty()) {
                states = JsonNodeFactory.instance.objectNode();
                for (Map.Entry<JsonNode, JsonNode> entry : sMap.entrySet()) {
                    states.set(entry.getKey().asText(), entry.getValue());
                }
            }
        }

        Map<JsonNode, JsonNode> tMap =
            MinecraftBlockSerializer.serializeTile(block.getState(), JsonOps.INSTANCE);

        if (tMap != null && !tMap.isEmpty()) {
            nbt = JsonNodeFactory.instance.objectNode();
            for (Map.Entry<JsonNode, JsonNode> entry : tMap.entrySet()) {
                nbt.set(entry.getKey().asText(), entry.getValue());
            }
        }

        return new BlockInfo(pos, blockObj, states, properties, nbt);
    }
}