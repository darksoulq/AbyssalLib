package com.github.darksoulq.abyssallib.world.data.loot.condition;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.world.data.loot.LootCondition;
import com.github.darksoulq.abyssallib.world.data.loot.LootConditionType;
import com.github.darksoulq.abyssallib.world.data.loot.LootContext;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * A loot condition that evaluates to true if the tool used in the generation context
 * matches a specific material.
 * <p>
 * This is primarily used for block-break loot tables where specific items should
 * only drop when harvested with a specific tool (e.g., Shears for leaves).
 * </p>
 */
public class MatchToolCondition extends LootCondition {

    /**
     * The codec used for serializing and deserializing the match tool condition.
     * <p>
     * It maps the "item" field to a string representation of a {@link Material}.
     * </p>
     */
    public static final Codec<MatchToolCondition> CODEC = new Codec<>() {
        /**
         * Decodes a MatchToolCondition instance from the provided data.
         *
         * @param ops   The {@link DynamicOps} instance defining the data format.
         * @param input The serialized input data.
         * @param <D>   The type of the data being processed.
         * @return A new instance of {@link MatchToolCondition}.
         * @throws CodecException If the "item" field is missing or the material is unknown.
         */
        @Override
        public <D> MatchToolCondition decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
            String matName = Codecs.STRING.decode(ops, map.get(ops.createString("item")));
            Material material = Material.matchMaterial(matName);
            if (material == null) throw new CodecException("Unknown material: " + matName);
            return new MatchToolCondition(material);
        }

        /**
         * Encodes the MatchToolCondition instance into a serialized format.
         *
         * @param ops   The {@link DynamicOps} instance defining the data format.
         * @param value The condition instance to encode.
         * @param <D>   The type of the data being processed.
         * @return A map representing the encoded material name.
         * @throws CodecException If the encoding process fails.
         */
        @Override
        public <D> D encode(DynamicOps<D> ops, MatchToolCondition value) throws CodecException {
            Map<D, D> map = new HashMap<>();
            map.put(ops.createString("item"), Codecs.STRING.encode(ops, value.item.name()));
            return ops.createMap(map);
        }
    };

    /**
     * The registered type definition for the match tool loot condition.
     */
    public static final LootConditionType<MatchToolCondition> TYPE = () -> CODEC;

    /** The {@link Material} required for the tool to satisfy this condition. */
    private final Material item;

    /**
     * Constructs a new MatchToolCondition for the specified material.
     *
     * @param item The {@link Material} to match against the context tool.
     */
    public MatchToolCondition(Material item) {
        this.item = item;
    }

    /**
     * Tests whether the tool in the provided {@link LootContext} matches the required material.
     *
     * @param context The {@link LootContext} providing the tool {@link ItemStack}.
     * @return {@code true} if the tool is present and matches the material; {@code false} otherwise.
     */
    @Override
    public boolean test(LootContext context) {
        ItemStack tool = context.tool();
        return tool != null && tool.getType() == item;
    }

    /**
     * Retrieves the specific type definition for this loot condition.
     *
     * @return The {@link LootConditionType} associated with {@link MatchToolCondition}.
     */
    @Override
    public LootConditionType<?> getType() {
        return TYPE;
    }
}