package com.github.darksoulq.abyssallib.world.structure.processor.impl;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.world.gen.WorldGenAccess;
import com.github.darksoulq.abyssallib.world.structure.processor.BlockInfo;
import com.github.darksoulq.abyssallib.world.structure.processor.StructureProcessor;
import com.github.darksoulq.abyssallib.world.structure.processor.StructureProcessorType;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * A structure processor that applies a chance-based filter to block placement.
 * <p>
 * This processor mimics the "Integrity" setting found in vanilla Minecraft structure blocks.
 * For each block in the structure, a random roll is performed; if the roll exceeds the
 * integrity value, the block is skipped (returned as null), creating a decayed or
 * eroded effect.
 */
public class IntegrityProcessor extends StructureProcessor {

    /**
     * The codec used for serializing and deserializing the integrity processor.
     * <p>
     * It handles the "integrity" float field, which represents the placement
     * probability between 0.0 and 1.0.
     */
    public static final Codec<IntegrityProcessor> CODEC = new Codec<>() {
        /**
         * Decodes an IntegrityProcessor from the provided serialized data.
         *
         * @param ops   The dynamic operations logic.
         * @param input The serialized input data.
         * @param <D>   The data format type.
         * @return A new instance of {@link IntegrityProcessor}.
         * @throws CodecException If the map structure is invalid.
         */
        @Override
        public <D> IntegrityProcessor decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
            D val = map.get(ops.createString("integrity"));
            float integrity = 1.0f;
            if (val != null) {
                integrity = Codecs.FLOAT.decode(ops, val);
            }
            return new IntegrityProcessor(integrity);
        }

        /**
         * Encodes the IntegrityProcessor into a serialized format.
         *
         * @param ops   The dynamic operations logic.
         * @param value The processor instance to encode.
         * @param <D>   The data format type.
         * @return A map containing the integrity value.
         * @throws CodecException If serialization fails.
         */
        @Override
        public <D> D encode(DynamicOps<D> ops, IntegrityProcessor value) throws CodecException {
            Map<D, D> map = new HashMap<>();
            map.put(ops.createString("integrity"), Codecs.FLOAT.encode(ops, value.integrity));
            return ops.createMap(map);
        }
    };

    /**
     * The registered type definition for the integrity structure processor.
     */
    public static final StructureProcessorType<IntegrityProcessor> TYPE = () -> CODEC;

    /** The probability (0.0 to 1.0) that a block will be successfully placed. */
    private final float integrity;

    /** Random source for performing the integrity checks. */
    private final Random random = new Random();

    /**
     * Constructs a new IntegrityProcessor.
     *
     * @param integrity The placement chance. 1.0 means all blocks place; 0.5 means half are skipped.
     */
    public IntegrityProcessor(float integrity) {
        this.integrity = integrity;
    }

    /**
     * Processes a block placement in a standard world context.
     * <p>
     * Performs a random float check against the integrity threshold.
     *
     * @param world    The Bukkit world.
     * @param origin   The structure origin.
     * @param current  The current {@link BlockInfo}.
     * @param original The original {@link BlockInfo}.
     * @return The {@code current} block info if the check passes, or {@code null} to skip placement.
     */
    @Override
    public BlockInfo process(World world, Location origin, BlockInfo current, BlockInfo original) {
        if (integrity >= 1.0f) return current;
        return random.nextFloat() <= integrity ? current : null;
    }

    /**
     * Processes a block placement in a world generation context.
     * <p>
     * Performs a random float check against the integrity threshold.
     *
     * @param level    The generation accessor.
     * @param origin   The structure origin.
     * @param current  The current {@link BlockInfo}.
     * @param original The original {@link BlockInfo}.
     * @return The {@code current} block info if the check passes, or {@code null} to skip placement.
     */
    @Override
    public BlockInfo process(WorldGenAccess level, Location origin, BlockInfo current, BlockInfo original) {
        if (integrity >= 1.0f) return current;
        return random.nextFloat() <= integrity ? current : null;
    }

    /**
     * Retrieves the specific type definition for this processor.
     *
     * @return The {@link StructureProcessorType} associated with {@link IntegrityProcessor}.
     */
    @Override
    public StructureProcessorType<?> getType() {
        return TYPE;
    }
}