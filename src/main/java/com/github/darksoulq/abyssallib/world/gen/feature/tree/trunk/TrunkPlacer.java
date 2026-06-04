package com.github.darksoulq.abyssallib.world.gen.feature.tree.trunk;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.gen.WorldGenAccess;
import com.github.darksoulq.abyssallib.world.gen.state.provider.BlockStateProvider;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Random;

/**
 * The base class for all tree trunk generation algorithms.
 * <p>
 * A TrunkPlacer is responsible for building the wooden core of a tree and
 * returning a list of attachment points where foliage should subsequently be generated.
 */
public abstract class TrunkPlacer {

    /**
     * Polymorphic codec for serializing and deserializing any trunk placer implementation.
     */
    public static final Codec<TrunkPlacer> CODEC = Codec.dispatch(
        TrunkPlacer.class,
        "type",
        Codecs.STRING,
        placer -> {
            String typeId = Registries.TRUNK_PLACERS.getId(placer.getType());
            if (typeId == null) {
                throw new IllegalStateException("Unregistered trunk placer type");
            }
            return typeId;
        },
        typeId -> {
            TrunkPlacerType<?> type = Registries.TRUNK_PLACERS.get(typeId);
            if (type == null) {
                return Codec.error("Unknown trunk placer type: " + typeId);
            }
            return type.codec().unchecked();
        }
    ).describe("TrunkPlacer");

    /**
     * Executes the trunk generation logic.
     *
     * @param level         The world generation accessor.
     * @param random        The deterministic random source.
     * @param origin        The base starting location of the tree.
     * @param trunkProvider The block state provider for the trunk material.
     * @param height        The calculated total height for this specific tree instance.
     * @return A list of vectors representing the locations where foliage should be attached.
     */
    public abstract List<Vector> placeTrunk(WorldGenAccess level, Random random, Location origin, BlockStateProvider trunkProvider, int height);

    /**
     * Retrieves the specific type definition for this trunk placer.
     *
     * @return The trunk placer type associated with this instance.
     */
    public abstract TrunkPlacerType<?> getType();
}