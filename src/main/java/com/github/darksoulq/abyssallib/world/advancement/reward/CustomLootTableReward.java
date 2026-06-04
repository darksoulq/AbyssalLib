package com.github.darksoulq.abyssallib.world.advancement.reward;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.serialization.Codecs;
import com.github.darksoulq.abyssallib.common.serialization.DataResult;
import com.github.darksoulq.abyssallib.common.serialization.DynamicOps;
import com.github.darksoulq.abyssallib.common.serialization.RecordBuilder;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.data.loot.LootContext;
import com.github.darksoulq.abyssallib.world.data.loot.LootTable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * An advancement reward that generates and distributes items using a custom
 * {@link LootTable} defined in AbyssalLib's registry or inline.
 */
public class CustomLootTableReward implements AdvancementReward {

    /**
     * The inline codec for evaluating a completely customized loot table object explicitly.
     */
    private static final Codec<CustomLootTableReward> INLINE_CODEC = RecordBuilder.create(instance -> instance.group(
        LootTable.CODEC.fieldOf("table").forGetter(CustomLootTableReward.class, p -> p.table)
    ).apply(instance, table -> new CustomLootTableReward(null, table))).describe("InlineCustomLootTableReward");

    /**
     * The reference codec evaluating a simple string ID against the central registry.
     */
    private static final Codec<CustomLootTableReward> REFERENCE_CODEC = Codecs.STRING.flatXmap(
        idStr -> DataResult.success(new CustomLootTableReward(idStr, null)),
        reward -> {
            if (reward.id != null) return DataResult.success(reward.id);
            return DataResult.error("Cannot encode inline table as reference string");
        }
    ).describe("ReferenceCustomLootTableReward");

    /**
     * The hybrid fallback codec handling both explicit map definitions and simple string references.
     */
    public static final Codec<CustomLootTableReward> CODEC = Codec.fallback(REFERENCE_CODEC, INLINE_CODEC).describe("CustomLootTableReward");

    /**
     * The registered type definition for the custom loot table reward.
     */
    public static final RewardType<CustomLootTableReward> TYPE = () -> CODEC;

    private final String id;
    private final LootTable table;

    /**
     * Constructs a new CustomLootTableReward.
     *
     * @param id    The registry identifier of the loot table (nullable if inline).
     * @param table The literal inline loot table (nullable if referenced).
     */
    public CustomLootTableReward(String id, LootTable table) {
        this.id = id;
        this.table = table;
    }

    @Override
    public RewardType<?> getType() {
        return TYPE;
    }

    /**
     * Generates loot using the configured table and grants it to the player.
     *
     * @param player The player receiving the reward.
     */
    @Override
    public void grant(Player player) {
        LootTable targetTable = this.table;
        if (targetTable == null && this.id != null) {
            targetTable = Registries.LOOT_TABLES.get(this.id);
        }

        if (targetTable != null) {
            LootContext context = LootContext.builder(player.getLocation()).looter(player).build();
            List<ItemStack> items = targetTable.generate(context);
            for (ItemStack item : items) {
                if (item != null && !item.getType().isAir()) {
                    player.getInventory().addItem(item).values().forEach(remaining ->
                        player.getWorld().dropItem(player.getLocation(), remaining)
                    );
                }
            }
        }
    }
}