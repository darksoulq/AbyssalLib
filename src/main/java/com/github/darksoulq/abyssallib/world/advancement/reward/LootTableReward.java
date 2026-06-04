package com.github.darksoulq.abyssallib.world.advancement.reward;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;

import java.util.Collection;

/**
 * An advancement reward that hooks into vanilla Minecraft's default loot tables.
 */
public class LootTableReward implements AdvancementReward {

    /**
     * The codec used for serializing and deserializing the vanilla loot table reward.
     */
    public static final Codec<LootTableReward> CODEC = Codecs.NAMESPACED_KEY.flatXmap(
        key -> {
            LootTable table = Bukkit.getLootTable(key);
            if (table == null) {
                return DataResult.error("Unknown vanilla loot table reference: " + key);
            }
            return DataResult.success(new LootTableReward(table));
        },
        reward -> {
            if (reward.table == null) {
                return DataResult.error("Invalid internal state: null table reference");
            }
            return DataResult.success(reward.table.getKey());
        }
    ).describe("LootTableReward");

    /**
     * The registered type definition for the vanilla loot table reward.
     */
    public static final RewardType<LootTableReward> TYPE = () -> CODEC;

    private final LootTable table;

    /**
     * Constructs a new LootTableReward.
     *
     * @param table The vanilla loot table to evaluate.
     */
    public LootTableReward(LootTable table) {
        this.table = table;
    }

    @Override
    public RewardType<?> getType() {
        return TYPE;
    }

    /**
     * Generates loot using the vanilla table and grants it to the player.
     *
     * @param player The player receiving the reward.
     */
    @Override
    public void grant(Player player) {
        if (table == null) return;
        LootContext context = new LootContext.Builder(player.getLocation()).lootedEntity(player).build();
        Collection<ItemStack> items = table.populateLoot(new java.util.Random(), context);
        for (ItemStack item : items) {
            player.getInventory().addItem(item).values().forEach(remaining ->
                player.getWorld().dropItem(player.getLocation(), remaining)
            );
        }
    }
}