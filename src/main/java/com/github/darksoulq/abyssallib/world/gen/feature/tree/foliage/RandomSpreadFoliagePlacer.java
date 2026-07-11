package com.github.darksoulq.abyssallib.world.gen.feature.tree.foliage;

import com.github.darksoulq.abyssallib.common.serialization.BlockInfo;
import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.RecordBuilder;
import com.github.darksoulq.abyssallib.world.gen.WorldGenAccess;
import com.github.darksoulq.abyssallib.world.gen.internal.WorldGenUtils;
import com.github.darksoulq.abyssallib.world.gen.state.provider.BlockStateProvider;
import org.bukkit.Location;

import java.util.Random;

/**
 * A foliage placer that scatters individual leaf blocks randomly within a bounding area.
 * <p>
 * Instead of creating solid blobs, this algorithm attempts a configured number
 * of placements within the defined radius and height. This is highly effective
 * for creating sparse, wispy trees, dead trees covered in cobwebs, or irregular
 * swamp canopies.
 */
public class RandomSpreadFoliagePlacer extends FoliagePlacer {

    /**
     * The codec used for serializing and deserializing the random spread foliage placer.
     */
    public static final Codec<RandomSpreadFoliagePlacer> CODEC = RecordBuilder.create(instance -> instance.group(
        Codecs.INT.fieldOf("height").forGetter(RandomSpreadFoliagePlacer.class, p -> p.height),
        Codecs.INT.fieldOf("attempts").forGetter(RandomSpreadFoliagePlacer.class, p -> p.attempts)
    ).apply(instance, RandomSpreadFoliagePlacer::new)).describe("RandomSpreadFoliagePlacer");

    /**
     * The registered type definition for the random spread foliage placer.
     */
    public static final FoliagePlacerType<RandomSpreadFoliagePlacer> TYPE = () -> CODEC;

    /**
     * The vertical boundary (in layers below the attachment point) where leaves can spawn.
     */
    private final int height;

    /**
     * The number of times the algorithm will attempt to place a leaf block.
     */
    private final int attempts;

    /**
     * Constructs a new RandomSpreadFoliagePlacer.
     *
     * @param height   The maximum depth below the attachment point.
     * @param attempts The total number of blocks to attempt to place in the radius.
     */
    public RandomSpreadFoliagePlacer(int height, int attempts) {
        this.height = height;
        this.attempts = attempts;
    }

    /**
     * Scatters leaf blocks randomly within the horizontal radius and vertical height bounds.
     *
     * @param level           The world generation accessor.
     * @param random          The deterministic random source.
     * @param attachmentPoint The central origin of the scatter operation.
     * @param foliageProvider The block state provider for the leaf material.
     * @param radius          The maximum horizontal boundary on the X and Z axes.
     */
    @Override
    public void placeFoliage(WorldGenAccess level, Random random, Location attachmentPoint, BlockStateProvider foliageProvider, int radius) {
        for (int i = 0; i < attempts; i++) {
            int dx = random.nextInt(radius * 2 + 1) - radius;
            int dy = -random.nextInt(height + 1);
            int dz = random.nextInt(radius * 2 + 1) - radius;

            Location target = attachmentPoint.clone().add(dx, dy, dz);
            if (target.getBlockY() >= level.world().getMaxHeight()) continue;

            if (level.getType(target.getBlockX(), target.getBlockY(), target.getBlockZ()).isAir()) {
                BlockInfo stateToPlace = foliageProvider.getState(random, target);
                if (stateToPlace != null) {
                    WorldGenUtils.placeBlock(level, target, stateToPlace);
                }
            }
        }
    }

    /**
     * Retrieves the specific type definition for this foliage placer.
     *
     * @return The foliage placer type associated with this instance.
     */
    @Override
    public FoliagePlacerType<?> getType() {
        return TYPE;
    }
}