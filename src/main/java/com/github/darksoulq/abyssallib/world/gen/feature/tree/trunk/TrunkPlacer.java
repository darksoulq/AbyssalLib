package com.github.darksoulq.abyssallib.world.gen.feature.tree.trunk;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.gen.WorldGenAccess;
import com.github.darksoulq.abyssallib.world.gen.state.provider.BlockStateProvider;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * The base class for all tree trunk generation algorithms.
 * <p>
 * A TrunkPlacer is responsible for building the wooden core of a tree and
 * returning a list of attachment points where foliage should subsequently be generated.
 */
public abstract class TrunkPlacer {

    /**
     * Polymorphic codec for serializing and deserializing any trunk placer implementation.
     */
    public static final Codec<TrunkPlacer> CODEC = new Codec<>() {

        /**
         * Decodes a specific TrunkPlacer based on its registered type identifier.
         *
         * @param ops   The dynamic operations logic.
         * @param input The serialized input data.
         * @param <D>   The data format type.
         * @return The decoded trunk placer instance.
         * @throws CodecException If the type is missing or unknown.
         */
        @Override
        public <D> TrunkPlacer decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map for TrunkPlacer"));
            D typeNode = map.get(ops.createString("type"));
            if (typeNode == null) {
                throw new CodecException("Missing 'type'");
            }

            String typeId = ops.getStringValue(typeNode).orElseThrow(() -> new CodecException("Invalid type value"));
            TrunkPlacerType<?> type = Registries.TRUNK_PLACERS.get(typeId);
            if (type == null) {
                throw new CodecException("Unknown trunk placer type: " + typeId);
            }

            return type.codec().decode(ops, input);
        }

        /**
         * Encodes a TrunkPlacer, injecting its registered type ID into the resulting data.
         *
         * @param ops   The dynamic operations logic.
         * @param value The trunk placer instance to encode.
         * @param <D>   The data format type.
         * @return The encoded data object.
         * @throws CodecException If the trunk placer type is not registered.
         */
        @Override
        @SuppressWarnings("unchecked")
        public <D> D encode(DynamicOps<D> ops, TrunkPlacer value) throws CodecException {
            TrunkPlacerType<TrunkPlacer> type = (TrunkPlacerType<TrunkPlacer>) value.getType();
            String typeId = Registries.TRUNK_PLACERS.getId(type);
            if (typeId == null) {
                throw new CodecException("Unregistered trunk placer type");
            }

            D encoded = type.codec().encode(ops, value);
            Map<D, D> map = ops.getMap(encoded).orElseThrow(() -> new CodecException("Trunk placer codec must return a map"));
            map.put(ops.createString("type"), ops.createString(typeId));
            return ops.createMap(map);
        }
    };

    /**
     * Executes the trunk generation logic.
     *
     * @param level         The world generation accessor.
     * @param random        The deterministic random source.
     * @param origin        The base starting location of the tree.
     * @param trunkProvider The block state provider for the trunk material.
     * @param height        The calculated total height for this specific tree instance.
     * @return A list of vectors representing the locations where foliage should be attached.
     */
    public abstract List<Vector> placeTrunk(WorldGenAccess level, Random random, Location origin, BlockStateProvider trunkProvider, int height);

    /**
     * Retrieves the specific type definition for this trunk placer.
     *
     * @return The trunk placer type associated with this instance.
     */
    public abstract TrunkPlacerType<?> getType();
}