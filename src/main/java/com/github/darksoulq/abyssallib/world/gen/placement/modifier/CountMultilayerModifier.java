package com.github.darksoulq.abyssallib.world.gen.placement.modifier;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.world.gen.internal.WorldGenUtils;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementContext;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementModifier;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementModifierType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class CountMultilayerModifier extends PlacementModifier {
    public static final Codec<CountMultilayerModifier> CODEC = new Codec<>() {
        @Override
        public <D> CountMultilayerModifier decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
            int count = Codecs.INT.decode(ops, map.get(ops.createString("count")));
            List<String> valid = new ArrayList<>();
            if (map.containsKey(ops.createString("valid_blocks"))) {
                valid = Codecs.STRING.list().decode(ops, map.get(ops.createString("valid_blocks")));
            }
            return new CountMultilayerModifier(count, valid);
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, CountMultilayerModifier value) throws CodecException {
            Map<D, D> map = new HashMap<>();
            map.put(ops.createString("count"), Codecs.INT.encode(ops, value.count));
            map.put(ops.createString("valid_blocks"), Codecs.STRING.list().encode(ops, value.validBlocks));
            return ops.createMap(map);
        }
    };

    public static final PlacementModifierType<CountMultilayerModifier> TYPE = () -> CODEC;

    private final int count;
    private final List<String> validBlocks;

    public CountMultilayerModifier(int count, List<String> validBlocks) {
        this.count = count;
        this.validBlocks = validBlocks;
    }

    @Override
    public Stream<Vector> getPositions(PlacementContext context, Stream<Vector> positions) {
        return positions.flatMap(pos -> {
            List<Vector> valid = new ArrayList<>();
            int x = pos.getBlockX();
            int z = pos.getBlockZ();

            for (int y = context.getMinBuildHeight() + 1; y < context.getHeight() - 1; y++) {
                Location loc = new Location(context.level().getWorld(), x, y, z);

                if (context.level().getType(x, y, z) == Material.AIR || context.level().getType(x, y, z) == Material.CAVE_AIR) {
                    Location below = loc.clone().add(0, -1, 0);
                    if (WorldGenUtils.isValidBlock(context.level(), below, validBlocks)) {
                        valid.add(new Vector(x, y, z));
                    }
                }
            }

            if (valid.isEmpty()) return Stream.empty();

            List<Vector> result = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                result.add(valid.get(context.random().nextInt(valid.size())));
            }
            return result.stream();
        });
    }

    @Override
    public PlacementModifierType<?> getType() {
        return TYPE;
    }
}