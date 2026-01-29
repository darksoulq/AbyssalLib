package com.github.darksoulq.abyssallib.world.data.loot.function;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.world.data.loot.LootContext;
import com.github.darksoulq.abyssallib.world.data.loot.LootFunction;
import com.github.darksoulq.abyssallib.world.data.loot.LootFunctionType;
import org.bukkit.Bukkit;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.Collections;
import java.util.Iterator;

public class FurnaceSmeltFunction extends LootFunction {
    public static final Codec<FurnaceSmeltFunction> CODEC = new Codec<>() {
        @Override
        public <D> FurnaceSmeltFunction decode(DynamicOps<D> ops, D input) {
            return new FurnaceSmeltFunction();
        }

        @Override
        public <D> D encode(DynamicOps<D> ops, FurnaceSmeltFunction value) {
            return ops.createMap(Collections.emptyMap());
        }
    };

    public static final LootFunctionType<FurnaceSmeltFunction> TYPE = () -> CODEC;

    @Override
    public ItemStack apply(ItemStack stack, LootContext context) {
        Iterator<Recipe> iter = Bukkit.recipeIterator();
        while (iter.hasNext()) {
            Recipe r = iter.next();
            if (r instanceof FurnaceRecipe fr) {
                if (fr.getInput().getType() == stack.getType()) {
                    ItemStack result = fr.getResult().clone();
                    result.setAmount(stack.getAmount());
                    return result;
                }
            }
        }
        return stack;
    }

    @Override
    public LootFunctionType<?> getType() {
        return TYPE;
    }
}