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

public class CountModifier extends PlacementModifier {
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

    public static final PlacementModifierType<CountModifier> TYPE = () -> CODEC;

    private final int count;

    public CountModifier(int count) {
        this.count = count;
    }

    @Override
    public Stream<Vector> getPositions(PlacementContext context, Stream<Vector> positions) {
        return positions.flatMap(pos -> IntStream.range(0, count).mapToObj(i -> pos));
    }

    @Override
    public PlacementModifierType<?> getType() {
        return TYPE;
    }
}