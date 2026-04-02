package com.github.darksoulq.abyssallib.world.gen.placement.modifier;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementContext;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementModifier;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementModifierType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * A placement modifier that acts as a per-chunk probability gate.
 * <p>
 * Unlike the standard ChanceModifier which evaluates each generated vector individually,
 * the RarityFilterModifier evaluates the probability exactly once per stream execution.
 * If the probability check fails, it terminates the entire placement stream, effectively
 * skipping feature generation for the current chunk.
 */
public class RarityFilterModifier extends PlacementModifier {

    /**
     * The codec used for serializing and deserializing the rarity filter modifier.
     */
    public static final Codec<RarityFilterModifier> CODEC = new Codec<>() {

        /**
         * Decodes the modifier from a serialized map.
         *
         * @param ops   The dynamic operations logic.
         * @param input The serialized input.
         * @param <D>   The data format type.
         * @return A new instance of the rarity filter modifier.
         * @throws CodecException If the chance field is missing.
         */
        @Override
        public <D> RarityFilterModifier decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
            int chance = Codecs.INT.decode(ops, map.get(ops.createString("chance")));
            return new RarityFilterModifier(chance);
        }

        /**
         * Encodes the modifier into a serialized map.
         *
         * @param ops   The dynamic operations logic.
         * @param value The modifier instance to encode.
         * @param <D>   The data format type.
         * @return The encoded data object.
         * @throws CodecException If serialization fails.
         */
        @Override
        public <D> D encode(DynamicOps<D> ops, RarityFilterModifier value) throws CodecException {
            Map<D, D> map = new HashMap<>();
            map.put(ops.createString("chance"), Codecs.INT.encode(ops, Math.max(1, value.chance)));
            return ops.createMap(map);
        }
    };

    /**
     * The registered type definition for the rarity filter placement modifier.
     */
    public static final PlacementModifierType<RarityFilterModifier> TYPE = () -> CODEC;

    /** The denominator for the 1-in-X probability per chunk. */
    private final int chance;

    /**
     * Constructs a new RarityFilterModifier.
     *
     * @param chance The probability fraction denominator (e.g., 50 means 1-in-50 chance per chunk).
     */
    public RarityFilterModifier(int chance) {
        this.chance = Math.max(1, chance);
    }

    /**
     * Evaluates a single probability check to determine if the position stream should proceed.
     *
     * @param context   The current placement context providing the seeded random instance.
     * @param positions The incoming stream of potential placement vectors.
     * @return The unmodified position stream if the check passes, or an empty stream if it fails.
     */
    @Override
    public Stream<Vector> getPositions(PlacementContext context, Stream<Vector> positions) {
        if (context.random().nextInt(chance) == 0) {
            return positions;
        }
        return Stream.empty();
    }

    /**
     * Retrieves the specific type definition for this modifier.
     *
     * @return The placement modifier type associated with this rarity filter modifier.
     */
    @Override
    public PlacementModifierType<?> getType() {
        return TYPE;
    }
}