package com.github.darksoulq.abyssallib.world.gen.feature.tree.trunk;

import com.github.darksoulq.abyssallib.common.serialization.BlockInfo;
import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.world.gen.WorldGenAccess;
import com.github.darksoulq.abyssallib.world.gen.internal.WorldGenUtils;
import com.github.darksoulq.abyssallib.world.gen.state.provider.BlockStateProvider;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * A standard trunk placer that generates a straight vertical column of blocks.
 * <p>
 * This is used for basic trees like Oak, Birch, or Spruce. It returns a single
 * foliage attachment point located at the very top of the generated trunk.
 */
public class StraightTrunkPlacer extends TrunkPlacer {

    /**
     * The codec used for serializing and deserializing the straight trunk placer.
     * This placer has no configurable variables, so it encodes to an empty map.
     */
    public static final Codec<StraightTrunkPlacer> CODEC = new Codec<>() {
        @Override
        public <D> StraightTrunkPlacer decode(DynamicOps<D> ops, D input) {
            return new StraightTrunkPlacer();
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, StraightTrunkPlacer value) {
            return ops.createMap(new HashMap<>());
        }
    };

    /**
     * The registered type definition for the straight trunk placer.
     */
    public static final TrunkPlacerType<StraightTrunkPlacer> TYPE = () -> CODEC;

    /**
     * Constructs a new StraightTrunkPlacer.
     */
    public StraightTrunkPlacer() {}

    /**
     * Places a vertical column of blocks up to the specified height.
     *
     * @param level         The world generation accessor.
     * @param random        The deterministic random source.
     * @param origin        The base starting location of the tree.
     * @param trunkProvider The block state provider for the trunk material.
     * @param height        The calculated total height for this specific tree instance.
     * @return A list containing the single attachment vector at the top of the trunk.
     */
    @Override
    public List<Vector> placeTrunk(WorldGenAccess level, Random random, Location origin, BlockStateProvider trunkProvider, int height) {
        for (int i = 0; i < height; i++) {
            Location target = origin.clone().add(0, i, 0);
            if (target.getBlockY() >= level.getWorld().getMaxHeight()) break;
            
            BlockInfo stateToPlace = trunkProvider.getState(random, target);
            if (stateToPlace != null) {
                WorldGenUtils.placeBlock(level, target, stateToPlace);
            }
        }

        return List.of(new Vector(origin.getBlockX(), origin.getBlockY() + height, origin.getBlockZ()));
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