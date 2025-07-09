package com.github.darksoulq.abyssallib.server.registry;

import com.github.darksoulq.abyssallib.world.level.block.Block;
import com.github.darksoulq.abyssallib.world.level.data.loot.LootTable;
import com.github.darksoulq.abyssallib.world.level.data.tag.BlockTag;
import com.github.darksoulq.abyssallib.world.level.data.tag.ItemTag;
import com.github.darksoulq.abyssallib.world.level.entity.DamageType;
import com.github.darksoulq.abyssallib.world.level.entity.Entity;
import com.github.darksoulq.abyssallib.world.level.item.Item;
import org.bukkit.entity.LivingEntity;

public class BuiltinRegistries {

    public static final Registry<Item> ITEMS = new Registry<>(null);
    public static final Registry<Block> BLOCKS = new Registry<>(null);
    public static final Registry<Block> BLOCK_ITEMS = new Registry<>(null);
    public static final Registry<Entity<? extends LivingEntity>> ENTITIES = new Registry<>(null);

    public static final Registry<LootTable> LOOT_TABLES = new Registry<>(null);
    public static final Registry<DamageType> DAMAGE_TYPES = new Registry<>(null);

    public static final Registry<ItemTag> ITEM_TAGS = new Registry<>(null);
    public static final Registry<BlockTag> BLOCK_TAGS = new Registry<>(null);
}
