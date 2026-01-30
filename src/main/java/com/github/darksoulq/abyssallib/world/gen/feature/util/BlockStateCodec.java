package com.github.darksoulq.abyssallib.world.gen.feature.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.ops.JsonOps;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.block.CustomBlock;
import com.github.darksoulq.abyssallib.world.structure.processor.BlockInfo;
import com.github.darksoulq.abyssallib.world.structure.serializer.AbyssalLibBlockSerializer;
import com.github.darksoulq.abyssallib.world.structure.serializer.MinecraftBlockSerializer;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.Vector;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A utility class containing serialization logic for block states within the world generation system.
 * <p>
 * This codec facilitates the conversion between {@link BlockInfo} objects and their
 * serialized representations. It handles namespaced identifiers, relative positions,
 * block properties (states), and TileEntity NBT data.
 */
public class BlockStateCodec {

    /**
     * A polymorphic codec for {@link BlockInfo}.
     * <p>
     * Handles both vanilla "minecraft:" IDs and AbyssalLib custom block IDs. During
     * decoding, it reconstructs block objects, applies property states, and attaches
     * NBT data. During encoding, it flattens these objects into a map structure.
     */
    public static final Codec<BlockInfo> CODEC = new Codec<>() {

        /**
         * Decodes a {@link BlockInfo} instance from a serialized map.
         *
         * @param ops   The {@link DynamicOps} logic being used.
         * @param input The serialized data object.
         * @param <D>   The data type (e.g., JsonNode).
         * @return A fully reconstructed {@link BlockInfo} instance.
         * @throws CodecException If the ID is invalid, the block type is unknown, or
         * the data format is unexpected.
         */
        @Override
        public <D> BlockInfo decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));

            String id = Codecs.STRING.decode(ops, map.get(ops.createString("id")));

            Vector pos = new Vector(0, 0, 0);
            D posObj = map.get(ops.createString("pos"));
            if (posObj != null) {
                pos = Codecs.VECTOR_I.decode(ops, posObj);
            }

            Object blockObject;
            if (id.startsWith("minecraft:")) {
                Material mat = Material.matchMaterial(id.substring(10));
                if (mat == null || !mat.isBlock()) throw new CodecException("Invalid vanilla material: " + id);
                blockObject = mat.createBlockData();
            } else {
                CustomBlock base = Registries.BLOCKS.get(id);
                if (base == null) throw new CodecException("Unknown custom block: " + id);
                blockObject = base.clone();
            }

            ObjectNode combinedData = null;
            ObjectNode nbt = null;

            if (ops instanceof JsonOps && input instanceof JsonNode jsonInput) {
                if (jsonInput.has("states") || jsonInput.has("properties")) {
                    combinedData = (ObjectNode) jsonInput.deepCopy();
                    combinedData.remove("id");
                    combinedData.remove("pos");
                    combinedData.remove("nbt");
                }

                if (jsonInput.has("nbt")) {
                    nbt = (ObjectNode) jsonInput.get("nbt");
                }
            }

            if (blockObject instanceof BlockData bd) {
                D states = map.get(ops.createString("states"));
                if (states != null) {
                    Map<D, D> statesMap = ops.getMap(states).orElse(Collections.emptyMap());
                    MinecraftBlockSerializer.deserialize(bd, statesMap, ops);
                }
            } else if (blockObject instanceof CustomBlock cb) {
                if (combinedData != null && combinedData.has("states")) {
                    D states = map.get(ops.createString("states"));
                    if (states != null) {
                        Map<D, D> statesMap = ops.getMap(states).orElse(Collections.emptyMap());
                        BlockData tempData = cb.getMaterial().createBlockData();
                        MinecraftBlockSerializer.deserialize(tempData, statesMap, ops);
                    }
                }
            }

            return new BlockInfo(pos, blockObject, combinedData, nbt);
        }

        /**
         * Encodes a {@link BlockInfo} instance into a serialized representation.
         *
         * @param ops   The {@link DynamicOps} logic being used.
         * @param value The {@link BlockInfo} instance to serialize.
         * @param <D>   The data type (e.g., JsonNode).
         * @return The serialized data object.
         * @throws CodecException If serialization of internal components fails.
         */
        @Override
        public <D> D encode(DynamicOps<D> ops, BlockInfo value) throws CodecException {
            Map<D, D> map = new HashMap<>();
            String id;
            Map<D, D> nbtMap = new HashMap<>();

            if (value.nbt() != null) {
                @SuppressWarnings("unchecked")
                Map<D, D> existing = (Map<D, D>) ops.getMap((D) value.nbt()).orElse(null);
                if (existing != null) nbtMap.putAll(existing);
            }

            if (value.block() instanceof CustomBlock cb) {
                id = cb.getId().toString();
                Map<D, D> serialized = AbyssalLibBlockSerializer.serialize(cb, ops);

                if (serialized.containsKey(ops.createString("states"))) {
                    map.put(ops.createString("states"), serialized.get(ops.createString("states")));
                }

                if (serialized.containsKey(ops.createString("properties"))) {
                    nbtMap.put(ops.createString("properties"), serialized.get(ops.createString("properties")));
                }
            } else {
                BlockData bd = (BlockData) value.block();
                id = "minecraft:" + bd.getMaterial().name().toLowerCase();

                Map<D, D> states = MinecraftBlockSerializer.serialize(bd, ops);
                if (!states.isEmpty()) {
                    map.put(ops.createString("states"), ops.createMap(states));
                }
            }

            map.put(ops.createString("id"), Codecs.STRING.encode(ops, id));
            map.put(ops.createString("pos"), Codecs.VECTOR_I.encode(ops, value.pos()));

            if (!nbtMap.isEmpty()) {
                map.put(ops.createString("nbt"), ops.createMap(nbtMap));
            }

            return ops.createMap(map);
        }
    };
}