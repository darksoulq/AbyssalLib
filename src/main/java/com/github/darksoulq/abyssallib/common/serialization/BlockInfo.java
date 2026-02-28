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
 * A data container representing a specific block state and optional NBT data.
 * Can be used in context of structures, generation, or generalized block tagging.
 *
 * @param pos          The transformed position vector where the block is located (can be null in abstract contexts).
 * @param block        The actual block object (typically {@link BlockData} or {@link CustomBlock}).
 * @param combinedData The serialized visual state data of the block.
 * @param nbt          The serialized tile entity or property data for the block.
 */
public record BlockInfo(@Nullable Vector pos, Object block, @Nullable ObjectNode combinedData, @Nullable ObjectNode nbt) {
    
    /**
     * Resolves the string identifier of the underlying block.
     * Useful for tags, serialization, and generalized lookups.
     *
     * @return The string identifier (e.g. "minecraft:stone" or "abyssallib:custom_block").
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
     * Automatically constructs a BlockInfo from a physical Bukkit block in the world.
     * Extracts its properties, NBT/TileState data, and identifies if it is a CustomBlock.
     *
     * @param block The block in the world to resolve.
     * @return A fully constructed BlockInfo representation.
     */
    public static BlockInfo resolve(Block block) {
        Vector pos = new Vector(block.getX(), block.getY(), block.getZ());
        Object blockObj;
        ObjectNode combinedData = JsonNodeFactory.instance.objectNode();
        ObjectNode nbt = null;

        CustomBlock cb = CustomBlock.resolve(block);
        if (cb != null) {
            blockObj = cb;
            Map<JsonNode, JsonNode> serialized = AbyssalLibBlockSerializer.serialize(cb, JsonOps.INSTANCE);

            JsonNode statesNode = serialized.get(JsonNodeFactory.instance.textNode("states"));
            if (statesNode != null) {
                combinedData.set("states", statesNode);
            }

            JsonNode propsNode = serialized.get(JsonNodeFactory.instance.textNode("properties"));
            if (propsNode != null) {
                nbt = JsonNodeFactory.instance.objectNode();
                nbt.set("properties", propsNode);
            }
        } else {
            blockObj = block.getBlockData();
            Map<JsonNode, JsonNode> states = MinecraftBlockSerializer.serialize((BlockData) blockObj, JsonOps.INSTANCE);
            if (!states.isEmpty()) {
                ObjectNode statesNode = JsonNodeFactory.instance.objectNode();
                states.forEach((k, v) -> statesNode.set(k.asText(), v));
                combinedData.set("states", statesNode);
            }

            Map<JsonNode, JsonNode> tileMap = MinecraftBlockSerializer.serializeTile(block, JsonOps.INSTANCE);
            if (tileMap != null && !tileMap.isEmpty()) {
                nbt = JsonNodeFactory.instance.objectNode();
                for (Map.Entry<JsonNode, JsonNode> entry : tileMap.entrySet()) {
                    nbt.set(entry.getKey().asText(), entry.getValue());
                }
            }
        }

        if (combinedData.isEmpty()) {
            combinedData = null;
        }

        return new BlockInfo(pos, blockObj, combinedData, nbt);
    }
}