package com.github.darksoulq.abyssallib.world.gen.placement.modifier;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementContext;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementModifier;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementModifierType;
import org.bukkit.HeightMap;
import org.bukkit.util.Vector;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

public class HeightmapModifier extends PlacementModifier {
    public static final Codec<HeightmapModifier> CODEC = new Codec<>() {
        @Override
        public <D> HeightmapModifier decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
            String type = Codecs.STRING.decode(ops, map.get(ops.createString("heightmap")));
            return new HeightmapModifier(HeightMap.valueOf(type));
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, HeightmapModifier value) throws CodecException {
            Map<D, D> map = Collections.singletonMap(
                ops.createString("heightmap"),
                Codecs.STRING.encode(ops, value.heightMap.name())
            );
            return ops.createMap(map);
        }
    };

    public static final PlacementModifierType<HeightmapModifier> TYPE = () -> CODEC;

    private final HeightMap heightMap;

    public HeightmapModifier(HeightMap heightMap) {
        this.heightMap = heightMap;
    }

    @Override
    public Stream<Vector> getPositions(PlacementContext context, Stream<Vector> positions) {
        return positions.map(pos -> {
            int y = context.level().getWorld().getHighestBlockYAt(pos.getBlockX(), pos.getBlockZ(), heightMap);
            return new Vector(pos.getX(), y, pos.getZ());
        });
    }

    @Override
    public PlacementModifierType<?> getType() {
        return TYPE;
    }
}