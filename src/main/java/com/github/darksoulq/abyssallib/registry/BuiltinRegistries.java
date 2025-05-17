package com.github.darksoulq.abyssallib.registry;

import com.github.darksoulq.abyssallib.block.Block;
import com.github.darksoulq.abyssallib.item.Item;
import com.github.darksoulq.abyssallib.loot.LootTable;
import com.github.darksoulq.abyssallib.recipe.CustomRecipe;
import com.github.darksoulq.abyssallib.recipe.Recipe;
import com.github.darksoulq.abyssallib.tag.BlockTag;
import com.github.darksoulq.abyssallib.tag.ItemTag;

public class BuiltinRegistries {

    public static final Registry<Item> ITEMS = new Registry<>();
    public static final Registry<Block> BLOCKS = new Registry<>();
    public static final Registry<Block> BLOCK_ITEMS = new Registry<>();

    public static final Registry<LootTable> LOOT_TABLES = new Registry<>();
    public static final Registry<Recipe> RECIPES = new Registry<>();
    public static final Registry<CustomRecipe> CUSTOM_RECIPES = new Registry<>();

    public static final Registry<ItemTag> ITEM_TAGS = new Registry<>();
    public static final Registry<BlockTag> BLOCK_TAGS = new Registry<>();
}
