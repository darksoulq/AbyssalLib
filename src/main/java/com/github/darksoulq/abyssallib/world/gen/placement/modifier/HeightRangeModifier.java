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

public class HeightRangeModifier extends PlacementModifier {
    public static final Codec<HeightRangeModifier> CODEC = new Codec<>() {
        @Override
        public <D> HeightRangeModifier decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
            int min = Codecs.INT.decode(ops, map.get(ops.createString("min")));
            int max = Codecs.INT.decode(ops, map.get(ops.createString("max")));
            return new HeightRangeModifier(min, max);
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, HeightRangeModifier value) throws CodecException {
            Map<D, D> map = new HashMap<>();
            map.put(ops.createString("min"), Codecs.INT.encode(ops, value.min));
            map.put(ops.createString("max"), Codecs.INT.encode(ops, value.max));
            return ops.createMap(map);
        }
    };

    public static final PlacementModifierType<HeightRangeModifier> TYPE = () -> CODEC;

    private final int min;
    private final int max;

    public HeightRangeModifier(int min, int max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public Stream<Vector> getPositions(PlacementContext context, Stream<Vector> positions) {
        return positions.map(pos -> {
            int y = context.random().nextInt(max - min + 1) + min;
            return new Vector(pos.getX(), y, pos.getZ());
        });
    }

    @Override
    public PlacementModifierType<?> getType() {
        return TYPE;
    }
}