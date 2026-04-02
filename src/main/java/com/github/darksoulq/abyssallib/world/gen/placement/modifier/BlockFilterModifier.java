package com.github.darksoulq.abyssallib.world.gen.placement.modifier;

import com.github.darksoulq.abyssallib.common.serialization.*;
import com.github.darksoulq.abyssallib.world.gen.internal.WorldGenUtils;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementContext;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementModifier;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementModifierType;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * A placement modifier that filters out incoming positions based on the target block type.
 */
public class BlockFilterModifier extends PlacementModifier {

    /**
     * The codec used for serializing and deserializing the block filter modifier.
     */
    public static final Codec<BlockFilterModifier> CODEC = new Codec<>() {

        /**
         * Decodes the modifier from a serialized map.
         *
         * @param ops   The dynamic operations logic.
         * @param input The serialized input.
         * @param <D>   The data format type.
         * @return A new instance of the block filter modifier.
         * @throws CodecException If fields are missing or invalid.
         */
        @Override
        public <D> BlockFilterModifier decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
            List<BlockInfo> targets = ExtraCodecs.BLOCK_INFO.list().decode(ops, map.get(ops.createString("targets")));
            Vector offset = Codecs.VECTOR_I.decode(ops, map.get(ops.createString("offset")));
            return new BlockFilterModifier(targets, offset);
        }

        /**
         * Encodes the modifier into a serialized map.
         *
         * @param ops   The dynamic operations logic.
         * @param value The modifier instance to encode.
         * @param <D>   The data format type.
         * @return The encoded data object.
         * @throws CodecException If serialization fails.
         */
        @Override
        public <D> D encode(DynamicOps<D> ops, BlockFilterModifier value) throws CodecException {
            Map<D, D> map = new HashMap<>();
            map.put(ops.createString("targets"), ExtraCodecs.BLOCK_INFO.list().encode(ops, value.targets));
            map.put(ops.createString("offset"), Codecs.VECTOR_I.encode(ops, value.offset));
            return ops.createMap(map);
        }
    };

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