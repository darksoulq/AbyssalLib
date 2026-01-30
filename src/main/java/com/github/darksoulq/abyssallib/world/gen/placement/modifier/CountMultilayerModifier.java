package com.github.darksoulq.abyssallib.world.gen.placement.modifier;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.world.gen.internal.WorldGenUtils;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementContext;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementModifier;
import com.github.darksoulq.abyssallib.world.gen.placement.PlacementModifierType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * A placement modifier that distributes feature attempts across multiple vertical layers within a column.
 * <p>
 * Unlike standard count modifiers that pick a single height, this modifier scans the entire
 * vertical column at a given X/Z coordinate to find all valid surfaces (air blocks with a
 * valid supporting block below). It then randomly selects from these identified locations.
 */
public class CountMultilayerModifier extends PlacementModifier {

    /**
     * The codec used for serializing and deserializing the multilayer count modifier.
     */
    public static final Codec<CountMultilayerModifier> CODEC = new Codec<>() {
        /**
         * Decodes the modifier from a serialized map.
         *
         * @param ops   The dynamic operations logic.
         * @param input The serialized input.
         * @param <D>   The data format type.
         * @return A new instance of {@link CountMultilayerModifier}.
         * @throws CodecException If the "count" field is missing.
         */
        @Override
        public <D> CountMultilayerModifier decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
            int count = Codecs.INT.decode(ops, map.get(ops.createString("count")));
            List<String> valid = new ArrayList<>();
            if (map.containsKey(ops.createString("valid_blocks"))) {
                valid = Codecs.STRING.list().decode(ops, map.get(ops.createString("valid_blocks")));
            }
            return new CountMultilayerModifier(count, valid);
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
        public <D> D encode(DynamicOps<D> ops, CountMultilayerModifier value) throws CodecException {
            Map<D, D> map = new HashMap<>();
            map.put(ops.createString("count"), Codecs.INT.encode(ops, value.count));
            map.put(ops.createString("valid_blocks"), Codecs.STRING.list().encode(ops, value.validBlocks));
            return ops.createMap(map);
        }
    };

    /**
     * The registered type definition for the multilayer count placement modifier.
     */
    public static final PlacementModifierType<CountMultilayerModifier> TYPE = () -> CODEC;

    /** The number of placement attempts to make per column. */
    private final int count;

    /** The list of block identifiers that are considered valid supporting surfaces. */
    private final List<String> validBlocks;

    /**
     * Constructs a new CountMultilayerModifier.
     *
     * @param count       The number of attempts to select from the discovered valid layers.
     * @param validBlocks A list of valid surface block identifiers.
     */
    public CountMultilayerModifier(int count, List<String> validBlocks) {
        this.count = count;
        this.validBlocks = validBlocks;
    }

    /**
     * Scans the vertical column of each input position and produces a stream of selected surface positions.
     * <p>
     * For every incoming X/Z coordinate, this method performs a full vertical scan from the
     * world's minimum height to its maximum height. It identifies air blocks situated
     * immediately above valid "ground" blocks. If any such layers are found, it randomly
     * selects a number of them equal to the configured count.
     *
     * @param context   The current {@link PlacementContext}.
     * @param positions The incoming stream of potential placement vectors.
     * @return A flattened stream of vectors representing the selected vertical layers.
     */
    @Override
    public Stream<Vector> getPositions(PlacementContext context, Stream<Vector> positions) {
        return positions.flatMap(pos -> {
            List<Vector> valid = new ArrayList<>();
            int x = pos.getBlockX();
            int z = pos.getBlockZ();

            for (int y = context.getMinBuildHeight() + 1; y < context.getHeight() - 1; y++) {
                Location loc = new Location(context.level().getWorld(), x, y, z);

                if (context.level().getType(x, y, z) == Material.AIR || context.level().getType(x, y, z) == Material.CAVE_AIR) {
                    Location below = loc.clone().add(0, -1, 0);
                    if (WorldGenUtils.isValidBlock(context.level(), below, validBlocks)) {
                        valid.add(new Vector(x, y, z));
                    }
                }
            }

            if (valid.isEmpty()) return Stream.empty();

            List<Vector> result = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                result.add(valid.get(context.random().nextInt(valid.size())));
            }
            return result.stream();
        });
    }

    /**
     * Retrieves the specific type definition for this modifier.
     *
     * @return The {@link PlacementModifierType} associated with {@link CountMultilayerModifier}.
     */
    @Override
    public PlacementModifierType<?> getType() {
        return TYPE;
    }
}