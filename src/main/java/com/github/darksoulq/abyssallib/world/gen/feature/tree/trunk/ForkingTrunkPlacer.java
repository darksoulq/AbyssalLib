package com.github.darksoulq.abyssallib.world.gen.feature.tree.trunk;

import com.github.darksoulq.abyssallib.common.serialization.BlockInfo;
import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.world.gen.WorldGenAccess;
import com.github.darksoulq.abyssallib.world.gen.internal.WorldGenUtils;
import com.github.darksoulq.abyssallib.world.gen.state.provider.BlockStateProvider;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

import java.util.*;

/**
 * A trunk placer that generates a main vertical stem and sprouts diagonal branches
 * from its upper section.
 * <p>
 * This placer is highly useful for generating sprawling trees such as Acacia or
 * large custom oaks. It calculates a main trunk and then casts out secondary
 * branches, returning attachment points for the tips of every branch generated.
 */
public class ForkingTrunkPlacer extends TrunkPlacer {

    /**
     * The standard horizontal directions used to determine branch growth paths.
     */
    private static final BlockFace[] HORIZONTALS = {
        BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST
    };

    /**
     * The codec used for serializing and deserializing the forking trunk placer.
     */
    public static final Codec<ForkingTrunkPlacer> CODEC = new Codec<>() {

        /**
         * Decodes the placer from a serialized map.
         *
         * @param ops   The dynamic operations logic.
         * @param input The serialized input.
         * @param <D>   The data format type.
         * @return A new instance of the forking trunk placer.
         * @throws CodecException If the configuration fields are missing.
         */
        @Override
        public <D> ForkingTrunkPlacer decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
            int branchCount = Codecs.INT.decode(ops, map.get(ops.createString("branch_count")));
            int branchLength = Codecs.INT.decode(ops, map.get(ops.createString("branch_length")));
            return new ForkingTrunkPlacer(branchCount, branchLength);
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
        public <D> D encode(DynamicOps<D> ops, ForkingTrunkPlacer value) throws CodecException {
            Map<D, D> map = new HashMap<>();
            map.put(ops.createString("branch_count"), Codecs.INT.encode(ops, value.branchCount));
            map.put(ops.createString("branch_length"), Codecs.INT.encode(ops, value.branchLength));
            return ops.createMap(map);
        }
    };

    /**
     * The registered type definition for the forking trunk placer.
     */
    public static final TrunkPlacerType<ForkingTrunkPlacer> TYPE = () -> CODEC;

    /** The maximum number of branches this trunk will attempt to generate. */
    private final int branchCount;

    /** The maximum block length of each generated branch. */
    private final int branchLength;

    /**
     * Constructs a new ForkingTrunkPlacer.
     *
     * @param branchCount  The number of secondary branches to generate.
     * @param branchLength The length of the secondary branches.
     */
    public ForkingTrunkPlacer(int branchCount, int branchLength) {
        this.branchCount = branchCount;
        this.branchLength = branchLength;
    }

    /**
     * Places the main vertical trunk and spawns diagonal branches outward.
     *
     * @param level         The world generation accessor.
     * @param random        The deterministic random source.
     * @param origin        The base starting location of the tree.
     * @param trunkProvider The block state provider for the trunk material.
     * @param height        The calculated total height for this specific tree instance.
     * @return A list of vectors representing the tops of the main trunk and all branches.
     */
    @Override
    public List<Vector> placeTrunk(WorldGenAccess level, Random random, Location origin, BlockStateProvider trunkProvider, int height) {
        List<Vector> attachmentPoints = new ArrayList<>();
        
        for (int i = 0; i < height; i++) {
            Location target = origin.clone().add(0, i, 0);
            if (target.getBlockY() >= level.getWorld().getMaxHeight()) break;
            
            BlockInfo stateToPlace = trunkProvider.getState(random, target);
            if (stateToPlace != null) {
                WorldGenUtils.placeBlock(level, target, stateToPlace);
            }
        }
        
        attachmentPoints.add(new Vector(origin.getBlockX(), origin.getBlockY() + height, origin.getBlockZ()));

        int branchStartHeight = height / 2;

        for (int i = 0; i < branchCount; i++) {
            int branchY = branchStartHeight + random.nextInt(Math.max(1, height - branchStartHeight));
            BlockFace direction = HORIZONTALS[random.nextInt(HORIZONTALS.length)];
            
            int currentX = origin.getBlockX();
            int currentZ = origin.getBlockZ();
            int currentY = origin.getBlockY() + branchY;
            
            for (int step = 0; step < branchLength; step++) {
                currentX += direction.getModX();
                currentZ += direction.getModZ();
                currentY += 1;
                
                Location branchTarget = new Location(level.getWorld(), currentX, currentY, currentZ);
                if (branchTarget.getBlockY() >= level.getWorld().getMaxHeight()) break;

                BlockInfo stateToPlace = trunkProvider.getState(random, branchTarget);
                if (stateToPlace != null) {
                    WorldGenUtils.placeBlock(level, branchTarget, stateToPlace);
                }
            }
            
            attachmentPoints.add(new Vector(currentX, currentY, currentZ));
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