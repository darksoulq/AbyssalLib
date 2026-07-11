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
 * A standard foliage placer explicitly designed to generate the iconic thick,
 * layered cuboid canopy footprint associated strictly with Dark Oak tree typologies.
 */
public class DarkOakFoliagePlacer extends FoliagePlacer {

    /**
     * The designated codec instance responsible for serializing and deserializing the state variables of this placer.
     */
    public static final Codec<DarkOakFoliagePlacer> CODEC = RecordBuilder.create(instance -> instance.group(
        Codecs.INT.fieldOf("radius").forGetter(DarkOakFoliagePlacer.class, p -> p.radius)
    ).apply(instance, DarkOakFoliagePlacer::new)).describe("DarkOakFoliagePlacer");

    /**
     * The globally registered type definition representing the dark oak foliage placer.
     */
    public static final FoliagePlacerType<DarkOakFoliagePlacer> TYPE = () -> CODEC;

    /**
     * The calculated absolute radial boundary governing the horizontal foliage spread.
     */
    private final int radius;

    /**
     * Constructs a new dark oak foliage placement generator bound by a structural radius parameter.
     *
     * @param radius The fundamental mathematical limit bounding lateral canopy growth computations.
     */
    public DarkOakFoliagePlacer(int radius) {
        this.radius = radius;
    }

    /**
     * Executes the procedural logic shaping thick cubic foliage discs radiating precisely from the attachment core.
     *
     * @param level           The secure world generation interface context.
     * @param random          The deterministic random source controlling probabilistic bounds.
     * @param attachmentPoint The central trunk coordinate structurally anchoring the canopy iteration limits.
     * @param foliageProvider The block state provider feeding environmental configurations structurally.
     * @param radiusOffset    The dynamic additive modifier actively extending or retracting the baseline calculation.
     */
    @Override
    public void placeFoliage(WorldGenAccess level, Random random, Location attachmentPoint, BlockStateProvider foliageProvider, int radiusOffset) {
        int activeRadius = radius + radiusOffset;

        for (int yOffset = -2; yOffset <= 1; yOffset++) {
            int layerRadius = activeRadius + (yOffset >= 0 ? -1 : 0);

            for (int dx = -layerRadius; dx <= layerRadius + 1; dx++) {
                for (int dz = -layerRadius; dz <= layerRadius + 1; dz++) {
                    if (Math.abs(dx) > layerRadius || Math.abs(dz) > layerRadius) {
                        if (random.nextInt(2) == 0) continue;
                    }

                    Location target = attachmentPoint.clone().add(dx, yOffset, dz);
                    if (target.getBlockY() >= level.world().getMaxHeight()) continue;

                    if (level.getType(target.getBlockX(), target.getBlockY(), target.getBlockZ()).isAir()) {
                        BlockInfo stateToPlace = foliageProvider.getState(random, target);
                        if (stateToPlace != null) {
                            WorldGenUtils.placeBlock(level, target, stateToPlace);
                        }
                    }
                }
            }
        }
    }

    /**
     * Retrieves the formalized registry definition cleanly categorizing this placer structure.
     *
     * @return The specific placer type enumeration mapping natively evaluated mechanically.
     */
    @Override
    public FoliagePlacerType<?> getType() {
        return TYPE;
    }
}