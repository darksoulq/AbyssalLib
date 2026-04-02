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
 * A foliage placer that generates a conical, layered structure.
 * <p>
 * This mimics the natural shape of conifers (spruce, pine), where the radius
 * of the leaves gradually widens as it approaches the bottom of the canopy,
 * occasionally stepping inwards to create horizontal ridges.
 */
public class PineFoliagePlacer extends FoliagePlacer {

    /**
     * The codec used for serializing and deserializing the pine foliage placer.
     */
    public static final Codec<PineFoliagePlacer> CODEC = new Codec<>() {

        /**
         * Decodes the placer from a serialized map.
         *
         * @param ops   The dynamic operations logic.
         * @param input The serialized input.
         * @param <D>   The data format type.
         * @return A new instance of the pine foliage placer.
         * @throws CodecException If the height field is missing.
         */
        @Override
        public <D> PineFoliagePlacer decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
            int height = Codecs.INT.decode(ops, map.get(ops.createString("height")));
            return new PineFoliagePlacer(height);
        }

        /**
         * Encodes the placer into a serialized map.
         *
         * @param ops   The dynamic operations logic.
         * @param value The placer instance to encode.
         * @param <D>   The data format type.
         * @return The encoded data object.
         * @throws CodecException If serialization fails.
         */
        @Override
        public <D> D encode(DynamicOps<D> ops, PineFoliagePlacer value) throws CodecException {
            Map<D, D> map = new HashMap<>();
            map.put(ops.createString("height"), Codecs.INT.encode(ops, value.height));
            return ops.createMap(map);
        }
    };

    /**
     * The registered type definition for the pine foliage placer.
     */
    public static final FoliagePlacerType<PineFoliagePlacer> TYPE = () -> CODEC;

    /** The vertical height (layers) of the conical foliage section. */
    private final int height;

    /**
     * Constructs a new PineFoliagePlacer.
     *
     * @param height The total number of vertical layers the foliage spans downwards.
     */
    public PineFoliagePlacer(int height) {
        this.height = height;
    }

    /**
     * Generates a conical structure of leaves downwards from the attachment point.
     *
     * @param level           The world generation accessor.
     * @param random          The deterministic random source.
     * @param attachmentPoint The top-center location of the foliage.
     * @param foliageProvider The block state provider for the leaf material.
     * @param radius          The maximum horizontal radius at the base of the cone.
     */
    @Override
    public void placeFoliage(WorldGenAccess level, Random random, Location attachmentPoint, BlockStateProvider foliageProvider, int radius) {
        int currentRadius = 0;
        
        for (int yOffset = 0; yOffset >= -height; --yOffset) {
            for (int dx = -currentRadius; dx <= currentRadius; dx++) {
                for (int dz = -currentRadius; dz <= currentRadius; dz++) {
                    if (Math.abs(dx) == currentRadius && Math.abs(dz) == currentRadius && currentRadius > 0) {
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

            if (yOffset % 2 == 0) {
                currentRadius++;
            }
            if (currentRadius > radius) {
                currentRadius = 1;
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