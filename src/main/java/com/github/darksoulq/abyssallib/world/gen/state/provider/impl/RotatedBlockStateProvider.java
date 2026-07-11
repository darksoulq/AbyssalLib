package com.github.darksoulq.abyssallib.world.gen.state.provider.impl;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.darksoulq.abyssallib.common.serialization.BlockInfo;
import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.RecordBuilder;
import com.github.darksoulq.abyssallib.world.gen.state.provider.BlockStateProvider;
import com.github.darksoulq.abyssallib.world.gen.state.provider.BlockStateProviderType;
import org.bukkit.Axis;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;

import java.util.Random;

/**
 * A block state provider wrapper that intercepts the provided block state and rigidly
 * forces its orientation properties (Axis, Facing, or 16-point Rotation).
 * <p>
 * This is incredibly useful for ensuring branches always face outward, stairs always
 * form roofs properly, or hanging vines are forced to a specific block face.
 */
public class RotatedBlockStateProvider extends BlockStateProvider {

    /**
     * The codec used for serializing and deserializing the rotated block state provider.
     */
    public static final Codec<RotatedBlockStateProvider> CODEC = RecordBuilder.create(instance -> instance.group(
        BlockStateProvider.CODEC.fieldOf("base_provider").forGetter(RotatedBlockStateProvider.class, p -> p.baseProvider),
        Codec.enumCodec(Axis.class).nullable().optionalFieldOf("axis", null).forGetter(RotatedBlockStateProvider.class, p -> p.axis),
        Codec.enumCodec(BlockFace.class).nullable().optionalFieldOf("facing", null).forGetter(RotatedBlockStateProvider.class, p -> p.facing),
        Codecs.INT.nullable().optionalFieldOf("rotation", null).forGetter(RotatedBlockStateProvider.class, p -> p.rotation)
    ).apply(instance, RotatedBlockStateProvider::new)).describe("RotatedBlockStateProvider");

    /**
     * The registered type definition for the rotated block state provider.
     */
    public static final BlockStateProviderType<RotatedBlockStateProvider> TYPE = () -> CODEC;

    /**
     * The underlying provider supplying the base block.
     */
    private final BlockStateProvider baseProvider;

    /**
     * The forced axis alignment (e.g., for logs).
     */
    private final Axis axis;

    /**
     * The forced directional facing (e.g., for stairs, chests, vines).
     */
    private final BlockFace facing;

    /**
     * The forced 16-point rotation (e.g., for signs, banners, skulls).
     */
    private final Integer rotation;

    /**
     * Constructs a new RotatedBlockStateProvider.
     *
     * @param baseProvider The origin provider.
     * @param axis         The axis to force upon the block state (nullable).
     * @param facing       The direction to force upon the block state (nullable).
     * @param rotation     The 0-15 rotation integer to force (nullable).
     */
    public RotatedBlockStateProvider(BlockStateProvider baseProvider, Axis axis, BlockFace facing, Integer rotation) {
        this.baseProvider = baseProvider;
        this.axis = axis;
        this.facing = facing;
        this.rotation = rotation;
    }

    /**
     * Retrieves the base state and forcefully injects the defined orientation properties.
     *
     * @param random   The random source.
     * @param location The placement location.
     * @return The modified block info with the injected states.
     */
    @Override
    public BlockInfo getState(Random random, Location location) {
        BlockInfo base = baseProvider.getState(random, location);
        if (base == null) return null;

        Object statesObj = base.states();
        ObjectNode statesNode;

        if (statesObj instanceof ObjectNode on) {
            statesNode = on.deepCopy();
        } else {
            statesNode = JsonNodeFactory.instance.objectNode();
        }

        if (axis != null) {
            statesNode.put("axis", axis.name().toLowerCase());
        }
        if (facing != null) {
            statesNode.put("facing", facing.name().toLowerCase());
        }
        if (rotation != null) {
            statesNode.put("rotation", String.valueOf(rotation));
        }

        return new BlockInfo(base.pos(), base.block(), statesNode, base.properties(), base.nbt());
    }

    /**
     * Retrieves the specific type definition for this provider.
     *
     * @return The block state provider type.
     */
    @Override
    public BlockStateProviderType<?> getType() {
        return TYPE;
    }
}