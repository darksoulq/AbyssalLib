package me.darksoul.abyssallib.registry;

import me.darksoul.abyssallib.block.Block;
import me.darksoul.abyssallib.item.Item;
import me.darksoul.abyssallib.loot.LootTable;
import me.darksoul.abyssallib.recipe.CustomRecipe;
import me.darksoul.abyssallib.recipe.Recipe;
import me.darksoul.abyssallib.tags.TagRegistry;

public class BuiltinRegistries {

    public static final Registry<Item> ITEMS = new Registry<>();
    public static final Registry<Block> BLOCKS = new Registry<>();

    public static final Registry<LootTable> LOOT_TABLES = new Registry<>();
    public static final Registry<Recipe> RECIPES = new Registry<>();
    public static final Registry<CustomRecipe> CUSTOM_RECIPES = new Registry<>();

    public static final TagRegistry<Item> ITEM_TAGS = new TagRegistry<>();
}
