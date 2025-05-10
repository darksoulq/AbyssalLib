package io.github.darksoulq.abyssalLib.registry;

import io.github.darksoulq.abyssalLib.block.Block;
import io.github.darksoulq.abyssalLib.item.Item;
import io.github.darksoulq.abyssalLib.loot.LootTable;
import io.github.darksoulq.abyssalLib.recipe.CustomRecipe;
import io.github.darksoulq.abyssalLib.recipe.Recipe;
import io.github.darksoulq.abyssalLib.tags.TagRegistry;

public class BuiltinRegistries {

    public static final Registry<Item> ITEMS = new Registry<>();
    public static final Registry<Block> BLOCKS = new Registry<>();

    public static final Registry<LootTable> LOOT_TABLES = new Registry<>();
    public static final Registry<Recipe> RECIPES = new Registry<>();
    public static final Registry<CustomRecipe> CUSTOM_RECIPES = new Registry<>();

    public static final TagRegistry<Item> ITEM_TAGS = new TagRegistry<>();
}
