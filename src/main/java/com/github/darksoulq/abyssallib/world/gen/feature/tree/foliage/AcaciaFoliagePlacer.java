package com.github.darksoulq.abyssallib.world.gen.feature.tree.foliage;

import com.github.darksoulq.abyssallib.common.serialization.BlockInfo;
import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.world.gen.WorldGenAccess;
import com.github.darksoulq.abyssallib.world.gen.internal.WorldGenUtils;
import com.github.darksoulq.abyssallib.world.gen.state.provider.BlockStateProvider;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * A standard foliage placer explicitly designed to generate the iconic horizontal,
 * layered canopy footprint associated strictly with Acacia tree typologies.
 */
public class AcaciaFoliagePlacer extends FoliagePlacer {

    /**
     * The designated codec instance responsible for serializing and deserializing the state variables of this placer.
     */
    public static final Codec<AcaciaFoliagePlacer> CODEC = new Codec<>() {

        /**
         * Decodes the internal configuration explicitly mapped from a serialized data container.
         *
         * @param ops   The operational mapping framework governing translations.
         * @param input The unparsed initial data payload.
         * @param <D>   The dynamic object bounds dictating parsing logic.
         * @return The correctly instantiated placer logic implementation.
         * @throws CodecException Resolves strictly upon encountering missing required mapping fields.
         */
        @Override
        public <D> AcaciaFoliagePlacer decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
            int radius = Codecs.INT.decode(ops, map.get(ops.createString("radius")));
            return new AcaciaFoliagePlacer(radius);
        }

        /**
         * Encodes the instantiated explicit memory states strictly into the designated map structure.
         *
         * @param ops   The operational mapping framework governing translations.
         * @param value The active placer instance retaining memory states.
         * @param <D>   The dynamic object bounds dictating parsing logic.
         * @return The thoroughly formatted serialized representation map.
         * @throws CodecException Resolves strictly upon internal translation conversion failures.
         */
        @Override
        public <D> D encode(DynamicOps<D> ops, AcaciaFoliagePlacer value) throws CodecException {
            Map<D, D> map = new HashMap<>();
            map.put(ops.createString("radius"), Codecs.INT.encode(ops, value.radius));
            return ops.createMap(map);
        }
    };

    /**
     * The globally registered type definition representing the acacia foliage placer.
     */
    public static final FoliagePlacerType<AcaciaFoliagePlacer> TYPE = () -> CODEC;

    /**
     * The calculated absolute radial boundary governing the horizontal foliage spread.
     */
    private final int radius;

    /**
     * Constructs a new acacia foliage placement generator bound by a structural radius parameter.
     *
     * @param radius The fundamental mathematical limit bounding lateral canopy growth computations.
     */
    public AcaciaFoliagePlacer(int radius) {
        this.radius = radius;
    }

    /**
     * Executes the procedural logic shaping horizontal foliage discs radiating precisely from the attachment core.
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

        for (int yOffset = -1; yOffset <= 0; yOffset++) {
            int layerRadius = activeRadius + (yOffset == 0 ? -1 : 0);

            for (int dx = -layerRadius; dx <= layerRadius; dx++) {
                for (int dz = -layerRadius; dz <= layerRadius; dz++) {
                    if (Math.abs(dx) == layerRadius && Math.abs(dz) == layerRadius && layerRadius > 0) {
                        continue;
                    }

                    Location target = attachmentPoint.clone().add(dx, yOffset, dz);
                    if (target.getBlockY() >= level.getWorld().getMaxHeight()) continue;

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