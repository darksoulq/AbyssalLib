package com.github.darksoulq.abyssallib.world.gen.feature.impl;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.gen.feature.Feature;
import com.github.darksoulq.abyssallib.world.gen.feature.FeatureConfig;
import com.github.darksoulq.abyssallib.world.gen.feature.FeaturePlaceContext;
import com.github.darksoulq.abyssallib.world.gen.feature.GenerationPhase;
import com.github.darksoulq.abyssallib.world.structure.Structure;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;

import java.util.HashMap;
import java.util.Map;

/**
 * A world generation feature that delegates generation logic to a pre-defined,
 * saved JSON structure file within the AbyssalLib structure registry.
 */
public class StructureFeature extends Feature<StructureFeature.Config> {

    /**
     * Constructs a new StructureFeature with its associated configuration codec.
     */
    public StructureFeature() {
        super(Config.CODEC);
    }

    /**
     * Executes the placement of the resolved structure at the origin coordinate.
     *
     * @param context The feature place context providing world access and configuration.
     * @return True if the structure was resolved and successfully initiated placement.
     */
    @Override
    public boolean place(FeaturePlaceContext<Config> context) {
        Config config = context.config();
        Structure structure = Registries.STRUCTURES.get(config.structureId());
        AbyssalLib.LOGGER.info("Placing at " + context.origin());

        if (structure == null) {
            return false;
        }

        StructureRotation finalRotation = config.randomRotation() ?
            StructureRotation.values()[context.random().nextInt(4)] :
            config.rotation();

        Mirror finalMirror = config.randomMirror() ?
            Mirror.values()[context.random().nextInt(3)] :
            config.mirror();

        structure.place(context.level(), context.origin(), finalRotation, finalMirror, 1.0f);
        return true;
    }

    /**
     * Specifies the procedural generation phase in which this feature executes.
     *
     * @return The SURFACE_STRUCTURES generation phase.
     */
    @Override
    public GenerationPhase getPhase(Config config) {
        return GenerationPhase.SURFACE_STRUCTURES;
    }

    /**
     * Configuration record for the structure feature.
     *
     * @param structureId    The namespaced registry key of the target structure.
     * @param randomRotation Flag to randomize rotation completely, overriding the configured rotation.
     * @param rotation       The explicitly defined structural rotation.
     * @param randomMirror   Flag to randomize the mirroring completely, overriding the configured mirror.
     * @param mirror         The explicitly defined structural mirror.
     */
    public record Config(String structureId, boolean randomRotation, StructureRotation rotation, boolean randomMirror, Mirror mirror) implements FeatureConfig {

        /**
         * The codec for serializing and deserializing the structure configuration.
         */
        public static final Codec<Config> CODEC = new Codec<>() {

            /**
             * Decodes the configuration from a map.
             *
             * @param ops   The dynamic operations logic.
             * @param input The serialized input.
             * @param <D>   The data format type.
             * @return A new configuration instance.
             * @throws CodecException If the required structure_id field is missing.
             */
            @Override
            public <D> Config decode(DynamicOps<D> ops, D input) throws CodecException {
                Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));

                String structureId = Codecs.STRING.decode(ops, map.get(ops.createString("structure_id")));

                boolean randomRotation = false;
                D randRotNode = map.get(ops.createString("random_rotation"));
                if (randRotNode != null) {
                    randomRotation = Codecs.BOOLEAN.decode(ops, randRotNode);
                }

                StructureRotation rotation = StructureRotation.NONE;
                D rotNode = map.get(ops.createString("rotation"));
                if (rotNode != null) {
                    rotation = Codec.enumCodec(StructureRotation.class).decode(ops, rotNode);
                }

                boolean randomMirror = false;
                D randMirrorNode = map.get(ops.createString("random_mirror"));
                if (randMirrorNode != null) {
                    randomMirror = Codecs.BOOLEAN.decode(ops, randMirrorNode);
                }

                Mirror mirror = Mirror.NONE;
                D mirrorNode = map.get(ops.createString("mirror"));
                if (mirrorNode != null) {
                    mirror = Codec.enumCodec(Mirror.class).decode(ops, mirrorNode);
                }

                return new Config(structureId, randomRotation, rotation, randomMirror, mirror);
            }

            /**
             * Encodes the configuration into a map.
             *
             * @param ops   The dynamic operations logic.
             * @param value The configuration instance.
             * @param <D>   The data format type.
             * @return The encoded data object.
             * @throws CodecException If serialization fails.
             */
            @Override
            public <D> D encode(DynamicOps<D> ops, Config value) throws CodecException {
                Map<D, D> map = new HashMap<>();
                map.put(ops.createString("structure_id"), Codecs.STRING.encode(ops, value.structureId));
                map.put(ops.createString("random_rotation"), Codecs.BOOLEAN.encode(ops, value.randomRotation));
                map.put(ops.createString("rotation"), Codec.enumCodec(StructureRotation.class).encode(ops, value.rotation));
                map.put(ops.createString("random_mirror"), Codecs.BOOLEAN.encode(ops, value.randomMirror));
                map.put(ops.createString("mirror"), Codec.enumCodec(Mirror.class).encode(ops, value.mirror));
                return ops.createMap(map);
            }
        };
    }
}