package com.github.darksoulq.abyssallib.registry;

import com.github.darksoulq.abyssallib.block.Block;
import com.github.darksoulq.abyssallib.item.Item;
import com.github.darksoulq.abyssallib.loot.LootTable;
import com.github.darksoulq.abyssallib.recipe.CustomRecipe;
import com.github.darksoulq.abyssallib.recipe.Recipe;
import com.github.darksoulq.abyssallib.tags.TagRegistry;

public class BuiltinRegistries {

    public static final Registry<Item> ITEMS = new Registry<>();
    public static final Registry<Block> BLOCKS = new Registry<>();

    public static final Registry<LootTable> LOOT_TABLES = new Registry<>();
    public static final Registry<Recipe> RECIPES = new Registry<>();
    public static final Registry<CustomRecipe> CUSTOM_RECIPES = new Registry<>();

    public static final TagRegistry<Item> ITEM_TAGS = new TagRegistry<>();
}
