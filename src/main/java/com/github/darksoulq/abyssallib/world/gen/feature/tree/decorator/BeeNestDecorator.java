package com.github.darksoulq.abyssallib.world.gen.feature.tree.decorator;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.RecordBuilder;
import com.github.darksoulq.abyssallib.world.gen.WorldGenAccess;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;

import java.util.Random;
import java.util.Set;

/**
 * A specialized tree decorator responsible for seamlessly attaching organic bee nest structures
 * directly onto the exposed horizontal faces of the generated trunk blocks bordering foliage.
 */
public class BeeNestDecorator extends TreeDecorator {

    /**
     * The standard horizontal directional vectors evaluated during radial collision analysis.
     */
    private static final BlockFace[] FACES = {BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST};

    /**
     * The designated codec instance responsible for serializing and deserializing the state variables of this decorator.
     */
    public static final Codec<BeeNestDecorator> CODEC = RecordBuilder.create(instance -> instance.group(
        Codecs.FLOAT.fieldOf("probability").forGetter(BeeNestDecorator.class, p -> p.probability)
    ).apply(instance, BeeNestDecorator::new)).describe("BeeNestDecorator");

    /**
     * The globally registered type definition representing the bee nest tree decorator.
     */
    public static final TreeDecoratorType<BeeNestDecorator> TYPE = () -> CODEC;

    /**
     * The absolute execution probability bounding instantiation sequences across the targeted structure.
     */
    private final float probability;

    /**
     * Constructs a new bee nest placement decorator bound by a strict statistical probability parameter.
     *
     * @param probability The bounded float percentage denoting execution success chances.
     */
    public BeeNestDecorator(float probability) {
        this.probability = probability;
    }

    /**
     * Iterates the complete tracked trunk structure attempting a singular probability-bound lateral nest placement natively.
     *
     * @param level  The secure world generation interface context.
     * @param random The deterministic random source controlling probabilistic branches.
     * @param logs   The actively tracked structural anchor coordinates designating trunk placement.
     * @param leaves The actively tracked structural anchor coordinates designating foliage placement.
     */
    @Override
    public void decorate(WorldGenAccess level, Random random, Set<Location> logs, Set<Location> leaves) {
        if (random.nextFloat() >= probability) return;

        for (Location log : logs) {
            for (BlockFace face : FACES) {
                Location target = log.clone().add(face.getModX(), face.getModY(), face.getModZ());
                if (level.getType(target.getBlockX(), target.getBlockY(), target.getBlockZ()).isAir()) {
                    BlockData nestData = Material.BEE_NEST.createBlockData();
                    if (nestData instanceof Directional directional) {
                        directional.setFacing(face);
                    }
                    level.setBlock(target.getBlockX(), target.getBlockY(), target.getBlockZ(), nestData);
                    return;
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