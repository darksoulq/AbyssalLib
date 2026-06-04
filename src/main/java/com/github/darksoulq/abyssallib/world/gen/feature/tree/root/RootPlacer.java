package com.github.darksoulq.abyssallib.world.gen.feature.tree.root;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.gen.WorldGenAccess;
import com.github.darksoulq.abyssallib.world.gen.state.provider.BlockStateProvider;
import org.bukkit.Location;

import java.util.Random;

/**
 * The base class for all complex root generation algorithms.
 */
public abstract class RootPlacer {

    /**
     * Polymorphic codec for serializing and deserializing any root placer implementation.
     */
    public static final Codec<RootPlacer> CODEC = Codec.dispatch(
        RootPlacer.class,
        "type",
        Codecs.STRING,
        placer -> {
            String typeId = Registries.ROOT_PLACERS.getId(placer.getType());
            if (typeId == null) {
                throw new IllegalStateException("Unregistered root placer type");
            }
            return typeId;
        },
        typeId -> {
            RootPlacerType<?> type = Registries.ROOT_PLACERS.get(typeId);
            if (type == null) {
                return Codec.error("Unknown root placer type: " + typeId);
            }
            return type.codec().unchecked();
        }
    ).describe("RootPlacer");

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