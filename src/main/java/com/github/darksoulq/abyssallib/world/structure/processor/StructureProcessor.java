package com.github.darksoulq.abyssallib.world.structure.processor;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.gen.WorldGenAccess;
import org.bukkit.Location;
import org.bukkit.World;

import javax.annotation.Nullable;
import java.util.Map;

public abstract class StructureProcessor {

    public static final Codec<StructureProcessor> CODEC = new Codec<>() {
        @Override
        public <D> StructureProcessor decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map for StructureProcessor"));
            D typeNode = map.get(ops.createString("type"));
            if (typeNode == null) throw new CodecException("Missing 'type' in StructureProcessor");

            String typeId = ops.getStringValue(typeNode).orElseThrow(() -> new CodecException("Invalid type value"));
            StructureProcessorType<?> type = Registries.PROCESSOR_TYPES.get(typeId);
            if (type == null) throw new CodecException("Unknown processor type: " + typeId);

            return type.codec().decode(ops, input);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <D> D encode(DynamicOps<D> ops, StructureProcessor value) throws CodecException {
            StructureProcessorType<StructureProcessor> type = (StructureProcessorType<StructureProcessor>) value.getType();
            String typeId = Registries.PROCESSOR_TYPES.getId(type);
            if (typeId == null) throw new CodecException("Unregistered processor type");

            D encoded = type.codec().encode(ops, value);
            Map<D, D> map = ops.getMap(encoded).orElseThrow(() -> new CodecException("Processor codec must return a map"));
            map.put(ops.createString("type"), ops.createString(typeId));
            return ops.createMap(map);
        }
    };

    @Nullable
    public abstract BlockInfo process(World world, Location origin, BlockInfo current, BlockInfo original);

    @Nullable
    public abstract BlockInfo process(WorldGenAccess level, Location origin, BlockInfo current, BlockInfo original);

    public abstract StructureProcessorType<?> getType();
}