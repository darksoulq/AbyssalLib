package com.github.darksoulq.abyssallib.loot;

import org.bukkit.inventory.ItemStack;

import java.util.Random;

public interface LootEntry {
    boolean shouldDrop(Random random);
    ItemStack getDrop(Random random);
}
