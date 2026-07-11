package com.github.darksoulq.abyssallib.world.data.loot;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.RecordBuilder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * A pool of potential loot entries within a table.
 * <p>
 * A pool determines how many "rolls" it performs. During each roll, weights are
 * calculated for valid entries to determine which item is selected.
 *
 * @param rolls      The base number of times to roll this pool.
 * @param bonusRolls Additional rolls scaled by the {@link LootContext#luck()} value.
 * @param entries    The list of {@link LootEntry} objects to choose from.
 * @param conditions A list of {@link LootCondition}s that must pass for the pool to execute.
 */
public record LootPool(int rolls, int bonusRolls, List<LootEntry> entries, List<LootCondition> conditions) {

    /**
     * Executes the logic to generate loot from this pool.
     *
     * @param context The {@link LootContext} used for conditions and random selection.
     * @param sink    A {@link Consumer} that accepts the generated {@link ItemStack}s.
     */
    public void generate(LootContext context, Consumer<ItemStack> sink) {
        for (LootCondition condition : conditions) {
            if (!condition.test(context)) return;
        }

        int totalRolls = rolls + (int) (bonusRolls * context.luck());

        for (int i = 0; i < totalRolls; i++) {
            List<LootEntry> valid = new ArrayList<>();
            int totalWeight = 0;

            for (LootEntry entry : entries) {
                if (entry.test(context)) {
                    valid.add(entry);
                    totalWeight += entry.weight;
                }
            }

            if (valid.isEmpty()) continue;

            int pick = context.random().nextInt(totalWeight);
            int current = 0;

            for (LootEntry entry : valid) {
                current += entry.weight;
                if (current > pick) {
                    entry.expand(context, sink);
                    break;
                }
            }
        }
    }

    /**
     * Codec for serializing and deserializing {@link LootPool} instances.
     */
    public static final Codec<LootPool> CODEC = RecordBuilder.create(instance -> instance.group(
        Codecs.INT.fieldOf("rolls").forGetter(LootPool.class, LootPool::rolls),
        Codecs.INT.optionalFieldOf("bonus_rolls", 0).forGetter(LootPool.class, LootPool::bonusRolls),
        LootEntry.CODEC.list().fieldOf("entries").forGetter(LootPool.class, LootPool::entries),
        LootCondition.CODEC.list().optionalFieldOf("conditions", Collections.emptyList()).forGetter(LootPool.class, LootPool::conditions)
    ).apply(instance, LootPool::new)).describe("LootPool");
}