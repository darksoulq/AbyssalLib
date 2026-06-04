package com.github.darksoulq.abyssallib.world.gen.placement.modifier;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.RecordBuilder;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementContext;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementModifier;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementModifierType;
import org.bukkit.util.Vector;

import java.util.stream.Stream;

/**
 * A placement modifier that slightly shifts the incoming position randomly
 * along all three axes based on the defined spread values.
 * <p>
 * This is commonly used in conjunction with the InSquareModifier to add vertical
 * or localized horizontal scattering to features.
 */
public class RandomOffsetModifier extends PlacementModifier {

    /**
     * The codec used for serializing and deserializing the random offset modifier.
     */
    public static final Codec<RandomOffsetModifier> CODEC = RecordBuilder.create(instance -> instance.group(
        Codecs.INT.fieldOf("xz_spread").forGetter(RandomOffsetModifier.class, p -> p.xzSpread),
        Codecs.INT.fieldOf("y_spread").forGetter(RandomOffsetModifier.class, p -> p.ySpread)
    ).apply(instance, RandomOffsetModifier::new)).describe("RandomOffsetModifier");

    /**
     * The registered type definition for the random offset placement modifier.
     */
    public static final PlacementModifierType<RandomOffsetModifier> TYPE = () -> CODEC;

    /** The maximum horizontal distance (positive or negative) to offset the position. */
    private final int xzSpread;

    /** The maximum vertical distance (positive or negative) to offset the position. */
    private final int ySpread;

    /**
     * Constructs a new RandomOffsetModifier.
     *
     * @param xzSpread The bounds for X and Z coordinate randomization.
     * @param ySpread  The bounds for Y coordinate randomization.
     */
    public RandomOffsetModifier(int xzSpread, int ySpread) {
        this.xzSpread = Math.max(0, xzSpread);
        this.ySpread = Math.max(0, ySpread);
    }

    /**
     * Shifts each incoming position by a random amount within the configured spread bounds.
     *
     * @param context   The current placement context.
     * @param positions The incoming stream of potential placement vectors.
     * @return A stream of vectors shifted by the random offsets.
     */
    @Override
    public Stream<Vector> getPositions(PlacementContext context, Stream<Vector> positions) {
        return positions.map(pos -> {
            int dx = xzSpread > 0 ? context.random().nextInt(xzSpread * 2 + 1) - xzSpread : 0;
            int dy = ySpread > 0 ? context.random().nextInt(ySpread * 2 + 1) - ySpread : 0;
            int dz = xzSpread > 0 ? context.random().nextInt(xzSpread * 2 + 1) - xzSpread : 0;
            return new Vector(pos.getBlockX() + dx, pos.getBlockY() + dy, pos.getBlockZ() + dz);
        });
    }

    /**
     * Retrieves the specific type definition for this modifier.
     *
     * @return The placement modifier type associated with this random offset modifier.
     */
    @Override
    public PlacementModifierType<?> getType() {
        return TYPE;
    }
}