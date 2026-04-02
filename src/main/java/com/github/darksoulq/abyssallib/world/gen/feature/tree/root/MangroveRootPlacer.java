package com.github.darksoulq.abyssallib.world.gen.feature.tree.root;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.world.gen.WorldGenAccess;
import com.github.darksoulq.abyssallib.world.gen.internal.WorldGenUtils;
import com.github.darksoulq.abyssallib.world.gen.state.provider.BlockStateProvider;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Random;

/**
 * A root placer that generates sprawling, arching structural roots.
 * <p>
 * Because roots lift the tree, this placer recalculates the trunk's starting
 * origin and returns the new apex coordinate.
 */
public class MangroveRootPlacer extends RootPlacer {

    /**
     * The codec used for serializing and deserializing the mangrove root placer.
     */
    public static final Codec<MangroveRootPlacer> CODEC = new Codec<>() {
        @Override
        public <D> MangroveRootPlacer decode(DynamicOps<D> ops, D input) {
            return new MangroveRootPlacer();
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, MangroveRootPlacer value) {
            return ops.createMap(new HashMap<>());
        }
    };

    /**
     * The registered type definition for the mangrove root placer.
     */
    public static final RootPlacerType<MangroveRootPlacer> TYPE = () -> CODEC;

    /**
     * Constructs a new MangroveRootPlacer.
     */
    public MangroveRootPlacer() {}

    /**
     * Generates a sprawling root structure and elevates the central trunk origin.
     *
     * @param level        The world generation accessor.
     * @param random       The deterministic random source.
     * @param origin       The requested base location of the tree.
     * @param rootProvider The provider defining the material of the roots.
     * @param dirtProvider The provider to optionally enforce the ground beneath the roots.
     * @return The new, elevated origin location where the trunk should begin.
     */
    @Override
    public Location placeRoots(WorldGenAccess level, Random random, Location origin, BlockStateProvider rootProvider, BlockStateProvider dirtProvider) {
        int trunkOffset = random.nextInt(3) + 2;
        Location trunkOrigin = origin.clone().add(0, trunkOffset, 0);

        for (int i = 0; i < 4; i++) {
            int dx = random.nextInt(3) * (random.nextBoolean() ? 1 : -1);
            int dz = random.nextInt(3) * (random.nextBoolean() ? 1 : -1);
            
            Location rootEnd = origin.clone().add(dx, 0, dz);

            int currentY = trunkOrigin.getBlockY();
            int currentX = trunkOrigin.getBlockX();
            int currentZ = trunkOrigin.getBlockZ();

            while (currentY >= rootEnd.getBlockY()) {
                Location pos = new Location(level.getWorld(), currentX, currentY, currentZ);
                
                if (level.getType(currentX, currentY, currentZ).isAir()) {
                    WorldGenUtils.placeBlock(level, pos, rootProvider.getState(random, pos));
                }

                currentY--;
                if (currentY > rootEnd.getBlockY() && random.nextBoolean()) {
                    if (currentX < rootEnd.getBlockX()) currentX++;
                    else if (currentX > rootEnd.getBlockX()) currentX--;
                    
                    if (currentZ < rootEnd.getBlockZ()) currentZ++;
                    else if (currentZ > rootEnd.getBlockZ()) currentZ--;
                }
            }
            WorldGenUtils.placeBlock(level, new Location(level.getWorld(), currentX, currentY, currentZ), dirtProvider.getState(random, rootEnd));
        }

        return trunkOrigin;
    }

    /**
     * Retrieves the specific type definition for this root placer.
     *
     * @return The root placer type associated with this instance.
     */
    @Override
    public RootPlacerType<?> getType() {
        return TYPE;
    }
}