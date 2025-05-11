package me.darksoul.abyssallib.loot;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LootTable {
    private final List<LootPool> pools = new ArrayList<>();

    public LootTable addPool(LootPool pool) {
        pools.add(pool);
        return this;
    }

    public List<ItemStack> generateLoot(Random random) {
        List<ItemStack> loot = new ArrayList<>();
        for (LootPool pool : pools) {
            loot.addAll(pool.generateLoot(random));
        }
        return loot;
    }
}
