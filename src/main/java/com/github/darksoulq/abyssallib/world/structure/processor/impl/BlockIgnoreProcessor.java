package com.github.darksoulq.abyssallib.world.structure.processor.impl;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.world.block.CustomBlock;
import com.github.darksoulq.abyssallib.world.gen.WorldGenAccess;
import com.github.darksoulq.abyssallib.world.structure.processor.BlockInfo;
import com.github.darksoulq.abyssallib.world.structure.processor.StructureProcessor;
import com.github.darksoulq.abyssallib.world.structure.processor.StructureProcessorType;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A structure processor that prevents specific blocks from being placed.
 * <p>
 * This processor checks the namespaced ID of every block in the structure against
 * a list of ignored IDs. If a match is found, the block placement is cancelled.
 * It supports both vanilla Minecraft blocks and AbyssalLib custom blocks.
 */
public class BlockIgnoreProcessor extends StructureProcessor {

    /**
     * The codec used for serializing and deserializing the block ignore processor.
     * <p>
     * It maps the "blocks" field, which is a list of strings representing the
     * namespaced IDs to be ignored.
     */
    public static final Codec<BlockIgnoreProcessor> CODEC = new Codec<>() {
        /**
         * Decodes a BlockIgnoreProcessor from the provided serialized data.
         *
         * @param ops   The dynamic operations logic.
         * @param input The serialized input data.
         * @param <D>   The data format type.
         * @return A new instance of {@link BlockIgnoreProcessor}.
         * @throws CodecException If the map structure is invalid.
         */
        @Override
        public <D> BlockIgnoreProcessor decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
            D listNode = map.get(ops.createString("blocks"));
            List<String> blocks = new ArrayList<>();
            if (listNode != null) {
                blocks = Codecs.STRING.list().decode(ops, listNode);
            }
            return new BlockIgnoreProcessor(blocks);
        }

        /**
         * Encodes the BlockIgnoreProcessor into a serialized format.
         *
         * @param ops   The dynamic operations logic.
         * @param value The processor instance to encode.
         * @param <D>   The data format type.
         * @return A map containing the list of ignored block IDs.
         * @throws CodecException If serialization fails.
         */
        @Override
        public <D> D encode(DynamicOps<D> ops, BlockIgnoreProcessor value) throws CodecException {
            Map<D, D> map = new HashMap<>();
            map.put(ops.createString("blocks"), Codecs.STRING.list().encode(ops, value.ignoredIds));
            return ops.createMap(map);
        }
    };

    /**
     * The registered type definition for the block ignore structure processor.
     */
    public static final StructureProcessorType<BlockIgnoreProcessor> TYPE = () -> CODEC;

    /** The list of namespaced block identifiers to be excluded from placement. */
    private final List<String> ignoredIds;

    /**
     * Constructs a new BlockIgnoreProcessor.
     *
     * @param ignoredIds A list of block IDs (e.g., "minecraft:air", "abyssallib:test_block").
     */
    public BlockIgnoreProcessor(List<String> ignoredIds) {
        this.ignoredIds = ignoredIds;
    }

    /**
     * Processes a block placement in a standard world context.
     *
     * @param world    The Bukkit world.
     * @param origin   The structure origin.
     * @param current  The current {@link BlockInfo}.
     * @param original The original {@link BlockInfo}.
     * @return {@code null} if the block is ignored, otherwise the {@code current} block info.
     */
    @Override
    public BlockInfo process(World world, Location origin, BlockInfo current, BlockInfo original) {
        if (shouldIgnore(current)) return null;
        return current;
    }

    /**
     * Processes a block placement in a world generation context.
     *
     * @param level    The generation accessor.
     * @param origin   The structure origin.
     * @param current  The current {@link BlockInfo}.
     * @param original The original {@link BlockInfo}.
     * @return {@code null} if the block is ignored, otherwise the {@code current} block info.
     */
    @Override
    public BlockInfo process(WorldGenAccess level, Location origin, BlockInfo current, BlockInfo original) {
        if (shouldIgnore(current)) return null;
        return current;
    }

    /**
     * Determines if the given block info matches an entry in the ignored IDs list.
     * <p>
     * Resolves the ID from either a {@link CustomBlock} or {@link BlockData}.
     *
     * @param current The block info to check.
     * @return {@code true} if the block should be ignored; {@code false} otherwise.
     */
    private boolean shouldIgnore(BlockInfo current) {
        String id;
        if (current.block() instanceof CustomBlock cb) {
            id = cb.getId().toString();
        } else if (current.block() instanceof BlockData bd) {
            id = "minecraft:" + bd.getMaterial().name().toLowerCase();
        } else {
            return false;
        }
        return ignoredIds.contains(id);
    }

    /**
     * Retrieves the specific type definition for this processor.
     *
     * @return The {@link StructureProcessorType} associated with {@link BlockIgnoreProcessor}.
     */
    @Override
    public StructureProcessorType<?> getType() {
        return TYPE;
    }
}