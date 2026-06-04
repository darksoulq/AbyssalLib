package com.github.darksoulq.abyssallib.world.gen.feature.tree.decorator;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.gen.WorldGenAccess;
import org.bukkit.Location;

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
    public static final Codec<TreeDecorator> CODEC = Codec.dispatch(
        TreeDecorator.class,
        "type",
        Codecs.STRING,
        decorator -> {
            String typeId = Registries.TREE_DECORATORS.getId(decorator.getType());
            if (typeId == null) {
                throw new IllegalStateException("Unregistered tree decorator type");
            }
            return typeId;
        },
        typeId -> {
            TreeDecoratorType<?> type = Registries.TREE_DECORATORS.get(typeId);
            if (type == null) {
                return Codec.error("Unknown tree decorator type: " + typeId);
            }
            return type.codec().unchecked();
        }
    ).describe("TreeDecorator");

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