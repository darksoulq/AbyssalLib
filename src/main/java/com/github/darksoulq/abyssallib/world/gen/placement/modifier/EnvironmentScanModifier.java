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
 * A placement modifier that performs a vertical scan to find the nearest solid surface.
 * <p>
 * This modifier iterates vertically from the input position, searching for a solid block.
 * Depending on the configuration, it can scan upwards to find a ceiling or downwards
 * to find a floor, adjusting the final placement vector to sit perfectly on or under
 * that discovered surface.
 */
public class EnvironmentScanModifier extends PlacementModifier {

    /**
     * The codec used for serializing and deserializing the environment scan modifier.
     */
    public static final Codec<EnvironmentScanModifier> CODEC = RecordBuilder.create(instance -> instance.group(
        Codecs.INT.fieldOf("max_steps").forGetter(EnvironmentScanModifier.class, p -> p.maxSteps),
        Codecs.BOOLEAN.fieldOf("up").forGetter(EnvironmentScanModifier.class, p -> p.up)
    ).apply(instance, EnvironmentScanModifier::new)).describe("EnvironmentScanModifier");

    /**
     * The registered type definition for the environment scan placement modifier.
     */
    public static final PlacementModifierType<EnvironmentScanModifier> TYPE = () -> CODEC;

    /**
     * The maximum number of blocks to scan in the specified direction.
     */
    private final int maxSteps;

    /**
     * Determines the scan direction; true to scan upwards, false to scan downwards.
     */
    private final boolean up;

    /**
     * Constructs a new EnvironmentScanModifier.
     *
     * @param maxSteps The maximum range of the vertical scan.
     * @param up       True to find a ceiling, false to find a floor.
     */
    public EnvironmentScanModifier(int maxSteps, boolean up) {
        this.maxSteps = maxSteps;
        this.up = up;
    }

    /**
     * Maps each input position to the nearest empty space adjacent to a solid surface.
     * <p>
     * The method iterates through the Y-axis starting at the input coordinate. If a
     * solid block is encountered within the scanning range, the position is updated.
     * If the scan is upward, it returns the position of the air block immediately
     * below the ceiling. If downward, it returns the position immediately above the floor.
     *
     * @param context   The current placement context providing world bounds and data.
     * @param positions The incoming stream of potential placement vectors.
     * @return A stream of vectors adjusted to the discovered surfaces.
     */
    @Override
    public Stream<Vector> getPositions(PlacementContext context, Stream<Vector> positions) {
        return positions.map(pos -> {
            int x = pos.getBlockX();
            int y = pos.getBlockY();
            int z = pos.getBlockZ();
            int step = up ? 1 : -1;

            for (int i = 0; i < maxSteps; i++) {
                int checkY = y + (i * step);

                if (checkY < context.getMinBuildHeight() || checkY >= context.getHeight()) {
                    break;
                }

                Material m = context.level().getType(x, checkY, z);

                if (m.isSolid()) {
                    return new Vector(x, checkY + (up ? -1 : 1), z);
                }
            }

            return pos;
        });
    }

    /**
     * Retrieves the specific type definition for this modifier.
     *
     * @return The placement modifier type associated with this environment scan modifier.
     */
    @Override
    public PlacementModifierType<?> getType() {
        return TYPE;
    }
}