package com.github.darksoulq.abyssallib.world.data.loot;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Consumer;

public abstract class LootEntry {
    protected final int weight;
    protected final int quality;
    protected final List<LootCondition> conditions;
    protected final List<LootFunction> functions;

    public LootEntry(int weight, int quality, List<LootCondition> conditions, List<LootFunction> functions) {
        this.weight = weight;
        this.quality = quality;
        this.conditions = conditions;
        this.functions = functions;
    }

    public boolean test(LootContext context) {
        for (LootCondition condition : conditions) {
            if (!condition.test(context)) return false;
        }
        return true;
    }

    public abstract void expand(LootContext context, Consumer<ItemStack> generator);

    protected ItemStack applyFunctions(ItemStack stack, LootContext context) {
        ItemStack result = stack;
        for (LootFunction function : functions) {
            result = function.apply(result, context);
        }
        return result;
    }

    public static final Codec<LootEntry> CODEC = new Codec<>() {
        @Override
        public <D> LootEntry decode(DynamicOps<D> ops, D input) throws CodecException {
            Map<D, D> map = ops.getMap(input).orElseThrow(() -> new CodecException("Expected map"));
            String type = ops.getStringValue(map.get(ops.createString("type"))).orElse("item");
            int weight = Codecs.INT.orElse(1).decode(ops, map.get(ops.createString("weight")));
            int quality = Codecs.INT.orElse(0).decode(ops, map.get(ops.createString("quality")));
            
            List<LootCondition> conditions = new ArrayList<>();
            if (map.containsKey(ops.createString("conditions"))) {
                conditions = LootCondition.CODEC.list().decode(ops, map.get(ops.createString("conditions")));
            }
            
            List<LootFunction> functions = new ArrayList<>();
            if (map.containsKey(ops.createString("functions"))) {
                functions = LootFunction.CODEC.list().decode(ops, map.get(ops.createString("functions")));
            }

            if (type.equals("empty")) {
                return new EmptyEntry(weight, quality, conditions, functions);
            } else {
                ItemStack stack = Codecs.ITEM_STACK.decode(ops, map.get(ops.createString("name"))); 
                return new ItemEntry(stack, weight, quality, conditions, functions);
            }
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, LootEntry value) throws CodecException {
            Map<D, D> map = new HashMap<>();
            map.put(ops.createString("weight"), Codecs.INT.encode(ops, value.weight));
            map.put(ops.createString("quality"), Codecs.INT.encode(ops, value.quality));
            map.put(ops.createString("conditions"), LootCondition.CODEC.list().encode(ops, value.conditions));
            map.put(ops.createString("functions"), LootFunction.CODEC.list().encode(ops, value.functions));
            
            if (value instanceof EmptyEntry) {
                map.put(ops.createString("type"), ops.createString("empty"));
            } else if (value instanceof ItemEntry itemEntry) {
                map.put(ops.createString("type"), ops.createString("item"));
                map.put(ops.createString("name"), Codecs.ITEM_STACK.encode(ops, itemEntry.stack));
            }
            return ops.createMap(map);
        }
    };

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

    public static class EmptyEntry extends LootEntry {
        public EmptyEntry(int weight, int quality, List<LootCondition> conditions, List<LootFunction> functions) {
            super(weight, quality, conditions, functions);
        }

        @Override
        public void expand(LootContext context, Consumer<ItemStack> generator) {}
    }
}