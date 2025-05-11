package com.github.darksoulq.abyssallib.loot;

import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class ItemLootEntry implements LootEntry {
    private final ItemStack item;
    private final double chance;

    public ItemLootEntry(ItemStack item, double chance) {
        this.item = item;
        this.chance = chance;
    }

    @Override
    public boolean shouldDrop(Random random) {
        return random.nextDouble() < chance;
    }

    @Override
    public ItemStack getDrop(Random random) {
        return item.clone();
    }
}
