package com.github.darksoulq.abyssallib.world.gen.placement.modifier;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementContext;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementModifier;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementModifierType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * A placement modifier that completely overrides the procedural generation stream,
 * replacing it with a fixed, hardcoded list of specific coordinates.
 * <p>
 * This is highly useful for generating specific lore structures, quest elements,
 * or testing features at exact, known locations without relying on random seeds.
 */
public class FixedPlacementModifier extends PlacementModifier {

    /**
     * The codec used for serializing and deserializing the fixed placement modifier.
     */
    public static final Codec<FixedPlacementModifier> CODEC = new Codec<>() {

        /**
         * Decodes the modifier from a serialized map.
         *
         * @param ops   The dynamic operations logic.
         * @param input The serialized input.
         * @param <D>   The data format type.
         * @return A new instance of the fixed placement modifier.
         * @throws CodecException If the positions list is missing.
         */
        @Override
        public <D> FixedPlacementModifier decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
            List<Vector> positions = Codecs.VECTOR_I.list().decode(ops, map.get(ops.createString("positions")));
            return new FixedPlacementModifier(positions);
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
        public <D> D encode(DynamicOps<D> ops, FixedPlacementModifier value) throws CodecException {
            Map<D, D> map = new HashMap<>();
            map.put(ops.createString("positions"), Codecs.VECTOR_I.list().encode(ops, value.positions));
            return ops.createMap(map);
        }
    };

    /**
     * The registered type definition for the fixed placement modifier.
     */
    public static final PlacementModifierType<FixedPlacementModifier> TYPE = () -> CODEC;

    /** The static list of absolute world coordinates. */
    private final List<Vector> positions;

    /**
     * Constructs a new FixedPlacementModifier.
     *
     * @param positions The exact list of vectors to generate at.
     */
    public FixedPlacementModifier(List<Vector> positions) {
        this.positions = positions;
    }

    /**
     * Discards the incoming procedurally generated positions and returns the fixed list.
     *
     * @param context   The current placement context.
     * @param positions The incoming stream (which is discarded).
     * @return A stream containing only the explicitly configured vectors.
     */
    @Override
    public Stream<Vector> getPositions(PlacementContext context, Stream<Vector> positions) {
        return this.positions.stream();
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