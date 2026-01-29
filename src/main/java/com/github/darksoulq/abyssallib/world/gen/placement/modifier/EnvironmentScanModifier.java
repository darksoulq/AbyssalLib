package com.github.darksoulq.abyssallib.world.gen.placement.modifier;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementContext;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementModifier;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementModifierType;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class EnvironmentScanModifier extends PlacementModifier {
    public static final Codec<EnvironmentScanModifier> CODEC = new Codec<>() {
        @Override
        public <D> EnvironmentScanModifier decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
            int max = Codecs.INT.decode(ops, map.get(ops.createString("max_steps")));
            boolean up = Codecs.BOOLEAN.decode(ops, map.get(ops.createString("up")));
            return new EnvironmentScanModifier(max, up);
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, EnvironmentScanModifier value) throws CodecException {
            Map<D, D> map = new HashMap<>();
            map.put(ops.createString("max_steps"), Codecs.INT.encode(ops, value.maxSteps));
            map.put(ops.createString("up"), Codecs.BOOLEAN.encode(ops, value.up));
            return ops.createMap(map);
        }
    };

    public static final PlacementModifierType<EnvironmentScanModifier> TYPE = () -> CODEC;

    private final int maxSteps;
    private final boolean up;

    public EnvironmentScanModifier(int maxSteps, boolean up) {
        this.maxSteps = maxSteps;
        this.up = up;
    }

    @Override
    public Stream<Vector> getPositions(PlacementContext context, Stream<Vector> positions) {
        return positions.map(pos -> {
            int x = pos.getBlockX();
            int y = pos.getBlockY();
            int z = pos.getBlockZ();
            int step = up ? 1 : -1;

            for (int i = 0; i < maxSteps; i++) {
                int checkY = y + (i * step);
                if (checkY < context.getMinBuildHeight() || checkY >= context.getHeight()) break;
                
                Material m = context.level().getType(x, checkY, z);
                if (m.isSolid()) {
                    return new Vector(x, checkY + (up ? 1 : 0), z);
                }
            }
            return pos;
        });
    }

    @Override
    public PlacementModifierType<?> getType() {
        return TYPE;
    }
}