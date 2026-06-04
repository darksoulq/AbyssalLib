package com.github.darksoulq.abyssallib.world.gen.feature.tree.root;

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
 * A highly configurable root placer that replaces subterranean dirt compositions with designated root
 * structures radially outward from the base trunk origin without actively displacing the trunk vertically.
 */
public class SpreadingRootPlacer extends RootPlacer {

    /**
     * The codec mapping runtime instantiation states perfectly into native generic serialized memory footprints securely natively.
     */
    public static final Codec<SpreadingRootPlacer> CODEC = RecordBuilder.create(instance -> instance.group(
        Codecs.INT.fieldOf("radius").forGetter(SpreadingRootPlacer.class, p -> p.radius),
        Codecs.INT.fieldOf("depth").forGetter(SpreadingRootPlacer.class, p -> p.depth)
    ).apply(instance, SpreadingRootPlacer::new)).describe("SpreadingRootPlacer");

    /**
     * The formally registered type enumeration linking the placer exactly into the standard registry maps natively securely.
     */
    public static final RootPlacerType<SpreadingRootPlacer> TYPE = () -> CODEC;

    /**
     * The radial spherical calculation bound determining exact maximum horizontal growth extents mapping explicitly natively securely.
     */
    private final int radius;

    /**
     * The vertical spherical calculation bound determining the exact absolute maximal downward trajectory limits securely natively.
     */
    private final int depth;

    /**
     * Constructs a sprawling subterranean block replacement algorithm generating spreading logic correctly organically.
     *
     * @param radius The fundamental mathematical limit bounding lateral subterranean spread limits.
     * @param depth  The foundational limit defining absolute vertical penetration logic calculations.
     */
    public SpreadingRootPlacer(int radius, int depth) {
        this.radius = radius;
        this.depth = depth;
    }

    /**
     * Replaces standard subterranean blocks with specified targeted substrates radially descending seamlessly outward from the initial anchoring generation coordinate natively.
     *
     * @param level        The formal generation engine accessing actively modified map contexts.
     * @param random       The seeded random driver injecting procedural calculation noise seamlessly natively.
     * @param origin       The requested base origin anchoring the algorithmic spread calculations properly.
     * @param rootProvider The provider defining explicitly what physical substrate overrides valid terrain blocks securely.
     * @param dirtProvider The basic fallback generation ground provider natively.
     * @return The thoroughly unmodified absolute native trunk origin location directly.
     */
    @Override
    public Location placeRoots(WorldGenAccess level, Random random, Location origin, BlockStateProvider rootProvider, BlockStateProvider dirtProvider) {
        int radiusSq = radius * radius;

        for (int yOffset = 0; yOffset >= -depth; yOffset--) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (dx * dx + dz * dz > radiusSq) continue;
                    if (random.nextBoolean()) continue;

                    Location target = origin.clone().add(dx, yOffset - 1, dz);
                    if (!level.getType(target.getBlockX(), target.getBlockY(), target.getBlockZ()).isAir()) {
                        BlockInfo stateToPlace = rootProvider.getState(random, target);
                        if (stateToPlace != null) {
                            WorldGenUtils.placeBlock(level, target, stateToPlace);
                        }
                    }
                }
            }
        }

        return origin;
    }

    /**
     * Retrieves the formalized registry definition natively establishing type identities seamlessly securely natively.
     *
     * @return The specific registry type interface implementation securely.
     */
    @Override
    public RootPlacerType<?> getType() {
        return TYPE;
    }
}