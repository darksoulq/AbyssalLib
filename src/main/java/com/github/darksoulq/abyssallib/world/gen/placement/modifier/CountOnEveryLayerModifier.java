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
 * A placement modifier that duplicates the incoming position stream across every
 * single Y-coordinate from the bottom of the world to the top.
 * <p>
 * This is incredibly useful for generating ubiquitous underground features like
 * dense ore veins or cave vines that need to attempt placement at every depth level.
 */
public class CountOnEveryLayerModifier extends PlacementModifier {

    /**
     * The codec used for serializing and deserializing the layer count modifier.
     */
    public static final Codec<CountOnEveryLayerModifier> CODEC = new Codec<>() {

        /**
         * Decodes the modifier from a serialized map.
         *
         * @param ops   The dynamic operations logic.
         * @param input The serialized input.
         * @param <D>   The data format type.
         * @return A new instance of the layer count modifier.
         * @throws CodecException If the count field is missing.
         */
        @Override
        public <D> CountOnEveryLayerModifier decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
            int count = Codecs.INT.decode(ops, map.get(ops.createString("count")));
            return new CountOnEveryLayerModifier(count);
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
        public <D> D encode(DynamicOps<D> ops, CountOnEveryLayerModifier value) throws CodecException {
            Map<D, D> map = new HashMap<>();
            map.put(ops.createString("count"), Codecs.INT.encode(ops, value.count));
            return ops.createMap(map);
        }
    };

    /**
     * The registered type definition for the layer count placement modifier.
     */
    public static final PlacementModifierType<CountOnEveryLayerModifier> TYPE = () -> CODEC;

    /** The number of placement attempts to make per Y-layer. */
    private final int count;

    /**
     * Constructs a new CountOnEveryLayerModifier.
     *
     * @param count The number of attempts per horizontal layer.
     */
    public CountOnEveryLayerModifier(int count) {
        this.count = count;
    }

    /**
     * Maps each incoming horizontal coordinate to every valid vertical Y-coordinate.
     *
     * @param context   The current placement context providing world boundaries.
     * @param positions The incoming stream of potential placement vectors.
     * @return A stream of vectors expanded across the entire vertical axis.
     */
    @Override
    public Stream<Vector> getPositions(PlacementContext context, Stream<Vector> positions) {
        int minY = context.getMinBuildHeight();
        int maxY = context.getHeight();

        return positions.flatMap(pos -> 
            IntStream.range(minY, maxY).boxed().flatMap(y -> 
                IntStream.range(0, count).mapToObj(i -> new Vector(pos.getBlockX(), y, pos.getBlockZ()))
            )
        );
    }

    /**
     * Retrieves the specific type definition for this modifier.
     *
     * @return The placement modifier type associated with this modifier.
     */
    @Override
    public PlacementModifierType<?> getType() {
        return TYPE;
    }
}