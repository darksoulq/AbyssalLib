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

public class RandomOffsetModifier extends PlacementModifier {
    public static final Codec<RandomOffsetModifier> CODEC = new Codec<>() {
        @Override
        public <D> RandomOffsetModifier decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
            int x = Codecs.INT.decode(ops, map.get(ops.createString("xz_spread")));
            int y = Codecs.INT.decode(ops, map.get(ops.createString("y_spread")));
            return new RandomOffsetModifier(x, y);
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, RandomOffsetModifier value) throws CodecException {
            Map<D, D> map = new HashMap<>();
            map.put(ops.createString("xz_spread"), Codecs.INT.encode(ops, value.xzSpread));
            map.put(ops.createString("y_spread"), Codecs.INT.encode(ops, value.ySpread));
            return ops.createMap(map);
        }
    };

    public static final PlacementModifierType<RandomOffsetModifier> TYPE = () -> CODEC;

    private final int xzSpread;
    private final int ySpread;

    public RandomOffsetModifier(int xzSpread, int ySpread) {
        this.xzSpread = xzSpread;
        this.ySpread = ySpread;
    }

    @Override
    public Stream<Vector> getPositions(PlacementContext context, Stream<Vector> positions) {
        return positions.map(pos -> {
            int dx = context.random().nextInt(xzSpread * 2 + 1) - xzSpread;
            int dy = context.random().nextInt(ySpread * 2 + 1) - ySpread;
            int dz = context.random().nextInt(xzSpread * 2 + 1) - xzSpread;
            return pos.clone().add(new Vector(dx, dy, dz));
        });
    }

    @Override
    public PlacementModifierType<?> getType() {
        return TYPE;
    }
}