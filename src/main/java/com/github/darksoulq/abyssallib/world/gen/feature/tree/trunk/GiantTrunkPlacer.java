package com.github.darksoulq.abyssallib.world.gen.feature.tree.trunk;

import com.github.darksoulq.abyssallib.common.serialization.BlockInfo;
import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.world.gen.WorldGenAccess;
import com.github.darksoulq.abyssallib.world.gen.internal.WorldGenUtils;
import com.github.darksoulq.abyssallib.world.gen.state.provider.BlockStateProvider;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * A trunk placer that generates a massive 2x2 vertical column of blocks.
 * <p>
 * This algorithm calculates four parallel columns, originating from the base
 * coordinate and expanding positive X and positive Z. It returns four distinct
 * attachment points, ensuring that associated foliage placers render a massive,
 * seamlessly merged canopy.
 */
public class GiantTrunkPlacer extends TrunkPlacer {

    /**
     * The codec used for serializing and deserializing the giant trunk placer.
     * This placer has no configurable variables, so it encodes to an empty map.
     */
    public static final Codec<GiantTrunkPlacer> CODEC = new Codec<>() {
        @Override
        public <D> GiantTrunkPlacer decode(DynamicOps<D> ops, D input) {
            return new GiantTrunkPlacer();
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, GiantTrunkPlacer value) {
            return ops.createMap(new HashMap<>());
        }
    };

    /**
     * The registered type definition for the giant trunk placer.
     */
    public static final TrunkPlacerType<GiantTrunkPlacer> TYPE = () -> CODEC;

    /**
     * Constructs a new GiantTrunkPlacer.
     */
    public GiantTrunkPlacer() {}

    /**
     * Places a 2x2 vertical column of blocks up to the specified height.
     *
     * @param level         The world generation accessor.
     * @param random        The deterministic random source.
     * @param origin        The base starting location (the north-west corner of the 2x2 trunk).
     * @param trunkProvider The block state provider for the trunk material.
     * @param height        The calculated total height for this specific tree instance.
     * @return A list containing the four attachment vectors at the top of the trunk.
     */
    @Override
    public List<Vector> placeTrunk(WorldGenAccess level, Random random, Location origin, BlockStateProvider trunkProvider, int height) {
        List<Vector> attachmentPoints = new ArrayList<>();

        for (int dx = 0; dx <= 1; dx++) {
            for (int dz = 0; dz <= 1; dz++) {
                for (int y = 0; y < height; y++) {
                    Location target = origin.clone().add(dx, y, dz);
                    if (target.getBlockY() >= level.getWorld().getMaxHeight()) break;
                    
                    BlockInfo stateToPlace = trunkProvider.getState(random, target);
                    if (stateToPlace != null) {
                        WorldGenUtils.placeBlock(level, target, stateToPlace);
                    }
                }
                attachmentPoints.add(new Vector(origin.getBlockX() + dx, origin.getBlockY() + height, origin.getBlockZ() + dz));
            }
        }

        return attachmentPoints;
    }

    /**
     * Retrieves the specific type definition for this trunk placer.
     *
     * @return The trunk placer type associated with this instance.
     */
    @Override
    public TrunkPlacerType<?> getType() {
        return TYPE;
    }
}