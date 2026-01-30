package com.github.darksoulq.abyssallib.world.data.loot;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Consumer;

/**
 * A pool of potential loot entries within a table.
 * <p>
 * A pool determines how many "rolls" it performs. During each roll, weights are
 * calculated for valid entries to determine which item is selected.
 * @param rolls       The base number of times to roll this pool.
 * @param bonusRolls  Additional rolls scaled by the {@link LootContext#luck()} value.
 * @param entries     The list of {@link LootEntry} objects to choose from.
 * @param conditions  A list of {@link LootCondition}s that must pass for the pool to execute.
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

        int totalRolls = rolls + (int)(bonusRolls * context.luck());

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

    /** Codec for serializing and deserializing {@link LootPool} instances. */
    public static final Codec<LootPool> CODEC = new Codec<>() {
        @Override
        public <D> LootPool decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
            int rolls = Codecs.INT.decode(ops, map.get(ops.createString("rolls")));
            int bonus = Codecs.INT.orElse(0).decode(ops, map.get(ops.createString("bonus_rolls")));
            List<LootEntry> entries = LootEntry.CODEC.list().decode(ops, map.get(ops.createString("entries")));
            List<LootCondition> conditions = new ArrayList<>();
            if (map.containsKey(ops.createString("conditions"))) {
                conditions = LootCondition.CODEC.list().decode(ops, map.get(ops.createString("conditions")));
            }
            return new LootPool(rolls, bonus, entries, conditions);
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, LootPool value) throws CodecException {
            Map<D, D> map = new HashMap<>();
            map.put(ops.createString("rolls"), Codecs.INT.encode(ops, value.rolls));
            map.put(ops.createString("bonus_rolls"), Codecs.INT.encode(ops, value.bonusRolls));
            map.put(ops.createString("entries"), LootEntry.CODEC.list().encode(ops, value.entries));
            map.put(ops.createString("conditions"), LootCondition.CODEC.list().encode(ops, value.conditions));
            return ops.createMap(map);
        }
    };
}