package com.github.darksoulq.abyssallib.world.structure.processor.impl;

import com.github.darksoulq.abyssallib.common.serialization.BlockInfo;
import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.RecordBuilder;
import com.github.darksoulq.abyssallib.world.gen.WorldGenAccess;
import com.github.darksoulq.abyssallib.world.structure.processor.StructureProcessor;
import com.github.darksoulq.abyssallib.world.structure.processor.StructureProcessorType;
import org.bukkit.Location;
import org.bukkit.World;

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
    public static final Codec<IntegrityProcessor> CODEC = RecordBuilder.create(instance -> instance.group(
        Codecs.FLOAT.optionalFieldOf("integrity", 1.0f).forGetter(IntegrityProcessor.class, p -> p.integrity)
    ).apply(instance, IntegrityProcessor::new)).describe("IntegrityProcessor");

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