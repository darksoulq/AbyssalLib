package com.github.darksoulq.abyssallib.world.gen.placement.modifier;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.RecordBuilder;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementContext;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementModifier;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementModifierType;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import java.util.stream.Stream;

/**
 * A placement modifier that filters positions based on the depth of the water column above them.
 * <p>
 * This modifier is strictly utilized for aquatic feature generation (such as seagrass,
 * kelp, or coral), ensuring that the target placement block is submerged under a specific
 * maximum depth of contiguous water.
 */
public class WaterDepthFilterModifier extends PlacementModifier {

    /**
     * The codec used for serializing and deserializing the water depth filter modifier.
     */
    public static final Codec<WaterDepthFilterModifier> CODEC = RecordBuilder.create(instance -> instance.group(
        Codecs.INT.fieldOf("max_depth").forGetter(WaterDepthFilterModifier.class, p -> p.maxDepth)
    ).apply(instance, WaterDepthFilterModifier::new)).describe("WaterDepthFilterModifier");

    /**
     * The registered type definition for the water depth filter placement modifier.
     */
    public static final PlacementModifierType<WaterDepthFilterModifier> TYPE = () -> CODEC;

    /**
     * The maximum allowed number of contiguous water blocks directly above the position.
     */
    private final int maxDepth;

    /**
     * Constructs a new WaterDepthFilterModifier.
     *
     * @param maxDepth The maximum tolerable water depth.
     */
    public WaterDepthFilterModifier(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    /**
     * Filters the incoming positions by scanning the Y-axis upward to count contiguous water.
     *
     * @param context   The current placement context.
     * @param positions The incoming stream of potential placement vectors.
     * @return A filtered stream containing only vectors submerged within the allowed depth limits.
     */
    @Override
    public Stream<Vector> getPositions(PlacementContext context, Stream<Vector> positions) {
        return positions.filter(pos -> {
            int depth = 0;
            int x = pos.getBlockX();
            int y = pos.getBlockY();
            int z = pos.getBlockZ();

            while (y + depth < context.getHeight()) {
                Material mat = context.level().getType(x, y + depth, z);

                if (mat == Material.WATER) {
                    depth++;
                    if (depth > maxDepth) {
                        return false;
                    }
                } else {
                    break;
                }
            }
            return true;
        });
    }

    /**
     * Retrieves the specific type definition for this modifier.
     *
     * @return The placement modifier type associated with this water depth filter modifier.
     */
    @Override
    public PlacementModifierType<?> getType() {
        return TYPE;
    }
}