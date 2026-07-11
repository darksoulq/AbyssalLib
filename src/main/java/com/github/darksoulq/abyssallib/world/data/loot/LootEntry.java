package com.github.darksoulq.abyssallib.world.data.loot;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.RecordBuilder;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * An abstract representation of a single choice within a loot pool.
 * <p>
 * Entries can represent items, empty slots, or recursive loot tables.
 */
public abstract class LootEntry {
    /**
     * The relative probability weight of this entry.
     */
    protected final int weight;
    /**
     * The quality modifier applied when luck is involved.
     */
    protected final int quality;
    /**
     * Conditions that must be met for this specific entry to be valid.
     */
    protected final List<LootCondition> conditions;
    /**
     * Functions applied to the generated item (e.g., setting amount or enchantments).
     */
    protected final List<LootFunction> functions;

    /**
     * @param weight     Entry weight.
     * @param quality    Entry quality.
     * @param conditions Activation conditions.
     * @param functions  Item modifiers.
     */
    public LootEntry(int weight, int quality, List<LootCondition> conditions, List<LootFunction> functions) {
        this.weight = weight;
        this.quality = quality;
        this.conditions = conditions;
        this.functions = functions;
    }

    /**
     * Tests if this entry can be selected in the given context.
     *
     * @param context The current {@link LootContext}.
     * @return {@code true} if all conditions pass.
     */
    public boolean test(LootContext context) {
        for (LootCondition condition : conditions) {
            if (!condition.test(context)) return false;
        }
        return true;
    }

    /**
     * Generates the actual content of the entry and passes it to the sink.
     *
     * @param context   The current {@link LootContext}.
     * @param generator The {@link Consumer} for generated stacks.
     */
    public abstract void expand(LootContext context, Consumer<ItemStack> generator);

    /**
     * Applies the assigned functions to a generated item stack.
     *
     * @param stack   The original {@link ItemStack}.
     * @param context The current {@link LootContext}.
     * @return The modified {@link ItemStack}.
     */
    protected ItemStack applyFunctions(ItemStack stack, LootContext context) {
        ItemStack result = stack;
        for (LootFunction function : functions) {
            result = function.apply(result, context);
        }
        return result;
    }

    private static final Codec<ItemEntry> ITEM_ENTRY_CODEC = RecordBuilder.create(instance -> instance.group(
        Codecs.ITEM_STACK.fieldOf("name").forGetter(ItemEntry.class, e -> e.stack),
        Codecs.INT.optionalFieldOf("weight", 1).forGetter(ItemEntry.class, e -> e.weight),
        Codecs.INT.optionalFieldOf("quality", 0).forGetter(ItemEntry.class, e -> e.quality),
        LootCondition.CODEC.list().optionalFieldOf("conditions", Collections.emptyList()).forGetter(ItemEntry.class, e -> e.conditions),
        LootFunction.CODEC.list().optionalFieldOf("functions", Collections.emptyList()).forGetter(ItemEntry.class, e -> e.functions)
    ).apply(instance, ItemEntry::new)).describe("ItemEntry");

    private static final Codec<EmptyEntry> EMPTY_ENTRY_CODEC = RecordBuilder.create(instance -> instance.group(
        Codecs.INT.optionalFieldOf("weight", 1).forGetter(EmptyEntry.class, e -> e.weight),
        Codecs.INT.optionalFieldOf("quality", 0).forGetter(EmptyEntry.class, e -> e.quality),
        LootCondition.CODEC.list().optionalFieldOf("conditions", Collections.emptyList()).forGetter(EmptyEntry.class, e -> e.conditions),
        LootFunction.CODEC.list().optionalFieldOf("functions", Collections.emptyList()).forGetter(EmptyEntry.class, e -> e.functions)
    ).apply(instance, EmptyEntry::new)).describe("EmptyEntry");

    /**
     * Polymorphic codec for handling various {@link LootEntry} implementations.
     */
    public static final Codec<LootEntry> CODEC = Codec.dispatch(
        LootEntry.class,
        "type",
        Codecs.STRING,
        entry -> entry instanceof EmptyEntry ? "empty" : "item",
        type -> type.equals("empty") ? EMPTY_ENTRY_CODEC.unchecked() : ITEM_ENTRY_CODEC.unchecked()
    ).describe("LootEntry");

    /**
     * A loot entry that generates a specific item stack.
     */
    public static class ItemEntry extends LootEntry {
        private final ItemStack stack;

        public ItemEntry(ItemStack stack, int weight, int quality, List<LootCondition> conditions, List<LootFunction> functions) {
            super(weight, quality, conditions, functions);
            this.stack = stack;
        }

        @Override
        public void expand(LootContext context, Consumer<ItemStack> generator) {
            generator.accept(applyFunctions(stack.clone(), context));
        }
    }

    /**
     * A loot entry that represents a "no drop" chance.
     */
    public static class EmptyEntry extends LootEntry {
        public EmptyEntry(int weight, int quality, List<LootCondition> conditions, List<LootFunction> functions) {
            super(weight, quality, conditions, functions);
        }

        @Override
        public void expand(LootContext context, Consumer<ItemStack> generator) {
        }
    }
}