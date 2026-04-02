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
 * A sophisticated trunk placer that generates a straight vertical column before sharply
 * bending diagonally towards a randomized horizontal direction.
 */
public class BendingTrunkPlacer extends TrunkPlacer {

    /**
     * The standard horizontal block faces utilized for directional vector resolution.
     */
    private static final BlockFace[] HORIZONTALS = {
        BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST
    };

    /**
     * The codec responsible for managing the serialization and deserialization of the bending trunk constraints.
     */
    public static final Codec<BendingTrunkPlacer> CODEC = new Codec<>() {

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
        public <D> BendingTrunkPlacer decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
            int bendLength = Codecs.INT.decode(ops, map.get(ops.createString("bend_length")));
            return new BendingTrunkPlacer(bendLength);
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
        public <D> D encode(DynamicOps<D> ops, BendingTrunkPlacer value) throws CodecException {
            Map<D, D> map = new HashMap<>();
            map.put(ops.createString("bend_length"), Codecs.INT.encode(ops, value.bendLength));
            return ops.createMap(map);
        }
    };

    /**
     * The formally registered type enumeration linking the placer exactly into the standard registry maps.
     */
    public static final TrunkPlacerType<BendingTrunkPlacer> TYPE = () -> CODEC;

    /**
     * The absolute predefined block length dictating the angled growth sequence distance.
     */
    private final int bendLength;

    /**
     * Constructs a bending trunk geometric constructor mapped natively to defined length algorithms.
     *
     * @param bendLength The integer count governing the absolute distance the trunk will actively bend.
     */
    public BendingTrunkPlacer(int bendLength) {
        this.bendLength = bendLength;
    }

    /**
     * Executes the procedural logic mapping straight vertical limits sequentially transitioning into defined angled structural offsets.
     *
     * @param level         The secure world generation interface context.
     * @param random        The deterministic random source controlling probabilistic branches.
     * @param origin        The central anchor coordinate structurally initializing the placement boundaries.
     * @param trunkProvider The block state provider feeding environmental materials natively.
     * @param height        The completely calculated overall height integer dictating bounds.
     * @return The explicit list encapsulating the finalized upper attachment coordinate targeting subsequent foliage integration.
     */
    @Override
    public List<Vector> placeTrunk(WorldGenAccess level, Random random, Location origin, BlockStateProvider trunkProvider, int height) {
        List<Vector> attachmentPoints = new ArrayList<>();
        int currentX = origin.getBlockX();
        int currentY = origin.getBlockY();
        int currentZ = origin.getBlockZ();

        for (int i = 0; i < height; i++) {
            Location target = new Location(level.getWorld(), currentX, currentY, currentZ);
            if (target.getBlockY() >= level.getWorld().getMaxHeight()) break;

            BlockInfo stateToPlace = trunkProvider.getState(random, target);
            if (stateToPlace != null) {
                WorldGenUtils.placeBlock(level, target, stateToPlace);
            }
            currentY++;
        }

        BlockFace bendDirection = HORIZONTALS[random.nextInt(HORIZONTALS.length)];

        for (int i = 0; i < bendLength; i++) {
            currentX += bendDirection.getModX();
            if (random.nextBoolean()) {
                currentY++;
            }
            currentZ += bendDirection.getModZ();

            Location target = new Location(level.getWorld(), currentX, currentY, currentZ);
            if (target.getBlockY() >= level.getWorld().getMaxHeight()) break;

            BlockInfo stateToPlace = trunkProvider.getState(random, target);
            if (stateToPlace != null) {
                WorldGenUtils.placeBlock(level, target, stateToPlace);
            }
        }

        attachmentPoints.add(new Vector(currentX, currentY, currentZ));
        return attachmentPoints;
    }

    /**
     * Retrieves the formalized registry definition natively establishing type identities.
     *
     * @return The specific registry type interface implementation.
     */
    @Override
    public TrunkPlacerType<?> getType() {
        return TYPE;
    }
}