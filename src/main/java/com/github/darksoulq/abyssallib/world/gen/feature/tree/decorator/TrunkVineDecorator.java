package com.github.darksoulq.abyssallib.world.gen.feature.tree.decorator;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.RecordBuilder;
import com.github.darksoulq.abyssallib.world.gen.WorldGenAccess;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.MultipleFacing;

import java.util.Random;
import java.util.Set;

/**
 * A specialized tree decorator responsible for seamlessly wrapping organic vine structures
 * directly onto the exposed horizontal faces of the generated trunk blocks.
 */
public class TrunkVineDecorator extends TreeDecorator {

    /**
     * The standard horizontal directional vectors evaluated during radial collision analysis.
     */
    private static final BlockFace[] FACES = {BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST};

    /**
     * The designated codec instance responsible for serializing and deserializing the state variables of this decorator.
     */
    public static final Codec<TrunkVineDecorator> CODEC = RecordBuilder.create(instance -> instance.group(
        Codecs.FLOAT.fieldOf("probability").forGetter(TrunkVineDecorator.class, p -> p.probability)
    ).apply(instance, TrunkVineDecorator::new)).describe("TrunkVineDecorator");

    /**
     * The globally registered type definition representing the trunk vine tree decorator.
     */
    public static final TreeDecoratorType<TrunkVineDecorator> TYPE = () -> CODEC;

    /**
     * The absolute execution probability bounding instantiation sequences per targeted horizontal block face.
     */
    private final float probability;

    /**
     * Constructs a new trunk vine placement decorator bound by a strict statistical probability parameter.
     *
     * @param probability The bounded float percentage denoting execution success chances.
     */
    public TrunkVineDecorator(float probability) {
        this.probability = probability;
    }

    /**
     * Iterates the complete tracked trunk structure attempting probability-bound lateral vine placements natively.
     *
     * @param level  The secure world generation interface context.
     * @param random The deterministic random source controlling probabilistic branches.
     * @param logs   The actively tracked structural anchor coordinates designating trunk placement.
     * @param leaves The actively tracked structural anchor coordinates designating foliage placement.
     */
    @Override
    public void decorate(WorldGenAccess level, Random random, Set<Location> logs, Set<Location> leaves) {
        for (Location log : logs) {
            for (BlockFace face : FACES) {
                if (random.nextFloat() < probability) {
                    Location target = log.clone().add(face.getModX(), face.getModY(), face.getModZ());
                    if (level.getType(target.getBlockX(), target.getBlockY(), target.getBlockZ()).isAir()) {
                        BlockData vineData = Material.VINE.createBlockData();
                        if (vineData instanceof MultipleFacing facingData) {
                            facingData.setFace(face.getOppositeFace(), true);
                        }
                        level.setBlock(target.getBlockX(), target.getBlockY(), target.getBlockZ(), vineData);
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