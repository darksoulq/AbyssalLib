package com.github.darksoulq.abyssallib.world.gen.placement.modifier;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementContext;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementModifier;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementModifierType;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

public class SurfaceWaterDepthFilter extends PlacementModifier {
    public static final Codec<SurfaceWaterDepthFilter> CODEC = new Codec<>() {
        @Override
        public <D> SurfaceWaterDepthFilter decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
            int max = Codecs.INT.decode(ops, map.get(ops.createString("max_water_depth")));
            return new SurfaceWaterDepthFilter(max);
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, SurfaceWaterDepthFilter value) throws CodecException {
            Map<D, D> map = Collections.singletonMap(
                ops.createString("max_water_depth"),
                Codecs.INT.encode(ops, value.maxWaterDepth)
            );
            return ops.createMap(map);
        }
    };

    public static final PlacementModifierType<SurfaceWaterDepthFilter> TYPE = () -> CODEC;

    private final int maxWaterDepth;

    public SurfaceWaterDepthFilter(int maxWaterDepth) {
        this.maxWaterDepth = maxWaterDepth;
    }

    @Override
    public Stream<Vector> getPositions(PlacementContext context, Stream<Vector> positions) {
        return positions.filter(pos -> {
            int x = pos.getBlockX();
            int z = pos.getBlockZ();
            int y = pos.getBlockY();

            for (int i = 0; i <= maxWaterDepth; i++) {
                Material mat = context.level().getType(x, y - i, z);
                if (mat == Material.AIR) return false;
                if (mat != Material.WATER) return true;
            }
            return false;
        });
    }

    @Override
    public PlacementModifierType<?> getType() {
        return TYPE;
    }
}