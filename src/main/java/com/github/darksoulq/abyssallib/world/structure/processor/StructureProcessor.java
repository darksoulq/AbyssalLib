package com.github.darksoulq.abyssallib.world.structure.processor;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.gen.WorldGenAccess;
import org.bukkit.Location;
import org.bukkit.World;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * The base class for all structure processors.
 * Processors are executed in a pipeline during structure placement. Each processor
 * can modify the block being placed, change its properties, or return {@code null}
 * to prevent the block from being placed entirely.
 */
public abstract class StructureProcessor {

    /**
     * Polymorphic codec for serializing and deserializing structure processors.
     * It uses the "type" field to identify the processor implementation registered
     * within {@link Registries#PROCESSOR_TYPES}.
     */
    public static final Codec<StructureProcessor> CODEC = new Codec<>() {
        /**
         * Decodes a specific StructureProcessor based on its registered type ID.
         *
         * @param <D>
         * The type of the serialized data.
         * @param ops
         * The dynamic operations logic used for parsing.
         * @param input
         * The serialized input data.
         * @return
         * The decoded {@link StructureProcessor} instance.
         * @throws CodecException
         * If the type is missing or unregistered in the registry.
         */
        @Override
        public <D> StructureProcessor decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map for StructureProcessor"));
            D typeNode = map.get(ops.createString("type"));
            if (typeNode == null) {
                throw new CodecException("Missing 'type' in StructureProcessor");
            }

            String typeId = ops.getStringValue(typeNode).orElseThrow(() -> new CodecException("Invalid type value"));
            StructureProcessorType<?> type = Registries.PROCESSOR_TYPES.get(typeId);
            if (type == null) {
                throw new CodecException("Unknown processor type: " + typeId);
            }

            return type.codec().decode(ops, input);
        }

        /**
         * Encodes a StructureProcessor, injecting its type ID into the resulting map.
         *
         * @param <D>
         * The target type for serialized data.
         * @param ops
         * The dynamic operations logic used for encoding.
         * @param value
         * The processor instance to encode.
         * @return
         * The encoded data object.
         * @throws CodecException
         * If the processor type is not registered or the inner codec fails.
         */
        @Override
        @SuppressWarnings("unchecked")
        public <D> D encode(DynamicOps<D> ops, StructureProcessor value) throws CodecException {
            StructureProcessorType<StructureProcessor> type = (StructureProcessorType<StructureProcessor>) value.getType();
            String typeId = Registries.PROCESSOR_TYPES.getId(type);
            if (typeId == null) {
                throw new CodecException("Unregistered processor type");
            }

            D encoded = type.codec().encode(ops, value);
            Map<D, D> map = ops.getMap(encoded).orElseThrow(() -> new CodecException("Processor codec must return a map"));
            map.put(ops.createString("type"), ops.createString(typeId));
            return ops.createMap(map);
        }
    };

    /**
     * Processes a block during standard world placement.
     *
     * @param world
     * The Bukkit {@link World} where the structure is being placed.
     * @param origin
     * The origin {@link Location} of the structure placement.
     * @param current
     * The current {@link BlockInfo} as modified by previous processors in the stack.
     * @param original
     * The original {@link BlockInfo} as defined in the source structure file.
     * @return
     * The modified {@link BlockInfo}, or {@code null} to skip placement of this specific block.
     */
    @Nullable
    public abstract BlockInfo process(World world, Location origin, BlockInfo current, BlockInfo original);

    /**
     * Processes a block during world generation placement.
     *
     * @param level
     * The {@link WorldGenAccess} providing thread-safe access during world generation.
     * @param origin
     * The origin {@link Location} of the structure placement.
     * @param current
     * The current {@link BlockInfo} as modified by previous processors.
     * @param original
     * The original {@link BlockInfo} as saved in the structure file.
     * @return
     * The modified {@link BlockInfo}, or {@code null} to skip placement.
     */
    @Nullable
    public abstract BlockInfo process(WorldGenAccess level, Location origin, BlockInfo current, BlockInfo original);

    /**
     * Retrieves the structure processor type associated with this specific implementation.
     * This is primarily used for serialization via the polymorphic codec.
     *
     * @return
     * The {@link StructureProcessorType} identifying this processor.
     */
    public abstract StructureProcessorType<?> getType();
}