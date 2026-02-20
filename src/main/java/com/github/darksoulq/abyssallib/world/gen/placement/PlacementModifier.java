package com.github.darksoulq.abyssallib.world.gen.placement;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.stream.Stream;

/**
 * The base class for all placement logic transformations in the world generation pipeline.
 * Placement modifiers take an input stream of {@link Vector} positions and return
 * a new stream, allowing for operations such as duplication, coordinate offsetting,
 * or spatial filtering based on environmental conditions.
 */
public abstract class PlacementModifier {

    /**
     * Polymorphic codec for serializing and deserializing any placement modifier implementation.
     * This codec utilizes a "type" field to resolve the specific implementation from
     * the {@link Registries#PLACEMENT_MODIFIERS} registry during decoding.
     */
    public static final Codec<PlacementModifier> CODEC = new Codec<>() {
        /**
         * Decodes a specific PlacementModifier based on its registered type identifier.
         *
         * @param <D>
         * The type of the serialized data.
         * @param ops
         * The dynamic operations logic used to parse the input.
         * @param input
         * The serialized input data representing the modifier.
         * @return
         * The decoded {@link PlacementModifier} instance.
         * @throws CodecException
         * If the type is missing, unknown, or the data structure is invalid.
         */
        @Override
        public <D> PlacementModifier decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map for PlacementModifier"));
            D typeNode = map.get(ops.createString("type"));
            if (typeNode == null) {
                throw new CodecException("Missing 'type'");
            }

            String typeId = ops.getStringValue(typeNode).orElseThrow(() -> new CodecException("Invalid type value"));
            PlacementModifierType<?> type = Registries.PLACEMENT_MODIFIERS.get(typeId);
            if (type == null) {
                throw new CodecException("Unknown placement modifier type: " + typeId);
            }

            return type.codec().decode(ops, input);
        }

        /**
         * Encodes a PlacementModifier, injecting its registered type ID into the resulting data.
         *
         * @param <D>
         * The target type for the serialized data.
         * @param ops
         * The dynamic operations logic used to construct the output.
         * @param value
         * The modifier instance to encode.
         * @return
         * The encoded data object containing both modifier data and its type identifier.
         * @throws CodecException
         * If the modifier type is not registered or the codec fails to return a map.
         */
        @Override
        @SuppressWarnings("unchecked")
        public <D> D encode(DynamicOps<D> ops, PlacementModifier value) throws CodecException {
            PlacementModifierType<PlacementModifier> type = (PlacementModifierType<PlacementModifier>) value.getType();
            String typeId = Registries.PLACEMENT_MODIFIERS.getId(type);
            if (typeId == null) {
                throw new CodecException("Unregistered placement modifier type");
            }

            D encoded = type.codec().encode(ops, value);
            Map<D, D> map = ops.getMap(encoded).orElseThrow(() -> new CodecException("Modifier codec must return a map"));
            map.put(ops.createString("type"), ops.createString(typeId));
            return ops.createMap(map);
        }
    };

    /**
     * Transforms the stream of positions according to the specific modifier logic.
     *
     * @param context
     * The current {@link PlacementContext} providing world and environmental data.
     * @param positions
     * The input {@link Stream} of potential placement {@link Vector} coordinates.
     * @return
     * A modified {@link Stream} of vectors after application of the modifier logic.
     */
    public abstract Stream<Vector> getPositions(PlacementContext context, Stream<Vector> positions);

    /**
     * Retrieves the placement modifier type used for identifying this specific implementation.
     *
     * @return
     * The {@link PlacementModifierType} associated with this instance.
     */
    public abstract PlacementModifierType<?> getType();
}