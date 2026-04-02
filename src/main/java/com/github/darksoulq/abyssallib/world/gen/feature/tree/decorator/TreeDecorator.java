package com.github.darksoulq.abyssallib.world.gen.feature.tree.decorator;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.gen.WorldGenAccess;
import org.bukkit.Location;

import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * The base class for all tree decoration algorithms.
 * <p>
 * Decorators are the final step in the tree generation pipeline. They receive the
 * exact coordinates of all logs and leaves placed by the placers, allowing them
 * to accurately drape vines, place beehives, or alter the surrounding ground.
 */
public abstract class TreeDecorator {

    /**
     * Polymorphic codec for serializing and deserializing any tree decorator implementation.
     */
    public static final Codec<TreeDecorator> CODEC = new Codec<>() {

        /**
         * Decodes a specific TreeDecorator based on its registered type identifier.
         *
         * @param ops   The dynamic operations logic.
         * @param input The serialized input data.
         * @param <D>   The data format type.
         * @return The decoded tree decorator instance.
         * @throws CodecException If the type is missing or unknown.
         */
        @Override
        public <D> TreeDecorator decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map for TreeDecorator"));
            D typeNode = map.get(ops.createString("type"));
            if (typeNode == null) {
                throw new CodecException("Missing 'type'");
            }

            String typeId = ops.getStringValue(typeNode).orElseThrow(() -> new CodecException("Invalid type value"));
            TreeDecoratorType<?> type = Registries.TREE_DECORATORS.get(typeId);
            if (type == null) {
                throw new CodecException("Unknown tree decorator type: " + typeId);
            }

            return type.codec().decode(ops, input);
        }

        /**
         * Encodes a TreeDecorator, injecting its registered type ID into the resulting data.
         *
         * @param ops   The dynamic operations logic.
         * @param value The decorator instance to encode.
         * @param <D>   The data format type.
         * @return The encoded data object.
         * @throws CodecException If the decorator type is not registered.
         */
        @Override
        @SuppressWarnings("unchecked")
        public <D> D encode(DynamicOps<D> ops, TreeDecorator value) throws CodecException {
            TreeDecoratorType<TreeDecorator> type = (TreeDecoratorType<TreeDecorator>) value.getType();
            String typeId = Registries.TREE_DECORATORS.getId(type);
            if (typeId == null) {
                throw new CodecException("Unregistered tree decorator type");
            }

            D encoded = type.codec().encode(ops, value);
            Map<D, D> map = ops.getMap(encoded).orElseThrow(() -> new CodecException("Decorator codec must return a map"));
            map.put(ops.createString("type"), ops.createString(typeId));
            return ops.createMap(map);
        }
    };

    /**
     * Executes the decoration logic upon the fully generated tree.
     *
     * @param level  The world generation accessor.
     * @param random The deterministic random source.
     * @param logs   The set of absolute coordinates where trunk blocks were placed.
     * @param leaves The set of absolute coordinates where foliage blocks were placed.
     */
    public abstract void decorate(WorldGenAccess level, Random random, Set<Location> logs, Set<Location> leaves);

    /**
     * Retrieves the specific type definition for this decorator.
     *
     * @return The tree decorator type associated with this instance.
     */
    public abstract TreeDecoratorType<?> getType();
}