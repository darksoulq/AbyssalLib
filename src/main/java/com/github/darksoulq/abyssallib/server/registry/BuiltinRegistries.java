package com.github.darksoulq.abyssallib.server.registry;

import com.github.darksoulq.abyssallib.world.level.block.Block;
import com.github.darksoulq.abyssallib.world.level.data.loot.LootTable;
import com.github.darksoulq.abyssallib.world.level.data.tag.BlockTag;
import com.github.darksoulq.abyssallib.world.level.data.tag.ItemTag;
import com.github.darksoulq.abyssallib.world.level.entity.DamageType;
import com.github.darksoulq.abyssallib.world.level.inventory.recipe.CustomRecipe;
import com.github.darksoulq.abyssallib.world.level.inventory.recipe.Recipe;
import com.github.darksoulq.abyssallib.world.level.item.Item;

public class BuiltinRegistries {

    public static final Registry<Item> ITEMS = new Registry<>(null);
    public static final Registry<Block> BLOCKS = new Registry<>(null);
    public static final Registry<Block> BLOCK_ITEMS = new Registry<>(null);

    public static final Registry<LootTable> LOOT_TABLES = new Registry<>(null);
    public static final Registry<Recipe> RECIPES = new Registry<>(null);
    public static final Registry<CustomRecipe> CUSTOM_RECIPES = new Registry<>(null);
    public static final Registry<DamageType> DAMAGE_TYPES = new Registry<>(null);

    public static final Registry<ItemTag> ITEM_TAGS = new Registry<>(null);
    public static final Registry<BlockTag> BLOCK_TAGS = new Registry<>(null);
}
