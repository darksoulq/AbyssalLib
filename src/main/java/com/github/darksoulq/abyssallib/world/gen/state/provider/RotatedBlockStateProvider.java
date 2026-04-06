package com.github.darksoulq.abyssallib.world.gen.state.provider;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.darksoulq.abyssallib.common.serialization.BlockInfo;
import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import org.bukkit.Axis;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;

import java.util.HashMap;
import java.util.Map;
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
    public static final Codec<RotatedBlockStateProvider> CODEC = new Codec<>() {

        /**
         * Decodes the provider from a serialized map.
         *
         * @param ops   The dynamic operations logic.
         * @param input The serialized input.
         * @param <D>   The data format type.
         * @return A new instance of the rotated block state provider.
         * @throws CodecException If the base provider is missing.
         */
        @Override
        public <D> RotatedBlockStateProvider decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));

            BlockStateProvider baseProvider = BlockStateProvider.CODEC.decode(ops, map.get(ops.createString("base_provider")));

            Axis axis = null;
            D axisNode = map.get(ops.createString("axis"));
            if (axisNode != null) {
                axis = Codec.enumCodec(Axis.class).decode(ops, axisNode);
            }

            BlockFace facing = null;
            D facingNode = map.get(ops.createString("facing"));
            if (facingNode != null) {
                facing = Codec.enumCodec(BlockFace.class).decode(ops, facingNode);
            }

            Integer rotation = null;
            D rotationNode = map.get(ops.createString("rotation"));
            if (rotationNode != null) {
                rotation = Codecs.INT.decode(ops, rotationNode);
            }

            return new RotatedBlockStateProvider(baseProvider, axis, facing, rotation);
        }

        /**
         * Encodes the provider into a serialized map.
         *
         * @param ops   The dynamic operations logic.
         * @param value The provider instance to encode.
         * @param <D>   The data format type.
         * @return The encoded data object.
         * @throws CodecException If serialization fails.
         */
        @Override
        public <D> D encode(DynamicOps<D> ops, RotatedBlockStateProvider value) throws CodecException {
            Map<D, D> map = new HashMap<>();

            map.put(ops.createString("base_provider"), BlockStateProvider.CODEC.encode(ops, value.baseProvider));

            if (value.axis != null) {
                map.put(ops.createString("axis"), Codec.enumCodec(Axis.class).encode(ops, value.axis));
            }
            if (value.facing != null) {
                map.put(ops.createString("facing"), Codec.enumCodec(BlockFace.class).encode(ops, value.facing));
            }
            if (value.rotation != null) {
                map.put(ops.createString("rotation"), Codecs.INT.encode(ops, value.rotation));
            }

            return ops.createMap(map);
        }
    };

    /**
     * The registered type definition for the rotated block state provider.
     */
    public static final BlockStateProviderType<RotatedBlockStateProvider> TYPE = () -> CODEC;

    /** The underlying provider supplying the base block. */
    private final BlockStateProvider baseProvider;

    /** The forced axis alignment (e.g., for logs). */
    private final Axis axis;

    /** The forced directional facing (e.g., for stairs, chests, vines). */
    private final BlockFace facing;

    /** The forced 16-point rotation (e.g., for signs, banners, skulls). */
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

        ObjectNode statesNode = base.states();
        if (statesNode == null) {
            statesNode = JsonNodeFactory.instance.objectNode();
        } else {
            statesNode = statesNode.deepCopy();
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