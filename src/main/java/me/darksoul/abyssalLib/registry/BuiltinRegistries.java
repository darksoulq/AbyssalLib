package me.darksoul.abyssalLib.registry;

import me.darksoul.abyssalLib.block.Block;
import me.darksoul.abyssalLib.item.Item;
import me.darksoul.abyssalLib.loot.LootTable;
import me.darksoul.abyssalLib.recipe.CustomRecipe;
import me.darksoul.abyssalLib.recipe.Recipe;
import me.darksoul.abyssalLib.tags.TagRegistry;

public class BuiltinRegistries {

    public static final Registry<Item> ITEMS = new Registry<>();
    public static final Registry<Block> BLOCKS = new Registry<>();

    public static final Registry<LootTable> LOOT_TABLES = new Registry<>();
    public static final Registry<Recipe> RECIPES = new Registry<>();
    public static final Registry<CustomRecipe> CUSTOM_RECIPES = new Registry<>();

    public static final TagRegistry<Item> ITEM_TAGS = new TagRegistry<>();
}
