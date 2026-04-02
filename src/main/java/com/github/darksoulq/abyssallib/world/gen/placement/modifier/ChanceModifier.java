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
 * A placement modifier that filters positions based on a random probability.
 * <p>
 * This modifier passes the incoming vector only if a random integer bounded
 * by the configured chance equals zero (e.g., a chance of 10 means a 1-in-10
 * or 10% probability of placement).
 */
public class ChanceModifier extends PlacementModifier {

    /**
     * The codec used for serializing and deserializing the chance modifier.
     */
    public static final Codec<ChanceModifier> CODEC = new Codec<>() {

        /**
         * Decodes the modifier from a serialized map.
         *
         * @param ops   The dynamic operations logic.
         * @param input The serialized input.
         * @param <D>   The data format type.
         * @return A new instance of the chance modifier.
         * @throws CodecException If the chance field is missing.
         */
        @Override
        public <D> ChanceModifier decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
            int chance = Codecs.INT.decode(ops, map.get(ops.createString("chance")));
            return new ChanceModifier(chance);
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
        public <D> D encode(DynamicOps<D> ops, ChanceModifier value) throws CodecException {
            Map<D, D> map = new HashMap<>();
            map.put(ops.createString("chance"), Codecs.INT.encode(ops, Math.max(1, value.chance)));
            return ops.createMap(map);
        }
    };

    /**
     * The registered type definition for the chance placement modifier.
     */
    public static final PlacementModifierType<ChanceModifier> TYPE = () -> CODEC;

    /** The denominator for the 1-in-X probability. Must be >= 1. */
    private final int chance;

    /**
     * Constructs a new ChanceModifier.
     *
     * @param chance The probability fraction denominator (e.g., 5 means 20% chance).
     */
    public ChanceModifier(int chance) {
        this.chance = Math.max(1, chance);
    }

    /**
     * Randomly drops incoming vectors based on the configured chance.
     *
     * @param context   The current placement context providing the seeded random instance.
     * @param positions The incoming stream of potential placement vectors.
     * @return A filtered stream containing only the positions that passed the probability check.
     */
    @Override
    public Stream<Vector> getPositions(PlacementContext context, Stream<Vector> positions) {
        return positions.filter(pos -> context.random().nextInt(chance) == 0);
    }

    /**
     * Retrieves the specific type definition for this modifier.
     *
     * @return The placement modifier type associated with this chance modifier.
     */
    @Override
    public PlacementModifierType<?> getType() {
        return TYPE;
    }
}