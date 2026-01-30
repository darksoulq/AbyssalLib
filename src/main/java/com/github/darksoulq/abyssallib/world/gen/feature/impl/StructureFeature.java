package com.github.darksoulq.abyssallib.world.gen.feature.impl;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.gen.feature.Feature;
import com.github.darksoulq.abyssallib.world.gen.feature.FeatureConfig;
import com.github.darksoulq.abyssallib.world.gen.feature.FeaturePlaceContext;
import com.github.darksoulq.abyssallib.world.structure.Structure;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;

import java.util.HashMap;
import java.util.Map;

/**
 * A world generation feature that places a predefined structure into the world.
 * <p>
 * This feature acts as a wrapper for the {@link Structure} system, allowing
 * complex multi-block objects to be spawned during the generation phase with
 * configurable transformations and block integrity.
 */
public class StructureFeature extends Feature<StructureFeature.Config> {

    /**
     * Constructs a new StructureFeature with the associated configuration codec.
     */
    public StructureFeature() {
        super(Config.CODEC);
    }

    /**
     * Executes the placement of the structure.
     * <p>
     * The method resolves the structure from the {@link Registries#STRUCTURES} using
     * the ID provided in the configuration. If found, it delegates the placement
     * logic to the structure instance, applying the configured rotation, mirror,
     * and integrity settings.
     *
     * @param context The {@link FeaturePlaceContext} providing world access, origin, and configuration.
     * @return {@code true} if the structure was successfully resolved and placed; {@code false} otherwise.
     */
    @Override
    public boolean place(FeaturePlaceContext<Config> context) {
        String id = context.config().structureId();
        Structure structure = Registries.STRUCTURES.get(id);

        if (structure == null) return false;

        structure.place(
            context.level(),
            context.origin(),
            context.config().rotation(),
            context.config().mirror(),
            context.config().integrity()
        );
        return true;
    }

    /**
     * Configuration record for {@link StructureFeature}.
     *
     * @param structureId The namespaced identifier of the structure to place.
     * @param rotation    The {@link StructureRotation} to apply to the structure.
     * @param mirror      The {@link Mirror} transformation to apply.
     * @param integrity   The probability (0.0 to 1.0) that each block in the structure will be placed.
     */
    public record Config(String structureId, StructureRotation rotation, Mirror mirror, float integrity) implements FeatureConfig {

        /**
         * The codec for serializing and deserializing the structure configuration.
         */
        public static final Codec<Config> CODEC = new Codec<>() {

            /**
             * Decodes the configuration from a map structure.
             *
             * @param ops   The dynamic operations logic.
             * @param input The serialized input.
             * @param <D>   The data format type.
             * @return A new {@link Config} instance.
             * @throws CodecException If the ID is missing or transformations are invalid.
             */
            @Override
            public <D> Config decode(DynamicOps<D> ops, D input) throws CodecException {
                Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));

                String id = Codecs.STRING.decode(ops, map.get(ops.createString("id")));
                StructureRotation rot = Codec.enumCodec(StructureRotation.class).orElse(StructureRotation.NONE)
                    .decode(ops, map.get(ops.createString("rotation")));
                Mirror mir = Codec.enumCodec(Mirror.class).orElse(Mirror.NONE)
                    .decode(ops, map.get(ops.createString("mirror")));

                float integ = 1.0f;
                if (map.containsKey(ops.createString("integrity"))) {
                    integ = Codecs.FLOAT.decode(ops, map.get(ops.createString("integrity")));
                }

                return new Config(id, rot, mir, integ);
            }

            /**
             * Encodes the configuration into a map structure.
             *
             * @param ops   The dynamic operations logic.
             * @param value The configuration instance to encode.
             * @param <D>   The data format type.
             * @return The encoded data object.
             * @throws CodecException If serialization fails.
             */
            @Override
            public <D> D encode(DynamicOps<D> ops, Config value) throws CodecException {
                Map<D, D> map = new HashMap<>();
                map.put(ops.createString("id"), Codecs.STRING.encode(ops, value.structureId));
                map.put(ops.createString("rotation"), Codec.enumCodec(StructureRotation.class).encode(ops, value.rotation));
                map.put(ops.createString("mirror"), Codec.enumCodec(Mirror.class).encode(ops, value.mirror));
                map.put(ops.createString("integrity"), Codecs.FLOAT.encode(ops, value.integrity));
                return ops.createMap(map);
            }
        };
    }
}