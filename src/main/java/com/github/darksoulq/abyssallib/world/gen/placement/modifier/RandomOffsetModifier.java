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
 * A placement modifier that shifts each position by a random offset in three dimensions.
 * <p>
 * This modifier applies a random displacement within a box defined by the XZ and Y spread
 * values. It is useful for adding organic variation to feature placement or for
 * scattering sub-features around a central point.
 */
public class RandomOffsetModifier extends PlacementModifier {

    /**
     * The codec used for serializing and deserializing the random offset modifier.
     * <p>
     * Requires "xz_spread" and "y_spread" integer fields to define the jitter radius.
     */
    public static final Codec<RandomOffsetModifier> CODEC = new Codec<>() {
        /**
         * Decodes a RandomOffsetModifier from the provided serialized data.
         *
         * @param ops   The dynamic operations logic.
         * @param input The serialized input data.
         * @param <D>   The data format type.
         * @return A new instance of {@link RandomOffsetModifier}.
         * @throws CodecException If the spread fields are missing or invalid.
         */
        @Override
        public <D> RandomOffsetModifier decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
            int x = Codecs.INT.decode(ops, map.get(ops.createString("xz_spread")));
            int y = Codecs.INT.decode(ops, map.get(ops.createString("y_spread")));
            return new RandomOffsetModifier(x, y);
        }

        /**
         * Encodes the random offset modifier into a serialized format.
         *
         * @param ops   The dynamic operations logic.
         * @param value The modifier instance to encode.
         * @param <D>   The data format type.
         * @return A map containing the xz_spread and y_spread values.
         * @throws CodecException If serialization fails.
         */
        @Override
        public <D> D encode(DynamicOps<D> ops, RandomOffsetModifier value) throws CodecException {
            Map<D, D> map = new HashMap<>();
            map.put(ops.createString("xz_spread"), Codecs.INT.encode(ops, value.xzSpread));
            map.put(ops.createString("y_spread"), Codecs.INT.encode(ops, value.ySpread));
            return ops.createMap(map);
        }
    };

    /**
     * The registered type definition for the random offset placement modifier.
     */
    public static final PlacementModifierType<RandomOffsetModifier> TYPE = () -> CODEC;

    /** The maximum horizontal displacement in the X and Z directions. */
    private final int xzSpread;

    /** The maximum vertical displacement in the Y direction. */
    private final int ySpread;

    /**
     * Constructs a new RandomOffsetModifier.
     *
     * @param xzSpread The radius for horizontal jitter.
     * @param ySpread  The radius for vertical jitter.
     */
    public RandomOffsetModifier(int xzSpread, int ySpread) {
        this.xzSpread = xzSpread;
        this.ySpread = ySpread;
    }

    /**
     * Maps each position in the stream to a new position with a random 3D offset.
     * <p>
     * For every incoming vector, a random displacement is calculated in the range
     * [-spread, +spread] for each axis. A new vector is returned with these offsets
     * added to the original coordinates.
     *
     * @param context   The current {@link PlacementContext}.
     * @param positions The incoming stream of potential placement vectors.
     * @return A stream of vectors shifted by random 3D offsets.
     */
    @Override
    public Stream<Vector> getPositions(PlacementContext context, Stream<Vector> positions) {
        return positions.map(pos -> {
            int dx = context.random().nextInt(xzSpread * 2 + 1) - xzSpread;
            int dy = context.random().nextInt(ySpread * 2 + 1) - ySpread;
            int dz = context.random().nextInt(xzSpread * 2 + 1) - xzSpread;
            return pos.clone().add(new Vector(dx, dy, dz));
        });
    }

    /**
     * Retrieves the specific type definition for this modifier.
     *
     * @return The {@link PlacementModifierType} associated with {@link RandomOffsetModifier}.
     */
    @Override
    public PlacementModifierType<?> getType() {
        return TYPE;
    }
}