package com.github.darksoulq.abyssallib.world.gen.feature.tree.decorator;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.RecordBuilder;
import com.github.darksoulq.abyssallib.world.gen.WorldGenAccess;
import com.github.darksoulq.abyssallib.world.gen.state.provider.BlockStateProvider;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.Random;
import java.util.Set;

/**
 * A tree decorator that alters the ground surface directly beneath the generated tree.
 */
public class AlterGroundDecorator extends TreeDecorator {

    /**
     * The codec used for serializing and deserializing the alter ground decorator.
     */
    public static final Codec<AlterGroundDecorator> CODEC = RecordBuilder.create(instance -> instance.group(
        BlockStateProvider.CODEC.fieldOf("provider").forGetter(AlterGroundDecorator.class, p -> p.provider)
    ).apply(instance, AlterGroundDecorator::new)).describe("AlterGroundDecorator");

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