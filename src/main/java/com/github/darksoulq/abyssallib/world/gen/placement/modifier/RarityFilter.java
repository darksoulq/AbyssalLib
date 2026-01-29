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

public class RarityFilter extends PlacementModifier {
    public static final Codec<RarityFilter> CODEC = new Codec<>() {
        @Override
        public <D> RarityFilter decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
            int chance = Codecs.INT.decode(ops, map.get(ops.createString("chance")));
            return new RarityFilter(chance);
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, RarityFilter value) throws CodecException {
            Map<D, D> map = new HashMap<>();
            map.put(ops.createString("chance"), Codecs.INT.encode(ops, value.chance));
            return ops.createMap(map);
        }
    };
    public static final PlacementModifierType<RarityFilter> TYPE = () -> CODEC;

    private final int chance;

    public RarityFilter(int chance) {
        this.chance = chance;
    }

    @Override
    public Stream<Vector> getPositions(PlacementContext context, Stream<Vector> positions) {
        if (context.random().nextInt(chance) == 0) {
            return positions;
        }
        return Stream.empty();
    }

    @Override
    public PlacementModifierType<?> getType() {
        return TYPE;
    }
}