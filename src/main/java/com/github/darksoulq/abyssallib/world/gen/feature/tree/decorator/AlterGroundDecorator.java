package com.github.darksoulq.abyssallib.world.gen.feature.tree.decorator;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.world.gen.WorldGenAccess;
import com.github.darksoulq.abyssallib.world.gen.state.provider.BlockStateProvider;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * A tree decorator that alters the ground surface directly beneath the generated tree.
 */
public class AlterGroundDecorator extends TreeDecorator {

    /**
     * The codec used for serializing and deserializing the alter ground decorator.
     */
    public static final Codec<AlterGroundDecorator> CODEC = new Codec<>() {

        /**
         * Decodes the decorator from a serialized map.
         *
         * @param ops   The dynamic operations logic.
         * @param input The serialized input.
         * @param <D>   The data format type.
         * @return A new instance of the alter ground decorator.
         * @throws CodecException If the provider field is missing.
         */
        @Override
        public <D> AlterGroundDecorator decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
            BlockStateProvider provider = BlockStateProvider.CODEC.decode(ops, map.get(ops.createString("provider")));
            return new AlterGroundDecorator(provider);
        }

        /**
         * Encodes the decorator into a serialized map.
         *
         * @param ops   The dynamic operations logic.
         * @param value The decorator instance to encode.
         * @param <D>   The data format type.
         * @return The encoded data object.
         * @throws CodecException If serialization fails.
         */
        @Override
        public <D> D encode(DynamicOps<D> ops, AlterGroundDecorator value) throws CodecException {
            Map<D, D> map = new HashMap<>();
            map.put(ops.createString("provider"), BlockStateProvider.CODEC.encode(ops, value.provider));
            return ops.createMap(map);
        }
    };

    /**
     * The registered type definition for the alter ground tree decorator.
     */
    public static final TreeDecoratorType<AlterGroundDecorator> TYPE = () -> CODEC;

    /** The provider supplying the altered ground blocks. */
    private final BlockStateProvider provider;

    /**
     * Constructs a new AlterGroundDecorator.
     *
     * @param provider The block state provider for the new ground.
     */
    public AlterGroundDecorator(BlockStateProvider provider) {
        this.provider = provider;
    }

    /**
     * Evaluates the lowest log coordinates and converts a radius of ground blocks
     * into the specified provider state.
     *
     * @param level  The world generation accessor.
     * @param random The deterministic random source.
     * @param logs   The set of absolute coordinates where trunk blocks were placed.
     * @param leaves The set of absolute coordinates where foliage blocks were placed.
     */
    @Override
    public void decorate(WorldGenAccess level, Random random, Set<Location> logs, Set<Location> leaves) {
        if (logs.isEmpty()) return;

        Location lowestLog = logs.iterator().next();
        for (Location log : logs) {
            if (log.getBlockY() < lowestLog.getBlockY()) {
                lowestLog = log;
            }
        }

        int radius = 2;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (Math.abs(dx) == radius && Math.abs(dz) == radius) {
                    if (random.nextBoolean()) continue;
                }

                Location target = lowestLog.clone().add(dx, -1, dz);
                Material mat = level.getType(target.getBlockX(), target.getBlockY(), target.getBlockZ());

                if (mat.isSolid()) {
                    com.github.darksoulq.abyssallib.world.gen.internal.WorldGenUtils.placeBlock(level, target, provider.getState(random, target));
                }
            }
        }
    }

    /**
     * Retrieves the specific type definition for this decorator.
     *
     * @return The tree decorator type associated with this instance.
     */
    @Override
    public TreeDecoratorType<?> getType() {
        return TYPE;
    }
}