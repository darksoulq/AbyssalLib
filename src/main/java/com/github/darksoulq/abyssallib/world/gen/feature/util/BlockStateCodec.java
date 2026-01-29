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

import java.util.HashMap;
import java.util.Map;

public class BlockStateCodec {

    public static final Codec<BlockInfo> CODEC = new Codec<>() {
        @Override
        public <D> BlockInfo decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
            String id = Codecs.STRING.decode(ops, map.get(ops.createString("id")));

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

            if (!(ops instanceof JsonOps)) {
                throw new CodecException("BlockStateCodec requires JsonOps");
            }

            JsonNode rootNode = (JsonNode) input;
            ObjectNode combinedData = null;
            ObjectNode nbt = null;

            if (rootNode.has("states") || rootNode.has("properties")) {
                combinedData = (ObjectNode) rootNode.deepCopy();
                combinedData.remove("id");
                combinedData.remove("nbt");
            }

            if (rootNode.has("nbt")) {
                nbt = (ObjectNode) rootNode.get("nbt");
            }

            return new BlockInfo(new Vector(0,0,0), blockObject, combinedData, nbt);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <D> D encode(DynamicOps<D> ops, BlockInfo value) throws CodecException {
            Map<D, D> map = new HashMap<>();
            String id;
            Map<D, D> combined = new HashMap<>();

            if (value.block() instanceof CustomBlock cb) {
                id = cb.getId().toString();
                Map<D, D> serialized = AbyssalLibBlockSerializer.serialize(cb, ops);
                combined.putAll(serialized);
            } else {
                BlockData bd = (BlockData) value.block();
                id = "minecraft:" + bd.getMaterial().name().toLowerCase();
                Map<D, D> states = MinecraftBlockSerializer.serialize(bd, ops);
                combined.put(ops.createString("states"), ops.createMap(states));
            }

            map.put(ops.createString("id"), Codecs.STRING.encode(ops, id));

            D states = combined.get(ops.createString("states"));
            if (states != null) map.put(ops.createString("states"), states);

            D properties = combined.get(ops.createString("properties"));
            if (properties != null) map.put(ops.createString("properties"), properties);

            if (value.nbt() != null) {
                map.put(ops.createString("nbt"), (D) value.nbt());
            }
            return ops.createMap(map);
        }
    };
}