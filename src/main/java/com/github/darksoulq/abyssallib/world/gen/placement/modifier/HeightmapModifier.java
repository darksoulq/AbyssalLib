package com.github.darksoulq.abyssallib.world.gen.placement.modifier;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementContext;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementModifier;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementModifierType;
import org.bukkit.HeightMap;
import org.bukkit.util.Vector;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

/**
 * A placement modifier that adjusts the Y-coordinate of positions to match a specific world heightmap.
 * <p>
 * This modifier is essential for surface-level generation, allowing features to dynamically
 * follow the terrain by querying the highest block at a given X/Z coordinate according
 * to logic like {@link HeightMap#WORLD_SURFACE} or {@link HeightMap#MOTION_BLOCKING}.
 */
public class HeightmapModifier extends PlacementModifier {

    /**
     * The codec used for serializing and deserializing the heightmap modifier.
     */
    public static final Codec<HeightmapModifier> CODEC = new Codec<>() {
        /**
         * Decodes a HeightmapModifier from the provided serialized data.
         *
         * @param ops   The dynamic operations logic.
         * @param input The serialized input data.
         * @param <D>   The data format type.
         * @return A new instance of {@link HeightmapModifier}.
         * @throws CodecException If the "heightmap" string is missing or not a valid HeightMap enum value.
         */
        @Override
        public <D> HeightmapModifier decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
            String type = Codecs.STRING.decode(ops, map.get(ops.createString("heightmap")));
            try {
                return new HeightmapModifier(HeightMap.valueOf(type));
            } catch (IllegalArgumentException e) {
                throw new CodecException("Invalid heightmap type: " + type);
            }
        }

        /**
         * Encodes the heightmap modifier into a serialized map.
         *
         * @param ops   The dynamic operations logic.
         * @param value The modifier instance to encode.
         * @param <D>   The data format type.
         * @return A map containing the name of the heightmap type.
         * @throws CodecException If serialization fails.
         */
        @Override
        public <D> D encode(DynamicOps<D> ops, HeightmapModifier value) throws CodecException {
            Map<D, D> map = Collections.singletonMap(
                ops.createString("heightmap"),
                Codecs.STRING.encode(ops, value.heightMap.name())
            );
            return ops.createMap(map);
        }
    };

    /**
     * The registered type definition for the heightmap placement modifier.
     */
    public static final PlacementModifierType<HeightmapModifier> TYPE = () -> CODEC;

    /** The specific heightmap criteria used to determine the Y-coordinate. */
    private final HeightMap heightMap;

    /**
     * Constructs a new HeightmapModifier.
     *
     * @param heightMap The {@link HeightMap} type to use for elevation snapping.
     */
    public HeightmapModifier(HeightMap heightMap) {
        this.heightMap = heightMap;
    }

    /**
     * Transforms each position in the stream to the height determined by the heightmap.
     * <p>
     * For every incoming vector, the X and Z coordinates are used to query the world's
     * highest block. The resulting Y-coordinate replaces the original Y-value in a
     * new vector instance.
     *
     * @param context   The current {@link PlacementContext}.
     * @param positions The incoming stream of potential placement vectors.
     * @return A stream of vectors adjusted to the world's surface.
     */
    @Override
    public Stream<Vector> getPositions(PlacementContext context, Stream<Vector> positions) {
        return positions.map(pos -> {
            int y = context.level().getWorld().getHighestBlockYAt(pos.getBlockX(), pos.getBlockZ(), heightMap);
            return new Vector(pos.getX(), y, pos.getZ());
        });
    }

    /**
     * Retrieves the specific type definition for this modifier.
     *
     * @return The {@link PlacementModifierType} associated with {@link HeightmapModifier}.
     */
    @Override
    public PlacementModifierType<?> getType() {
        return TYPE;
    }
}