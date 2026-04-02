package com.github.darksoulq.abyssallib.world.gen.feature.tree.root;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.gen.WorldGenAccess;
import com.github.darksoulq.abyssallib.world.gen.state.provider.BlockStateProvider;
import org.bukkit.Location;

import java.util.Map;
import java.util.Random;

/**
 * The base class for all complex root generation algorithms.
 */
public abstract class RootPlacer {

    /**
     * Polymorphic codec for serializing and deserializing any root placer implementation.
     */
    public static final Codec<RootPlacer> CODEC = new Codec<>() {

        /**
         * Decodes a specific RootPlacer based on its registered type identifier.
         *
         * @param ops   The dynamic operations logic.
         * @param input The serialized input data.
         * @param <D>   The data format type.
         * @return The decoded root placer instance.
         * @throws CodecException If the type is missing or unknown.
         */
        @Override
        public <D> RootPlacer decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map for RootPlacer"));
            D typeNode = map.get(ops.createString("type"));
            if (typeNode == null) {
                throw new CodecException("Missing 'type'");
            }

            String typeId = ops.getStringValue(typeNode).orElseThrow(() -> new CodecException("Invalid type value"));
            RootPlacerType<?> type = Registries.ROOT_PLACERS.get(typeId);
            if (type == null) {
                throw new CodecException("Unknown root placer type: " + typeId);
            }

            return type.codec().decode(ops, input);
        }

        /**
         * Encodes a RootPlacer, injecting its registered type ID into the resulting data.
         *
         * @param ops   The dynamic operations logic.
         * @param value The root placer instance to encode.
         * @param <D>   The data format type.
         * @return The encoded data object.
         * @throws CodecException If the root placer type is not registered.
         */
        @Override
        @SuppressWarnings("unchecked")
        public <D> D encode(DynamicOps<D> ops, RootPlacer value) throws CodecException {
            RootPlacerType<RootPlacer> type = (RootPlacerType<RootPlacer>) value.getType();
            String typeId = Registries.ROOT_PLACERS.getId(type);
            if (typeId == null) {
                throw new CodecException("Unregistered root placer type");
            }

            D encoded = type.codec().encode(ops, value);
            Map<D, D> map = ops.getMap(encoded).orElseThrow(() -> new CodecException("Root placer codec must return a map"));
            map.put(ops.createString("type"), ops.createString(typeId));
            return ops.createMap(map);
        }
    };

    /**
     * Executes the root generation logic.
     *
     * @param level        The world generation accessor.
     * @param random       The deterministic random source.
     * @param origin       The requested base location of the tree.
     * @param rootProvider The provider defining the material of the roots.
     * @param dirtProvider The provider to optionally enforce the ground beneath the roots.
     * @return The new, adjusted origin location where the TrunkPlacer should begin.
     */
    public abstract Location placeRoots(WorldGenAccess level, Random random, Location origin, BlockStateProvider rootProvider, BlockStateProvider dirtProvider);

    /**
     * Retrieves the specific type definition for this root placer.
     *
     * @return The root placer type associated with this instance.
     */
    public abstract RootPlacerType<?> getType();
}