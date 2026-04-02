package com.github.darksoulq.abyssallib.world.gen.placement.modifier;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementContext;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementModifier;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementModifierType;
import org.bukkit.HeightMap;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * A placement modifier that snaps the Y-coordinate of incoming positions to the
 * highest block at that specific X and Z coordinate, dictated by a selected heightmap.
 */
public class HeightmapModifier extends PlacementModifier {

    /**
     * The codec used for serializing and deserializing the heightmap modifier.
     */
    public static final Codec<HeightmapModifier> CODEC = new Codec<>() {

        /**
         * Decodes the modifier from a serialized map.
         *
         * @param ops   The dynamic operations logic.
         * @param input The serialized input.
         * @param <D>   The data format type.
         * @return A new instance of the heightmap modifier.
         * @throws CodecException If the heightmap field is missing or invalid.
         */
        @Override
        public <D> HeightmapModifier decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
            HeightMap heightmap = Codec.enumCodec(HeightMap.class).decode(ops, map.get(ops.createString("heightmap")));
            return new HeightmapModifier(heightmap);
        }

        /**
         * Encodes the modifier into a serialized map.
         *
         * @param ops   The dynamic operations logic.
         * @param value The modifier instance to encode.
         * @param <D>   The data format type.
         * @return The encoded data object.
         * @throws CodecException If serialization fails.
         */
        @Override
        public <D> D encode(DynamicOps<D> ops, HeightmapModifier value) throws CodecException {
            Map<D, D> map = new HashMap<>();
            map.put(ops.createString("heightmap"), Codec.enumCodec(HeightMap.class).encode(ops, value.heightmap));
            return ops.createMap(map);
        }
    };

    /**
     * The registered type definition for the heightmap placement modifier.
     */
    public static final PlacementModifierType<HeightmapModifier> TYPE = () -> CODEC;

    /** The heightmap projection to use when calculating the surface. */
    private final HeightMap heightmap;

    /**
     * Constructs a new HeightmapModifier.
     *
     * @param heightmap The Bukkit heightmap criteria to apply.
     */
    public HeightmapModifier(HeightMap heightmap) {
        this.heightmap = heightmap;
    }

    /**
     * Projects the Y-coordinate of each incoming position to the surface defined by the heightmap.
     *
     * @param context   The current placement context.
     * @param positions The incoming stream of potential placement vectors.
     * @return A stream of vectors mapped directly onto the heightmap surface.
     */
    @Override
    public Stream<Vector> getPositions(PlacementContext context, Stream<Vector> positions) {
        return positions.map(pos -> {
            int y = context.level().getHighestBlockY(pos.getBlockX(), pos.getBlockZ(), heightmap);
            return new Vector(pos.getBlockX(), y, pos.getBlockZ());
        });
    }

    /**
     * Retrieves the specific type definition for this modifier.
     *
     * @return The placement modifier type associated with this heightmap modifier.
     */
    @Override
    public PlacementModifierType<?> getType() {
        return TYPE;
    }
}