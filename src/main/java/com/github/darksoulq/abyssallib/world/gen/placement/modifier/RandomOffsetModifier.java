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
 * A placement modifier that slightly shifts the incoming position randomly
 * along all three axes based on the defined spread values.
 * <p>
 * This is commonly used in conjunction with the InSquareModifier to add vertical
 * or localized horizontal scattering to features.
 */
public class RandomOffsetModifier extends PlacementModifier {

    /**
     * The codec used for serializing and deserializing the random offset modifier.
     */
    public static final Codec<RandomOffsetModifier> CODEC = new Codec<>() {

        /**
         * Decodes the modifier from a serialized map.
         *
         * @param ops   The dynamic operations logic.
         * @param input The serialized input.
         * @param <D>   The data format type.
         * @return A new instance of the random offset modifier.
         * @throws CodecException If fields are missing.
         */
        @Override
        public <D> RandomOffsetModifier decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
            int xzSpread = Codecs.INT.decode(ops, map.get(ops.createString("xz_spread")));
            int ySpread = Codecs.INT.decode(ops, map.get(ops.createString("y_spread")));
            return new RandomOffsetModifier(xzSpread, ySpread);
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

    /** The maximum horizontal distance (positive or negative) to offset the position. */
    private final int xzSpread;
    
    /** The maximum vertical distance (positive or negative) to offset the position. */
    private final int ySpread;

    /**
     * Constructs a new RandomOffsetModifier.
     *
     * @param xzSpread The bounds for X and Z coordinate randomization.
     * @param ySpread  The bounds for Y coordinate randomization.
     */
    public RandomOffsetModifier(int xzSpread, int ySpread) {
        this.xzSpread = Math.max(0, xzSpread);
        this.ySpread = Math.max(0, ySpread);
    }

    /**
     * Shifts each incoming position by a random amount within the configured spread bounds.
     *
     * @param context   The current placement context.
     * @param positions The incoming stream of potential placement vectors.
     * @return A stream of vectors shifted by the random offsets.
     */
    @Override
    public Stream<Vector> getPositions(PlacementContext context, Stream<Vector> positions) {
        return positions.map(pos -> {
            int dx = xzSpread > 0 ? context.random().nextInt(xzSpread * 2 + 1) - xzSpread : 0;
            int dy = ySpread > 0 ? context.random().nextInt(ySpread * 2 + 1) - ySpread : 0;
            int dz = xzSpread > 0 ? context.random().nextInt(xzSpread * 2 + 1) - xzSpread : 0;
            return new Vector(pos.getBlockX() + dx, pos.getBlockY() + dy, pos.getBlockZ() + dz);
        });
    }

    /**
     * Retrieves the specific type definition for this modifier.
     *
     * @return The placement modifier type associated with this random offset modifier.
     */
    @Override
    public PlacementModifierType<?> getType() {
        return TYPE;
    }
}