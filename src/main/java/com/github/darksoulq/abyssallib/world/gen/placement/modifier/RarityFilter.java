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
 * A placement modifier that filters out entire placement streams based on a random chance.
 * <p>
 * This modifier provides a simple 1-in-N chance for the placement logic to continue.
 * If the random check fails, the entire stream of positions is discarded, effectively
 * canceling the generation of the feature for that specific placement pass.
 */
public class RarityFilter extends PlacementModifier {

    /**
     * The codec used for serializing and deserializing the rarity filter.
     * <p>
     * It maps the "chance" integer field, representing the 'N' in a 1-in-N chance.
     */
    public static final Codec<RarityFilter> CODEC = new Codec<>() {
        /**
         * Decodes a RarityFilter instance from the provided serialized data.
         *
         * @param ops   The {@link DynamicOps} instance defining the data format.
         * @param input The serialized input data.
         * @param <D>   The type of the data being processed.
         * @return A new instance of {@link RarityFilter}.
         * @throws CodecException If the "chance" field is missing or invalid.
         */
        @Override
        public <D> RarityFilter decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
            int chance = Codecs.INT.decode(ops, map.get(ops.createString("chance")));
            return new RarityFilter(chance);
        }

        /**
         * Encodes the RarityFilter instance into a serialized format.
         *
         * @param ops   The {@link DynamicOps} instance defining the data format.
         * @param value The rarity filter instance to encode.
         * @param <D>   The type of the data being processed.
         * @return A map representing the encoded chance value.
         * @throws CodecException If the encoding process fails.
         */
        @Override
        public <D> D encode(DynamicOps<D> ops, RarityFilter value) throws CodecException {
            Map<D, D> map = new HashMap<>();
            map.put(ops.createString("chance"), Codecs.INT.encode(ops, value.chance));
            return ops.createMap(map);
        }
    };

    /**
     * The registered type definition for the rarity placement modifier.
     */
    public static final PlacementModifierType<RarityFilter> TYPE = () -> CODEC;

    /** The inverse probability of placement (1-in-chance). */
    private final int chance;

    /**
     * Constructs a new RarityFilter.
     *
     * @param chance The probability denominator. A value of 10 results in a 10% chance.
     */
    public RarityFilter(int chance) {
        this.chance = chance;
    }

    /**
     * Determines whether to allow the position stream to pass based on random chance.
     * <p>
     * This method uses the random source from the {@link PlacementContext}. If the
     * random check passes, the input stream is returned as-is. If it fails, an
     * empty stream is returned, stopping the placement pipeline for this feature.
     * </p>
     *
     * @param context   The {@link PlacementContext} for the current chunk.
     * @param positions The incoming {@link Stream} of potential positions.
     * @return The original {@link Stream} if successful, or an empty {@link Stream} if not.
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
     * @return The {@link PlacementModifierType} associated with {@link RarityFilter}.
     */
    @Override
    public PlacementModifierType<?> getType() {
        return TYPE;
    }
}