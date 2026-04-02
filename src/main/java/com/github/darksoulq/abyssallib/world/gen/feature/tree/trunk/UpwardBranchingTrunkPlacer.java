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
 * A highly dynamic trunk placer algorithm charting a primary vertical stem that generates numerous distinct upwards-reaching structural branches.
 */
public class UpwardBranchingTrunkPlacer extends TrunkPlacer {

    /**
     * The standard horizontal block faces utilized for radial branching vector resolution.
     */
    private static final BlockFace[] HORIZONTALS = {
        BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST
    };

    /**
     * The codec mapping runtime instantiation states perfectly into native generic serialized memory footprints.
     */
    public static final Codec<UpwardBranchingTrunkPlacer> CODEC = new Codec<>() {

        /**
         * Parses the primitive data maps natively into the functional structured class memory.
         *
         * @param ops   The translation engine natively binding format boundaries.
         * @param input The unparsed data object footprint.
         * @param <D>   The structural constraint defining the data node format.
         * @return The correctly instantiated memory logic model.
         * @throws CodecException Resolves strictly upon critically failing data integrity validation checks.
         */
        @Override
        public <D> UpwardBranchingTrunkPlacer decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
            int branchCount = Codecs.INT.decode(ops, map.get(ops.createString("branch_count")));
            int branchLength = Codecs.INT.decode(ops, map.get(ops.createString("branch_length")));
            return new UpwardBranchingTrunkPlacer(branchCount, branchLength);
        }

        /**
         * Translates the initialized functional memory states back into completely formatted map objects securely.
         *
         * @param ops   The translation engine natively binding format boundaries.
         * @param value The active initialized target logic model holding configuration states.
         * @param <D>   The structural constraint defining the data node format.
         * @return The thoroughly constructed serialized payload tree natively formatted correctly.
         * @throws CodecException Resolves strictly upon encountering unrecoverable transformation pipeline faults.
         */
        @Override
        public <D> D encode(DynamicOps<D> ops, UpwardBranchingTrunkPlacer value) throws CodecException {
            Map<D, D> map = new HashMap<>();
            map.put(ops.createString("branch_count"), Codecs.INT.encode(ops, value.branchCount));
            map.put(ops.createString("branch_length"), Codecs.INT.encode(ops, value.branchLength));
            return ops.createMap(map);
        }
    };

    /**
     * The formally registered type enumeration linking the placer exactly into the standard registry maps.
     */
    public static final TrunkPlacerType<UpwardBranchingTrunkPlacer> TYPE = () -> CODEC;

    /**
     * The designated maximum aggregate of independently calculated vertical branches natively generated across the trunk sequence.
     */
    private final int branchCount;

    /**
     * The precisely calculated block progression distance strictly governing subsequent branch extensions visually.
     */
    private final int branchLength;

    /**
     * Constructs a dynamic vertical branching geometric constructor mapped natively to specific dimensional configurations natively securely.
     *
     * @param branchCount  The integer count specifying branching aggregation volumes natively.
     * @param branchLength The integer count governing the absolute distance individual branches will continually grow.
     */
    public UpwardBranchingTrunkPlacer(int branchCount, int branchLength) {
        this.branchCount = branchCount;
        this.branchLength = branchLength;
    }

    /**
     * Executes the procedural logic mapping straight vertical limits sequentially transitioning into defined upward branching structural offsets securely natively.
     *
     * @param level         The secure world generation interface context.
     * @param random        The deterministic random source controlling probabilistic bounds.
     * @param origin        The central anchor coordinate structurally initializing the placement boundaries.
     * @param trunkProvider The block state provider feeding environmental materials natively.
     * @param height        The completely calculated overall height integer dictating bounds.
     * @return The explicit list encapsulating the finalized upper attachment coordinate targets for subsequent foliage integration securely.
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

        int branchStartHeight = height / 3;

        for (int i = 0; i < branchCount; i++) {
            int branchY = branchStartHeight + random.nextInt(Math.max(1, height - branchStartHeight));
            BlockFace direction = HORIZONTALS[random.nextInt(HORIZONTALS.length)];
            
            int currentX = origin.getBlockX() + direction.getModX();
            int currentZ = origin.getBlockZ() + direction.getModZ();
            int currentY = origin.getBlockY() + branchY;
            
            for (int step = 0; step < branchLength; step++) {
                currentY++;
                if (random.nextBoolean()) {
                    currentX += direction.getModX();
                    currentZ += direction.getModZ();
                }
                
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
     * Retrieves the formalized registry definition natively establishing type identities seamlessly securely.
     *
     * @return The specific registry type interface implementation.
     */
    @Override
    public TrunkPlacerType<?> getType() {
        return TYPE;
    }
}