package io.github.darksoulq.abyssalLib.loot;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LootPool {
    private final List<LootEntry> entries = new ArrayList<>();
    private int rolls = 1;

    public LootPool rolls(int amount) {
        this.rolls = amount;
        return this;
    }

    public LootPool addEntry(LootEntry entry) {
        entries.add(entry);
        return this;
    }

    public List<ItemStack> generateLoot(Random random) {
        List<ItemStack> results = new ArrayList<>();
        for (int i = 0; i < rolls; i++) {
            for (LootEntry entry : entries) {
                if (entry.shouldDrop(random)) {
                    results.add(entry.getDrop(random));
                }
            }
        }
        return results;
    }
}
