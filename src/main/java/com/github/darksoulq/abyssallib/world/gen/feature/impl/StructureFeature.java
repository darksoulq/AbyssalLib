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

public class StructureFeature extends Feature<StructureFeature.Config> {

    public StructureFeature() {
        super(Config.CODEC);
    }

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

    public record Config(String structureId, StructureRotation rotation, Mirror mirror, float integrity) implements FeatureConfig {
        public static final Codec<Config> CODEC = new Codec<>() {
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