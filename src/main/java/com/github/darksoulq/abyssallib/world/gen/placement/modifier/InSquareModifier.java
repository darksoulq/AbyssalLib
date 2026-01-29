package com.github.darksoulq.abyssallib.world.gen.placement.modifier;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementContext;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementModifier;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementModifierType;
import org.bukkit.util.Vector;

import java.util.Collections;
import java.util.stream.Stream;

public class InSquareModifier extends PlacementModifier {
    private static final InSquareModifier INSTANCE = new InSquareModifier();
    public static final Codec<InSquareModifier> CODEC = new Codec<>() {
        @Override
        public <D> InSquareModifier decode(DynamicOps<D> ops, D input) { return INSTANCE; }
        @Override
        public <D> D encode(DynamicOps<D> ops, InSquareModifier value) { return ops.createMap(Collections.emptyMap()); }
    };

    public static final PlacementModifierType<InSquareModifier> TYPE = () -> CODEC;

    public static InSquareModifier instance() { return INSTANCE; }

    @Override
    public Stream<Vector> getPositions(PlacementContext context, Stream<Vector> positions) {
        return positions.map(pos -> {
            int x = context.random().nextInt(16) + pos.getBlockX();
            int z = context.random().nextInt(16) + pos.getBlockZ();
            return new Vector(x, pos.getY(), z);
        });
    }

    @Override
    public PlacementModifierType<?> getType() {
        return TYPE;
    }
}