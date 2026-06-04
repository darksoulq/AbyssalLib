package com.github.darksoulq.abyssallib.world.structure.processor;

import com.github.darksoulq.abyssallib.common.serialization.BlockInfo;
import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.gen.WorldGenAccess;
import org.bukkit.Location;
import org.bukkit.World;

import javax.annotation.Nullable;

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
    public static final Codec<StructureProcessor> CODEC = Codec.dispatch(
        StructureProcessor.class,
        "type",
        Codecs.STRING,
        processor -> {
            String typeId = Registries.PROCESSOR_TYPES.getId(processor.getType());
            if (typeId == null) {
                throw new IllegalStateException("Unregistered processor type");
            }
            return typeId;
        },
        typeId -> {
            StructureProcessorType<?> type = Registries.PROCESSOR_TYPES.get(typeId);
            if (type == null) {
                return Codec.error("Unknown processor type: " + typeId);
            }
            return type.codec().unchecked();
        }
    ).describe("StructureProcessor");

    /**
     * Processes a block during standard world placement.
     *
     * @param world    The Bukkit {@link World} where the structure is being placed.
     * @param origin   The origin {@link Location} of the structure placement.
     * @param current  The current {@link BlockInfo} as modified by previous processors in the stack.
     * @param original The original {@link BlockInfo} as defined in the source structure file.
     * @return The modified {@link BlockInfo}, or {@code null} to skip placement of this specific block.
     */
    @Nullable
    public abstract BlockInfo process(World world, Location origin, BlockInfo current, BlockInfo original);

    /**
     * Processes a block during world generation placement.
     *
     * @param level    The {@link WorldGenAccess} providing thread-safe access during world generation.
     * @param origin   The origin {@link Location} of the structure placement.
     * @param current  The current {@link BlockInfo} as modified by previous processors.
     * @param original The original {@link BlockInfo} as saved in the structure file.
     * @return The modified {@link BlockInfo}, or {@code null} to skip placement.
     */
    @Nullable
    public abstract BlockInfo process(WorldGenAccess level, Location origin, BlockInfo current, BlockInfo original);

    /**
     * Retrieves the structure processor type associated with this specific implementation.
     * This is primarily used for serialization via the polymorphic codec.
     *
     * @return The {@link StructureProcessorType} identifying this processor.
     */
    public abstract StructureProcessorType<?> getType();
}