package com.github.darksoulq.abyssallib.world.gen.placement;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.stream.Stream;

public abstract class PlacementModifier {

    public static final Codec<PlacementModifier> CODEC = new Codec<>() {
        @Override
        public <D> PlacementModifier decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map for PlacementModifier"));
            D typeNode = map.get(ops.createString("type"));
            if (typeNode == null) throw new CodecException("Missing 'type'");

            String typeId = ops.getStringValue(typeNode).orElseThrow(() -> new CodecException("Invalid type value"));
            PlacementModifierType<?> type = Registries.PLACEMENT_MODIFIERS.get(typeId);
            if (type == null) throw new CodecException("Unknown placement modifier type: " + typeId);

            return type.codec().decode(ops, input);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <D> D encode(DynamicOps<D> ops, PlacementModifier value) throws CodecException {
            PlacementModifierType<PlacementModifier> type = (PlacementModifierType<PlacementModifier>) value.getType();
            String typeId = Registries.PLACEMENT_MODIFIERS.getId(type);
            if (typeId == null) throw new CodecException("Unregistered placement modifier type");

            D encoded = type.codec().encode(ops, value);
            Map<D, D> map = ops.getMap(encoded).orElseThrow(() -> new CodecException("Modifier codec must return a map"));
            map.put(ops.createString("type"), ops.createString(typeId));
            return ops.createMap(map);
        }
    };

    public abstract Stream<Vector> getPositions(PlacementContext context, Stream<Vector> positions);

    public abstract PlacementModifierType<?> getType();
}