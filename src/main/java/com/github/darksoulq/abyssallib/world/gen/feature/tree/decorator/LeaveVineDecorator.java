package com.github.darksoulq.abyssallib.world.gen.feature.tree.decorator;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.world.gen.WorldGenAccess;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.MultipleFacing;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * A specialized tree decorator responsible for seamlessly draping organic vine structures
 * directly downwards from the generated leaf canopy blocks.
 */
public class LeaveVineDecorator extends TreeDecorator {

    /**
     * The designated codec instance responsible for serializing and deserializing the state variables of this decorator.
     */
    public static final Codec<LeaveVineDecorator> CODEC = new Codec<>() {

        /**
         * Decodes the internal configuration explicitly mapped from a serialized data container.
         *
         * @param ops   The operational mapping framework governing translations.
         * @param input The unparsed initial data payload.
         * @param <D>   The dynamic object bounds dictating parsing logic.
         * @return The correctly instantiated decorator logic implementation.
         * @throws CodecException Resolves strictly upon encountering missing required mapping fields.
         */
        @Override
        public <D> LeaveVineDecorator decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
            float probability = Codecs.FLOAT.decode(ops, map.get(ops.createString("probability")));
            return new LeaveVineDecorator(probability);
        }

        /**
         * Encodes the instantiated explicit memory states strictly into the designated map structure.
         *
         * @param ops   The operational mapping framework governing translations.
         * @param value The active decorator instance retaining memory states.
         * @param <D>   The dynamic object bounds dictating parsing logic.
         * @return The thoroughly formatted serialized representation map.
         * @throws CodecException Resolves strictly upon internal translation conversion failures.
         */
        @Override
        public <D> D encode(DynamicOps<D> ops, LeaveVineDecorator value) throws CodecException {
            Map<D, D> map = new HashMap<>();
            map.put(ops.createString("probability"), Codecs.FLOAT.encode(ops, value.probability));
            return ops.createMap(map);
        }
    };

    /**
     * The globally registered type definition representing the leaf vine tree decorator.
     */
    public static final TreeDecoratorType<LeaveVineDecorator> TYPE = () -> CODEC;

    /**
     * The absolute execution probability bounding instantiation sequences per targeted block.
     */
    private final float probability;

    /**
     * Constructs a new leaf vine placement decorator bound by a strict statistical probability parameter.
     *
     * @param probability The bounded float percentage denoting execution success chances.
     */
    public LeaveVineDecorator(float probability) {
        this.probability = probability;
    }

    /**
     * Iterates the complete tracked foliage structure attempting probability-bound downward vine placements natively.
     *
     * @param level  The secure world generation interface context.
     * @param random The deterministic random source controlling probabilistic branches.
     * @param logs   The actively tracked structural anchor coordinates designating trunk placement.
     * @param leaves The actively tracked structural anchor coordinates designating foliage placement.
     */
    @Override
    public void decorate(WorldGenAccess level, Random random, Set<Location> logs, Set<Location> leaves) {
        BlockData vineData = Material.VINE.createBlockData();
        if (vineData instanceof MultipleFacing facingData) {
            facingData.setFace(BlockFace.UP, true);
        }

        for (Location leaf : leaves) {
            if (random.nextFloat() < probability) {
                Location below = leaf.clone().add(0, -1, 0);
                if (below.getBlockY() >= level.getWorld().getMinHeight() && level.getType(below.getBlockX(), below.getBlockY(), below.getBlockZ()).isAir()) {
                    level.setBlock(below.getBlockX(), below.getBlockY(), below.getBlockZ(), vineData);
                    
                    Location trail = below.clone().add(0, -1, 0);
                    while (random.nextBoolean() && trail.getBlockY() >= level.getWorld().getMinHeight() && level.getType(trail.getBlockX(), trail.getBlockY(), trail.getBlockZ()).isAir()) {
                        level.setBlock(trail.getBlockX(), trail.getBlockY(), trail.getBlockZ(), vineData);
                        trail.add(0, -1, 0);
                    }
                }
            }
        }
    }

    /**
     * Retrieves the formalized registry definition cleanly categorizing this decorator structure.
     *
     * @return The specific decorator type enumeration mapping.
     */
    @Override
    public TreeDecoratorType<?> getType() {
        return TYPE;
    }
}