package com.github.darksoulq.abyssallib.world.gen.placement.modifier;

import com.github.darksoulq.abyssallib.common.serialization.BlockInfo;
import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.ExtraCodecs;
import com.github.darksoulq.abyssallib.common.serialization.RecordBuilder;
import com.github.darksoulq.abyssallib.world.gen.internal.WorldGenUtils;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementContext;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementModifier;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementModifierType;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.stream.Stream;

/**
 * A placement modifier that filters out incoming positions based on the target block type.
 */
public class BlockFilterModifier extends PlacementModifier {

    /**
     * The codec used for serializing and deserializing the block filter modifier.
     */
    public static final Codec<BlockFilterModifier> CODEC = RecordBuilder.create(instance -> instance.group(
        ExtraCodecs.BLOCK_INFO.list().fieldOf("targets").forGetter(BlockFilterModifier.class, p -> p.targets),
        Codecs.VECTOR_I.fieldOf("offset").forGetter(BlockFilterModifier.class, p -> p.offset)
    ).apply(instance, BlockFilterModifier::new)).describe("BlockFilterModifier");

    /**
     * The registered type definition for the block filter placement modifier.
     */
    public static final PlacementModifierType<BlockFilterModifier> TYPE = () -> CODEC;

    /** The list of allowed block info targets. */
    private final List<BlockInfo> targets;

    /** The coordinate offset to check relative to the current placement position. */
    private final Vector offset;

    /**
     * Constructs a new BlockFilterModifier.
     *
     * @param targets The list of allowed block information targets.
     * @param offset  The relative offset to apply before checking the block state.
     */
    public BlockFilterModifier(List<BlockInfo> targets, Vector offset) {
        this.targets = targets;
        this.offset = offset;
    }

    /**
     * Filters the incoming positions by checking if the targeted block matches the allowed list.
     *
     * @param context   The current placement context.
     * @param positions The incoming stream of potential placement vectors.
     * @return A filtered stream containing only valid vectors.
     */
    @Override
    public Stream<Vector> getPositions(PlacementContext context, Stream<Vector> positions) {
        return positions.filter(pos -> {
            Location checkLoc = context.toLocation(pos.clone().add(offset));
            return WorldGenUtils.isValidBlock(context.level(), checkLoc, targets);
        });
    }

    /**
     * Retrieves the specific type definition for this modifier.
     *
     * @return The placement modifier type associated with this block filter modifier.
     */
    @Override
    public PlacementModifierType<?> getType() {
        return TYPE;
    }
}