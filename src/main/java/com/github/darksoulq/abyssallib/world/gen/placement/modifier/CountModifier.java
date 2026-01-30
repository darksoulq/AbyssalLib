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
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * A placement modifier that duplicates positions to increase feature density.
 * <p>
 * This modifier takes each {@link Vector} in the input stream and outputs it
 * {@code count} times. It is typically the first modifier in a placement
 * list, defining how many times the generation logic should be attempted
 * within a single chunk.
 */
public class CountModifier extends PlacementModifier {

    /**
     * The codec used for serializing and deserializing the count modifier.
     * <p>
     * It maps the "count" integer field, which defines the duplication factor.
     */
    public static final Codec<CountModifier> CODEC = new Codec<>() {
        /**
         * Decodes a CountModifier instance from the provided serialized data.
         *
         * @param ops   The {@link DynamicOps} instance defining the data format.
         * @param input The serialized input data.
         * @param <D>   The type of the data being processed.
         * @return A new instance of {@link CountModifier}.
         * @throws CodecException If the "count" field is missing or invalid.
         */
        @Override
        public <D> CountModifier decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
            int count = Codecs.INT.decode(ops, map.get(ops.createString("count")));
            return new CountModifier(count);
        }

        /**
         * Encodes the CountModifier instance into a serialized format.
         *
         * @param ops   The {@link DynamicOps} instance defining the data format.
         * @param value The modifier instance to encode.
         * @param <D>   The type of the data being processed.
         * @return A map representing the encoded count value.
         * @throws CodecException If the encoding process fails.
         */
        @Override
        public <D> D encode(DynamicOps<D> ops, CountModifier value) throws CodecException {
            Map<D, D> map = new HashMap<>();
            map.put(ops.createString("count"), Codecs.INT.encode(ops, value.count));
            return ops.createMap(map);
        }
    };

    /**
     * The registered type definition for the count placement modifier.
     */
    public static final PlacementModifierType<CountModifier> TYPE = () -> CODEC;

    /** The number of times to duplicate each input position. */
    private final int count;

    /**
     * Constructs a new CountModifier.
     *
     * @param count The number of placement attempts to generate per input position.
     */
    public CountModifier(int count) {
        this.count = count;
    }

    /**
     * Multiplies the input positions by the configured count.
     * <p>
     * This uses a {@code flatMap} to expand the stream, repeating the same
     * coordinate vector. Subsequent modifiers like {@code InSquareModifier}
     * or {@code HeightModifier} are then responsible for spreading these
     * duplicated points across the chunk.
     * </p>
     *
     * @param context   The {@link PlacementContext} for the current chunk.
     * @param positions The incoming {@link Stream} of positions.
     * @return An expanded {@link Stream} of identical positions.
     */
    @Override
    public Stream<Vector> getPositions(PlacementContext context, Stream<Vector> positions) {
        return positions.flatMap(pos -> IntStream.range(0, count).mapToObj(i -> pos));
    }

    /**
     * Retrieves the specific type definition for this modifier.
     *
     * @return The {@link PlacementModifierType} associated with {@link CountModifier}.
     */
    @Override
    public PlacementModifierType<?> getType() {
        return TYPE;
    }
}