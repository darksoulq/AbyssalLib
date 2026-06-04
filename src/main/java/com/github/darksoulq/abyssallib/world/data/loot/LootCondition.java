package com.github.darksoulq.abyssallib.world.data.loot;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.server.registry.Registries;

/**
 * An abstract base class for logic gates that determine if a loot element should be processed.
 * <p>
 * LootConditions can be applied to both {@link LootPool}s and {@link LootEntry}s. If a
 * condition evaluates to {@code false}, the associated pool or entry is skipped entirely
 * for the current generation cycle.
 * </p>
 */
public abstract class LootCondition {

    /**
     * Evaluates this condition against the provided loot context.
     *
     * @param context The {@link LootContext} containing environment and entity data.
     * @return {@code true} if the condition is met; {@code false} otherwise.
     */
    public abstract boolean test(LootContext context);

    /**
     * Retrieves the specific type definition associated with this condition instance.
     *
     * @return The {@link LootConditionType} used for registry identification and serialization.
     */
    public abstract LootConditionType<?> getType();

    /**
     * Polymorphic codec for serializing and deserializing loot conditions.
     * <p>
     * Decodes the condition by identifying the "type" field via {@link Registries#LOOT_CONDITIONS}
     * and delegating to the specific type's internal codec.
     */
    public static final Codec<LootCondition> CODEC = Codec.dispatch(
        LootCondition.class,
        "type",
        Codecs.STRING,
        condition -> {
            String typeId = Registries.LOOT_CONDITIONS.getId(condition.getType());
            if (typeId == null) {
                throw new IllegalStateException("Unregistered loot condition type");
            }
            return typeId;
        },
        typeId -> {
            LootConditionType<?> type = Registries.LOOT_CONDITIONS.get(typeId);
            if (type == null) {
                return Codec.error("Unknown loot condition: " + typeId);
            }
            return type.codec().unchecked();
        }
    ).describe("LootCondition");
}