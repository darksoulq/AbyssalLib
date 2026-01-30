package com.github.darksoulq.abyssallib.world.gen.placement;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.stream.Stream;

/**
 * The base class for all placement logic transformations.
 * <p>
 * Placement modifiers take an input stream of {@link Vector} positions and return
 * a new stream, allowing for operations such as:
 * <ul>
 * <li><b>Count:</b> Duplicating positions to attempt generation multiple times.</li>
 * <li><b>Height:</b> Offsetting the Y-coordinate (e.g., uniform or trapezoidal).</li>
 * <li><b>Biomes:</b> Filtering out positions that are not in valid biomes.</li>
 * </ul>
 */
public abstract class PlacementModifier {

    /**
     * Polymorphic codec for serializing and deserializing any placement modifier.
     * <p>
     * It uses the "type" field to resolve the specific implementation from
     * {@link Registries#PLACEMENT_MODIFIERS}.
     */
    public static final Codec<PlacementModifier> CODEC = new Codec<>() {
        /**
         * Decodes a specific PlacementModifier based on its registered type ID.
         *
         * @param ops   The dynamic operations logic.
         * @param input The serialized input data.
         * @return The decoded {@link PlacementModifier} instance.
         * @throws CodecException If the type is missing or unknown.
         */
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

        /**
         * Encodes a PlacementModifier, injecting its registered type ID into the result.
         *
         * @param ops   The dynamic operations logic.
         * @param value The modifier instance to encode.
         * @return The encoded data object.
         * @throws CodecException If the modifier type is not registered.
         */
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

    /**
     * Transforms the stream of positions according to the modifier logic.
     *
     * @param context   The current {@link PlacementContext}.
     * @param positions The input stream of potential placement vectors.
     * @return A modified stream of vectors.
     */
    public abstract Stream<Vector> getPositions(PlacementContext context, Stream<Vector> positions);

    /**
     * @return The {@link PlacementModifierType} associated with this modifier.
     */
    public abstract PlacementModifierType<?> getType();
}