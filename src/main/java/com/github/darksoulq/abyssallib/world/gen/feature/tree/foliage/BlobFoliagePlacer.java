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
 * A standard foliage placer that generates a layered spherical blob of leaves.
 * <p>
 * This replicates the iconic leaf shape of standard Minecraft Oak and Birch trees.
 */
public class BlobFoliagePlacer extends FoliagePlacer {

    /**
     * The codec used for serializing and deserializing the blob foliage placer.
     */
    public static final Codec<BlobFoliagePlacer> CODEC = new Codec<>() {

        /**
         * Decodes the placer from a serialized map.
         *
         * @param ops   The dynamic operations logic.
         * @param input The serialized input.
         * @param <D>   The data format type.
         * @return A new instance of the blob foliage placer.
         * @throws CodecException If the height field is missing.
         */
        @Override
        public <D> BlobFoliagePlacer decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
            int height = Codecs.INT.decode(ops, map.get(ops.createString("height")));
            return new BlobFoliagePlacer(height);
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
        public <D> D encode(DynamicOps<D> ops, BlobFoliagePlacer value) throws CodecException {
            Map<D, D> map = new HashMap<>();
            map.put(ops.createString("height"), Codecs.INT.encode(ops, value.height));
            return ops.createMap(map);
        }
    };

    /**
     * The registered type definition for the blob foliage placer.
     */
    public static final FoliagePlacerType<BlobFoliagePlacer> TYPE = () -> CODEC;

    /** The vertical height (layers) of the foliage blob. */
    private final int height;

    /**
     * Constructs a new BlobFoliagePlacer.
     *
     * @param height The number of vertical layers the blob spans downwards from the attachment point.
     */
    public BlobFoliagePlacer(int height) {
        this.height = height;
    }

    /**
     * Generates a cascading blob of leaves downwards from the attachment point.
     *
     * @param level           The world generation accessor.
     * @param random          The deterministic random source.
     * @param attachmentPoint The top-center location of the foliage blob.
     * @param foliageProvider The block state provider for the leaf material.
     * @param radius          The maximum horizontal radius of the blob.
     */
    @Override
    public void placeFoliage(WorldGenAccess level, Random random, Location attachmentPoint, BlockStateProvider foliageProvider, int radius) {
        for (int yOffset = 0; yOffset >= -height; --yOffset) {
            int layerRadius = radius + (yOffset == 0 || yOffset == -height ? -1 : 0);
            
            for (int dx = -layerRadius; dx <= layerRadius; dx++) {
                for (int dz = -layerRadius; dz <= layerRadius; dz++) {
                    if (Math.abs(dx) == layerRadius && Math.abs(dz) == layerRadius && (random.nextInt(2) == 0 || yOffset == 0)) {
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
     * Retrieves the specific type definition for this foliage placer.
     *
     * @return The foliage placer type associated with this instance.
     */
    @Override
    public FoliagePlacerType<?> getType() {
        return TYPE;
    }
}