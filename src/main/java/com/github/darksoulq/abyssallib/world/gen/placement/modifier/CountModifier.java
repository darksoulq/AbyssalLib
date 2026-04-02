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
 * A placement modifier that duplicates the incoming position stream a specified number of times.
 * This is typically the first modifier in a placement sequence, determining how many
 * attempts are made to place a feature within a chunk.
 */
public class CountModifier extends PlacementModifier {

    /**
     * The codec used for serializing and deserializing the count modifier.
     */
    public static final Codec<CountModifier> CODEC = new Codec<>() {
        @Override
        public <D> CountModifier decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
            int count = Codecs.INT.decode(ops, map.get(ops.createString("count")));
            return new CountModifier(count);
        }

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

    /** The number of times to duplicate the input positions. */
    private final int count;

    /**
     * Constructs a new CountModifier.
     *
     * @param count The number of placement attempts per input position.
     */
    public CountModifier(int count) {
        this.count = count;
    }

    /**
     * Duplicates each position in the input stream by the configured count.
     *
     * @param context   The current placement context.
     * @param positions The incoming stream of potential placement vectors.
     * @return A stream of vectors duplicated by the configured count.
     */
    @Override
    public Stream<Vector> getPositions(PlacementContext context, Stream<Vector> positions) {
        return positions.flatMap(pos -> IntStream.range(0, count).mapToObj(i -> pos.clone()));
    }

    @Override
    public PlacementModifierType<?> getType() {
        return TYPE;
    }
}