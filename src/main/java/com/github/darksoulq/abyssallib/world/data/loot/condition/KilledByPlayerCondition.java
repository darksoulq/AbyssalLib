package com.github.darksoulq.abyssallib.world.data.loot.condition;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.world.data.loot.LootCondition;
import com.github.darksoulq.abyssallib.world.data.loot.LootConditionType;
import com.github.darksoulq.abyssallib.world.data.loot.LootContext;
import org.bukkit.entity.Player;

import java.util.Collections;

/**
 * A loot condition that evaluates to true only if the victim was killed by a player.
 * <p>
 * This is commonly used for mob loot tables to ensure that certain rare drops
 * only occur when a player is actively involved in the combat, preventing
 * automated mob farms from obtaining specific items.
 * </p>
 */
public class KilledByPlayerCondition extends LootCondition {

    /**
     * The codec used for serializing and deserializing the killed by player condition.
     * <p>
     * Since this condition logic is static and requires no unique parameters,
     * it encodes to and decodes from an empty map structure.
     * </p>
     */
    public static final Codec<KilledByPlayerCondition> CODEC = new Codec<>() {
        /**
         * Decodes a KilledByPlayerCondition instance.
         * * @param ops   The {@link DynamicOps} instance defining the data format.
         * @param input The serialized input data.
         * @param <D>   The type of the data being processed.
         * @return A new instance of {@link KilledByPlayerCondition}.
         */
        @Override
        public <D> KilledByPlayerCondition decode(DynamicOps<D> ops, D input) {
            return new KilledByPlayerCondition();
        }

        /**
         * Encodes the KilledByPlayerCondition instance.
         * * @param ops   The {@link DynamicOps} instance defining the data format.
         * @param value The condition instance to encode.
         * @param <D>   The type of the data being processed.
         * @return An empty map representing the encoded state.
         */
        @Override
        public <D> D encode(DynamicOps<D> ops, KilledByPlayerCondition value) {
            return ops.createMap(Collections.emptyMap());
        }
    };

    /**
     * The registered type definition for the killed by player loot condition.
     */
    public static final LootConditionType<KilledByPlayerCondition> TYPE = () -> CODEC;

    /**
     * Tests whether the killer in the provided context is an instance of a {@link Player}.
     * * @param context The {@link LootContext} providing the entity data for the kill event.
     * @return {@code true} if the killer is a player; {@code false} otherwise.
     */
    @Override
    public boolean test(LootContext context) {
        return context.killer() instanceof Player;
    }

    /**
     * Retrieves the specific type definition for this loot condition.
     * * @return The {@link LootConditionType} associated with {@link KilledByPlayerCondition}.
     */
    @Override
    public LootConditionType<?> getType() {
        return TYPE;
    }
}